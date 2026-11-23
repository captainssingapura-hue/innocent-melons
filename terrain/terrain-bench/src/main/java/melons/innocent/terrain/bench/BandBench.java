package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;
import melons.innocent.terrain.Averaged;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.meadow.MeadowFeatures;
import melons.innocent.terrain.melon.MelonFeatures;
import melons.innocent.terrain.tiles.Tiling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The land seen from a height, downsampled by how high the eye is: the eye kept within a band of height, every 500 m
 * from the sea's level up ({@link Altitudes}), and the land seen from the whole band the one way, so the view stays
 * as it is wherever the eye goes within it. From each band, the land is worked out as finely as it is and then
 * averaged over cells as wide as two pixels cover looking straight down from the band's top ({@link Averaged}), its
 * slopes with it, so its relief still shows; drawn in tiles all of one level, their points a cell apart, none coarser
 * than the next, so no seam; those over what the eye can see ({@link Footprint}), the nearest first, and besides them,
 * against its turning, those it would see turned any way round; every tile built kept, as the land does not change,
 * so a view come back to, or a band, is there at once. Where one feature gives way to another is cut on the land, to
 * within a cell. The eye looks down 45 degrees at least, so all it
 * sees is about as far off as it is high. Its land is the Melon country's, or with {@code -Dbench.country=meadow}
 * the meadow round the village, in the lab's materials.
 *
 * <p>{@code -Dbench.across=}, {@code -Dbench.along=}, {@code -Dbench.height=} (metres), {@code -Dbench.yaw=} and
 * {@code -Dbench.pitch=} (degrees down) where the eye is first placed; {@code -Dbench.held=} and {@code -Dbench.card=}
 * (megabytes) how much room what is built may take, kept here, and what is put on the card, there;
 * {@code -Dbench.cells=} how many cells a tile has to a side, 64 at first, so fewer, bigger tiles go on the card. For
 * a run with
 * nobody watching, {@code -Dbench.turn=} (degrees a second) turns the eye round of itself once the first view is
 * filled, and tells at the end how many frames since had holes in the land, or old tiles standing in, and how many at
 * worst; {@code -Dbench.settle=} so many thousandths of a second ends it, once every tile wanted is drawn, and
 * {@code -Dbench.snapshot=} a file saves the picture.
 */
public final class BandBench {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 720;
    private static final int LEFT = 40;
    private static final int TOP = 40;
    private static final int BESIDE = 380;
    private static final int EVENED = 30;
    private static final int MAP = 300;
    private static final double MILLION = 1e6;
    private static final long MB = 1L << 20;
    private static final double METRE = 100;
    private static final double FIELD = 50;
    private static final double NEAR = 50;
    /** How high each band is, in centimetres. */
    private static final double STOREY = 500 * METRE;
    /** How far down the eye looks, at least, in degrees. */
    private static final double STEEPEST = 45;

    private final Wanted wanted = new Wanted();
    private final Laid laid = new Laid();
    private final Daylight day;
    private final LandFeatures features;
    private final Painting painting;
    private final Tiling tiling;
    private final Altitudes altitudes = new Altitudes(STOREY, 2 * Math.tan(Math.toRadians(FIELD) / 2) / HEIGHT);
    private final Flight first;
    /** How fast the eye turns round of itself, in degrees a second, once the first view is filled: none, at first. */
    private final double turning;
    /** Where the eye is now: the card's thread's own. */
    private Flight flight;

    /** A band bench for {@code features}, drawn as {@code painting} says. */
    public BandBench(final LandFeatures features, final Painting painting) {
        this.features = features;
        this.painting = painting;
        final Daylight plain = new Daylight();
        this.day = new Daylight(plain.light().sunAt(said("bench.sun", 30), said("bench.sunRound", 225)), plain.sky());
        this.tiling = new Tiling(features.ground().extent(), (int) said("bench.cells", 64));
        this.first = new Flight(METRE * said("bench.across", 0), METRE * said("bench.along", -1_200),
                METRE * said("bench.height", 1_200), said("bench.yaw", 0),
                Math.max(STEEPEST, said("bench.pitch", 60)), 1);
        this.flight = first;
        this.turning = said("bench.turn", 0);
    }

    public static void main(final String[] args) throws IOException {
        final LandFeatures features = "meadow".equals(System.getProperty("bench.country", "melon"))
                ? MeadowFeatures.INSTANCE.features() : MelonFeatures.INSTANCE.features();
        new BandBench(features, new ByMaterials(features)).run(features.name() + " seen from a band of height,"
                + " downsampled for it, in the lab's materials, plain where it has none");
    }

