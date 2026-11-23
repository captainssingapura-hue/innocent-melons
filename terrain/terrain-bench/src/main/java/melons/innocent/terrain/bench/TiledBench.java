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
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.Seen;
import melons.innocent.terrain.melon.MelonFeatures;
import melons.innocent.terrain.tiles.Choice;
import melons.innocent.terrain.tiles.Tiling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The land drawn in tiles around where the eye is: finer near the spot under it, coarser further off, built away from
 * the card's thread nearest first and drawn as they are built, each layer's look built once for every tile. The eye
 * stands where it is placed, from outside the scene only, the bench's properties at first, the console's map and its
 * slider for how high after; the mouse dragged in the card's window turns it round and tips it, and moves it not.
 * Its land is the Melon country's natural ground from its contours, seen at a scale, its features in the lab's
 * materials, plain where the lab has none.
 *
 * <p>{@code -Dbench.across=}, {@code -Dbench.along=}, {@code -Dbench.height=} (metres), {@code -Dbench.yaw=} and
 * {@code -Dbench.pitch=} (degrees) where the eye is first placed; {@code -Dbench.finest=} (metres) how near together
 * the finest tiles have their points; {@code -Dbench.near=} how many times its side a tile may be from the spot and
 * still be cut into its four; {@code -Dbench.cells=} how many cells a tile has to a side; {@code -Dbench.up=} how many
 * times as high the land is drawn; {@code -Dbench.seen=} (metres) how finely the ground is seen, 10 m at first, the
 * same for every tile. For a run with nobody watching, {@code -Dbench.settle=} so many thousandths of a
 * second ends it, once every tile wanted is drawn, and {@code -Dbench.snapshot=} a file saves the picture.
 */
public final class TiledBench {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 720;
    private static final int LEFT = 40;
    private static final int TOP = 80;
    private static final int BESIDE = 380;
    private static final int EVENED = 30;
    /** How many pixels the console's map is each way. */
    private static final int MAP = 300;
    private static final double MILLION = 1e6;
    private static final double METRE = 100;
    private static final double FIELD = 50;
    private static final double NEAR = 50;

    private final Wanted wanted = new Wanted();
    private final Laid laid = new Laid();
    private final Daylight day;
    private final LandFeatures features;
    /** The ground as the tiles are made of it: seen at one scale, so tiles of every level meet. */
    private final Ground seen;
    private final Painting painting;
    private final Choice choice;
    private final double up;
    private final Flight first;
    /** Where the eye is now, placed only from outside the scene: the card's thread's own. */
    private Flight flight;

    /** A tiled bench for {@code features}, drawn as {@code painting} says. */
    public TiledBench(final LandFeatures features, final Painting painting) {
        this.features = features;
        this.seen = new Seen(features.ground(), METRE * said("bench.seen", 10));
        this.painting = painting;
        final Daylight plain = new Daylight();
        this.day = new Daylight(plain.light().sunAt(said("bench.sun", 30), said("bench.sunRound", 225)), plain.sky());
        this.up = said("bench.up", 1);
        final Extent e = features.ground().extent();
        final Tiling tiling = new Tiling(e, (int) said("bench.cells", 32));
        this.choice = new Choice(tiling, said("bench.near", 1.5), tiling.level(METRE * said("bench.finest", 1.5)));
        this.first = new Flight(METRE * said("bench.across", 0), METRE * said("bench.along", -300),
                METRE * said("bench.height", 120), said("bench.yaw", 0), said("bench.pitch", 15), 1);
        this.flight = first;
    }

    public static void main(final String[] args) throws IOException {
        final LandFeatures features = MelonFeatures.INSTANCE.features();
        new TiledBench(features, new ByMaterials(features)).run(features.name()
                + " in tiles around the eye, in the lab's materials, plain where it has none");
    }

    /** Opened, {@code about} said of what is drawn, until a window is closed or the time asked for is up. */
    public void run(final String about) throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Tiled terrain bench, drawn on " + card.name(), WIDTH, HEIGHT);
             TiledLand land = new TiledLand(card, laid, painting, choice.tiling(), seen, up)) {
            final TiledControls controls = new TiledControls(wanted, about, painting.legend(),
                    new MapPicture(features.ground(), painting, MAP), first);
            Platform.startup(() -> controls.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, land, controls);
            snapshot(card, window, land);
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame: the tiles around the eye chosen again as it is placed, built ones put on the card. */
    private void drawn(final Desk desk, final Card card, final Window window, final TiledLand land,
                       final TiledControls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        land.want(choice.around(flight.x(), flight.z()), List.of());
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Flight before = flight;
            flight = looked(window.mouse(), wanted.raised().map(up -> new Flight(flight.x(), flight.z(), up,
                    flight.yaw(), flight.pitch(), 1)).orElse(flight));
            final double[] spot = wanted.placedAt().orElse(null);
            if (spot != null) {
                flight = new Flight(spot[0], spot[1], flight.height(), flight.yaw(), flight.pitch(), 1);
                land.want(choice.around(flight.x(), flight.z()), List.of());
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
                controls.tiles(land.shown(), land.built(), land.drawn(), land.level(true), land.level(false));
                last = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle && land.gaps() == 0) {
                return;
            }
        }
    }

    /** {@code flight} turned round and tipped as the mouse was dragged, over the same spot, as high. */
    private Flight looked(final Moved mouse, final Flight flight) {
        return mouse.dragged() ? flight.dragged(mouse.across(), mouse.down()) : flight;
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
