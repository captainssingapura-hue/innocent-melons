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
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.melon.MelonFeatures;
import melons.innocent.terrain.tiles.Choice;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.Tiling;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The land drawn in tiles around where the eye is: finer near the spot under it, coarser further off, built away from
 * the card's thread nearest first and drawn as they are built, each layer's look built once for every tile. The eye
 * stands where it is placed, from outside the scene only, the bench's properties at first, the console's map and its
 * slider for how high after; the mouse dragged in the card's window turns it round and tips it, and moves it not.
 * Its land is the Melon country's, in the lab's materials, plain where the lab has none.
 *
 * <p>{@code -Dbench.across=}, {@code -Dbench.along=}, {@code -Dbench.height=} (metres), {@code -Dbench.yaw=} and
 * {@code -Dbench.pitch=} (degrees) where the eye is first placed; {@code -Dbench.finest=} (metres) how near together
 * the finest tiles have their points; {@code -Dbench.near=} how many times its side a tile may be from the spot and
 * still be cut into its four; {@code -Dbench.cells=} how many cells a tile has to a side; {@code -Dbench.up=} how many
 * times as high the land is drawn. For a run with nobody watching, {@code -Dbench.settle=} so many thousandths of a
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
    /** How many tiles are put on the card each frame, at most, so a frame is never held up long. */
    private static final int EACH_FRAME = 6;
    private static final double MILLION = 1e6;
    private static final double METRE = 100;
    private static final double FIELD = 50;
    private static final double NEAR = 50;

    private final Wanted wanted = new Wanted();
    private final Laid laid = new Laid();
    private final Daylight day;
    private final LandFeatures features;
    private final Painting painting;
    private final Choice choice;
    private final double up;
    private final Flight first;
    /** Where the eye is now, placed only from outside the scene: the card's thread's own. */
    private Flight flight;

    /** A tiled bench for {@code features}, drawn as {@code painting} says. */
    public TiledBench(final LandFeatures features, final Painting painting) {
        this.features = features;
        this.painting = painting;
        final Daylight plain = new Daylight();
        this.day = new Daylight(plain.light().sunAt(said("bench.sun", 30), said("bench.sunRound", 225)), plain.sky());
        this.up = said("bench.up", 1);
        final Extent e = features.terrain().extent();
        final Tiling tiling = new Tiling(e, (int) said("bench.cells", 32));
        this.choice = new Choice(tiling, said("bench.near", 1.5), tiling.level(METRE * said("bench.finest", 1.5)));
        this.first = new Flight(METRE * said("bench.across", 0), METRE * said("bench.along", -300),
                METRE * said("bench.height", 120), said("bench.yaw", 0), said("bench.pitch", 15), 1);
        this.flight = first;
    }

    public static void main(final String[] args) throws IOException {
        final LandFeatures features = MelonFeatures.INSTANCE.features();
        new TiledBench(features, new ByMaterials(features)).run(features.terrain().name()
                + " in tiles around the eye, in the lab's materials, plain where it has none");
    }

    /** Opened, {@code about} said of what is drawn, until a window is closed or the time asked for is up. */
    public void run(final String about) throws IOException {
        final Map<Chosen, TileDrawn> drawn = new LinkedHashMap<>();
        try (TileBuilder builder = new TileBuilder(choice.tiling(), features.terrain(), painting,
                Math.max(1, Runtime.getRuntime().availableProcessors() - 2));
             Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Tiled terrain bench, drawn on " + card.name(), WIDTH, HEIGHT);
             Looks looks = new Looks(card, laid, painting)) {
            final TiledControls controls = new TiledControls(wanted, about, painting.legend(),
                    new MapPicture(features.terrain(), painting, MAP), first);
            Platform.startup(() -> controls.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            try {
                drawn(desk, card, window, builder, drawn, looks, controls);
                snapshot(card, window, drawn, looks);
            } finally {
                drawn.values().forEach(TileDrawn::close);
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame: the tiles around the eye chosen again as it is placed, built ones put on the card. */
    private void drawn(final Desk desk, final Card card, final Window window, final TileBuilder builder,
                       final Map<Chosen, TileDrawn> drawn, final Looks looks, final TiledControls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        List<Chosen> chosen = choice.around(flight.x(), flight.z());
        builder.want(chosen);
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Flight before = flight;
            flight = looked(window.mouse(), wanted.raised().map(up -> new Flight(flight.x(), flight.z(), up,
                    flight.yaw(), flight.pitch(), 1)).orElse(flight));
            final double[] spot = wanted.placedAt().orElse(null);
            if (spot != null) {
                flight = new Flight(spot[0], spot[1], flight.height(), flight.yaw(), flight.pitch(), 1);
                chosen = choice.around(flight.x(), flight.z());
                builder.want(chosen);
            }
            if (!flight.equals(before)) {
                controls.seen(flight);
            }
            kept(card, builder, chosen, drawn);
            frame(window, drawn, looks);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            final long now = System.nanoTime();
            if (++frames % EVENED == 0) {
                controls.told((now - last) / MILLION / EVENED);
                controls.tiles(chosen.size(), builder.done(), drawn.size(), level(chosen, true), level(chosen, false));
                last = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle && drawn.size() == chosen.size()) {
                return;
            }
        }
    }

    /** The tiles on the card made the ones wanted: those no longer wanted closed, a few more built ones put on. */
    private void kept(final Card card, final TileBuilder builder, final List<Chosen> chosen,
                      final Map<Chosen, TileDrawn> drawn) {
        drawn.entrySet().removeIf(entry -> {
            final boolean gone = !chosen.contains(entry.getKey());
            if (gone) {
                entry.getValue().close();
            }
            return gone;
        });
        int put = 0;
        for (final Chosen tile : chosen) {
            if (put < EACH_FRAME && !drawn.containsKey(tile)) {
                final var built = builder.built(tile.key());
                if (built.isPresent()) {
                    drawn.put(tile, new TileDrawn(card, laid, built.get(), tile, up, painting.waterLayer()));
                    put++;
                }
            }
        }
    }

    /** {@code flight} turned round and tipped as the mouse was dragged, over the same spot, as high. */
    private Flight looked(final Moved mouse, final Flight flight) {
        return mouse.dragged() ? flight.dragged(mouse.across(), mouse.down()) : flight;
    }

    private int level(final List<Chosen> chosen, final boolean finest) {
        return chosen.stream().mapToInt(tile -> tile.key().level()).reduce(finest ? Math::max : Math::min).orElse(0);
    }

    /** The picture the card draws for {@code window}, saved, if a file is asked for it. */
    private void snapshot(final Card card, final Window window, final Map<Chosen, TileDrawn> drawn, final Looks looks)
            throws IOException {
        final String snapshot = System.getProperty("bench.snapshot", "");
        if (!snapshot.isEmpty()) {
            try (Canvas canvas = card.canvas(window.width(), window.height())) {
                frame(canvas, drawn, looks);
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
            }
        }
    }

    /** One frame of the tiles on the card, the eye where it is, drawn on {@code target}. */
    private void frame(final Target target, final Map<Chosen, TileDrawn> drawn, final Looks looks) {
        final Camera camera = flight.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> drawn.values().forEach(tile -> tile.drawn(pass, looks, flight, camera)));
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