    /** Opened, {@code about} said of what is drawn, until a window is closed or the time asked for is up. */
    public void run(final String about) throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Band terrain bench, drawn on " + card.name(), WIDTH, HEIGHT)) {
            final BandControls controls = new BandControls(wanted, about, painting.legend(),
                    new MapPicture(features.ground(), painting, MAP), first, altitudes);
            Platform.startup(() -> controls.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            try (TiledLand land = new TiledLand(card, laid, painting, tiling,
                    level -> new Averaged(features.ground(), tiling.spacing(level)), 1, MB * (long) said("bench.held",
                    1_536), MB * (long) said("bench.card", 512))) {
                drawn(desk, card, window, land, controls);
                snapshot(card, window, land);
            }
        } finally {
            Platform.exit();
        }
    }

    /** The level whose points are as far apart as the land is seen from band {@code band}, or the nearest finer. */
    private int level(final int band) {
        return tiling.level(altitudes.scale(band) * Math.sqrt(2));
    }

    /**
     * Frame after frame: the eye moved as the console and the mouse say; the tiles over what it sees wanted as it
     * moves, and besides them those it would see turned any way round, against its turning; a new band's tiles
     * wanted as it comes to it, the old ones shown till the new are built.
     */
    private void drawn(final Desk desk, final Card card, final Window window, final TiledLand land,
                       final BandControls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        long previous = began;
        int frames = 0;
        Flight wantedFor = null;
        double shapedFor = 0;
        final Tally tally = new Tally();
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Flight before = flight;
            final long at = System.nanoTime();
            flight = moved(window.mouse(), tally.filled() ? turning * (at - previous) / 1e9 : 0);
            previous = at;
            final int band = altitudes.band(flight.height());
            final double shape = (double) window.width() / Math.max(1, window.height());
            if (!flight.equals(wantedFor) || shape != shapedFor) {
                final Footprint seen = new Footprint(flight, FIELD, shape);
                land.want(seen.tiles(tiling, level(band)), seen.round(tiling, level(band)));
                wantedFor = flight;
                shapedFor = shape;
            }
            if (!flight.equals(before)) {
                controls.seen(flight);
            }
            land.kept();
            frame(window, land);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            final long now = System.nanoTime();
            if (++frames % EVENED == 0) {
                controls.told((now - last) / MILLION / EVENED);
                controls.cached(land.cached());
                last = now;
            }
            tally.framed(land.gaps(), land.stale());
            if (settle >= 0 && (now - began) / MILLION >= settle && land.gaps() == 0) {
                if (turning != 0) {
                    System.out.println(tally + String.format(java.util.Locale.ROOT, "; %d tiles put on the card,"
                            + " %.2f ms each; %d frames in %.1f s", land.cached().put(), land.cached().putting(),
                            frames, (now - began) / 1e9));
                }
                return;
            }
        }
    }

    /**
     * The eye as the console and {@code mouse} have moved it: turned round and tipped as the mouse was dragged, and
     * turned {@code round} degrees more, never looking less than {@link #STEEPEST} degrees down; as high as the console
     * wants it; over the spot it wants.
     */
    private Flight moved(final Moved mouse, final double round) {
        final Flight dragged = mouse.dragged() ? flight.dragged(mouse.across(), mouse.down()) : flight;
        final Flight looked = round == 0 ? dragged : dragged.turned(round, 0);
        final double[] spot = wanted.placedAt().orElse(new double[] {looked.x(), looked.z()});
        return new Flight(spot[0], spot[1], wanted.raised().orElse(looked.height()), looked.yaw(),
                Math.max(STEEPEST, looked.pitch()), 1);
    }

    /** The picture the card draws for {@code window}, saved, if a file is asked for it. */
    private void snapshot(final Card card, final Window window, final TiledLand land) throws IOException {
        final String snapshot = System.getProperty("bench.snapshot", "");
        if (!snapshot.isEmpty()) {
            try (Canvas canvas = card.canvas(window.width(), window.height())) {
                frame(canvas, land);
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
            }
        }
    }

    /** One frame of the tiles on the card, the eye where it is, drawn on {@code target}. */
    private void frame(final Target target, final TiledLand land) {
        final Camera camera = flight.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> land.drawn(pass, flight, camera));
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
