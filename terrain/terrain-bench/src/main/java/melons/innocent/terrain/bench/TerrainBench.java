package melons.innocent.terrain.bench;

import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
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
import melons.innocent.terrain.Terrain;
import melons.innocent.terrain.melon.MelonCountry;
import melons.innocent.terrain.melon.MelonFeatures;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The terrain's bench: a ground drawn on the card as a sheet ({@link Sheet}), its raw shape at once and then coloured
 * layer by layer ({@link Colouring}), lit by the day, in a window of its own, with its controls in a JavaFX window
 * beside it ({@link Controls}). No game engine: the land is only looked at, flown over as the avatar flies on its jets
 * ({@link Pilot}): W, A, S and D ahead, aside and back, Space up and Ctrl down, Shift faster, the mouse dragged to look
 * round, the wheel to draw the land bigger or smaller about the spot under the eye ({@link Flight}). The thread this
 * is started on is the card's: it opens the card and draws every frame. Closing either window ends it; anything the
 * validation layer says ends it too, and is the error it ends with.
 *
 * <p>{@code -Dbench.terrain=} says which ground: {@code melon}, the Melon country (the first), or {@code rolling}, the
 * rolling country it began with; for the Melon country, {@code -Dbench.paint=} how it is painted: {@code features},
 * each land feature in its colour (the first), {@code materials}, each in the lab's material for its kind, plain where
 * it has none, or {@code dry}, its dry ground in concrete. {@code -Dbench.every=}
 * says how far apart the sheet's points are, in metres; {@code -Dbench.across=} and {@code -Dbench.along=} (metres)
 * where over the map the eye starts, {@code -Dbench.height=} (metres) how high, {@code -Dbench.yaw=} and
 * {@code -Dbench.pitch=} (degrees, degrees down) which way it looks, and {@code -Dbench.scale=} how many times as big
 * the land is drawn; {@code -Dbench.up=} how many times as high as it is the land is drawn, so a shallow bed shows;
 * {@code -Dbench.sun=} and {@code -Dbench.sunRound=} (degrees) how high the sun stands and from which way, low from
 * the south-west at first. For a run with nobody watching, {@code -Dbench.settle=} so many thousandths of a second
 * ends it after that long, once it is all coloured, and {@code -Dbench.snapshot=} a file saves, as it ends, the
 * picture the card draws for its window.
 */
public final class TerrainBench {

    private static final int WIDTH = 1200;
    private static final int HEIGHT = 720;
    private static final int LEFT = 40;
    private static final int TOP = 80;
    /** How far right of the controls' left edge the card's window stands, so the two are side by side. */
    private static final int BESIDE = 360;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    /** How wide the camera sees, in degrees, and how near it begins to see, in centimetres. */
    private static final double FIELD = 50;
    private static final double NEAR = 50;

    private final Daylight day;
    /** How many times as high the land is drawn as it is. */
    private final double up;
    private final Wanted wanted = new Wanted();
    private final Ground ground;
    private final Laid laid = new Laid();
    private final Painting painting;
    private final Flight first;
    /** Where the land is looked at from now: the card's thread's own, flown by the keys and the mouse. */
    private Flight flight;
    /** The land on the card now, as far as its colouring has come: the card's thread's own. */
    private LandDrawn land;

    /** A bench for {@code ground}, painted as {@code painting} says. */
    public TerrainBench(final Ground ground, final Painting painting) {
        this.ground = ground;
        final Daylight plain = new Daylight();
        this.day = new Daylight(plain.light().sunAt(said("bench.sun", 30), said("bench.sunRound", 225)), plain.sky());
        this.up = said("bench.up", 1);
        this.painting = painting;
        final Extent e = ground.extent();
        this.first = new Flight(METRE * said("bench.across", (e.x0() + e.x1()) / 2 / METRE),
                METRE * said("bench.along", e.z0() / METRE), METRE * said("bench.height", e.along() / 4 / METRE),
                said("bench.yaw", 0), said("bench.pitch", 25), said("bench.scale", 1));
        this.flight = first;
    }

    public static void main(final String[] args) throws IOException {
        final Plainly concrete = new Plainly("concrete", new Shades().of(new Materials.Concrete()));
        if ("rolling".equals(System.getProperty("bench.terrain", "melon"))) {
            new TerrainBench(new Rolling(), concrete).run("Rolling country, three kilometres square, in concrete");
        } else if ("dry".equals(System.getProperty("bench.paint", "features"))) {
            final Terrain melon = MelonCountry.INSTANCE.terrain();
            new TerrainBench(melon, concrete).run(melon.name() + ", its dry ground, in concrete");
        } else if ("materials".equals(System.getProperty("bench.paint", "features"))) {
            final LandFeatures features = MelonFeatures.INSTANCE.features();
            new TerrainBench(features.terrain(), new ByMaterials(features)).run(features.terrain().name()
                    + ", its land features in the lab's materials, plain where it has none");
        } else {
            final LandFeatures features = MelonFeatures.INSTANCE.features();
            new TerrainBench(features.terrain(), new ByFeatures(features))
                    .run(features.terrain().name() + ", its land features in their colours");
        }
    }

    /**
     * Opened, {@code about} said of what is drawn, until a window is closed or the time asked for is up: the ground's
     * heights worked out first, its raw shape drawn at once, and coloured layer by layer as the layers are laid.
     */
    public void run(final String about) throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Terrain bench, drawn on " + card.name(), WIDTH, HEIGHT)) {
            final Controls controls = new Controls(wanted, about, painting.legend());
            Platform.startup(() -> controls.shown(LEFT, TOP));
            final Colouring colouring = new Colouring(new Grid(ground, METRE * said("bench.every", 10)), painting);
            land = new LandDrawn(card, laid, painting, colouring.grid(), up, colouring.latest());
            colouring.start();
            controls.seen(flight, on());
            window.shown(LEFT + BESIDE, TOP);
            try {
                drawn(desk, card, window, colouring, controls);
                snapshot(card, window);
            } finally {
                land.close();
            }
        } finally {
            Platform.exit();
        }
    }

    /** The picture the card draws for {@code window}, saved, if a file is asked for it. */
    private void snapshot(final Card card, final Window window) throws IOException {
        final String snapshot = System.getProperty("bench.snapshot", "");
        if (!snapshot.isEmpty()) {
            try (Canvas canvas = card.canvas(window.width(), window.height())) {
                frame(canvas);
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
            }
        }
    }

    /**
     * Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks; the view
     * flown between frames, and the land drawn again as each layer of its colouring is laid.
     */
    private void drawn(final Desk desk, final Card card, final Window window, final Colouring colouring,
                       final Controls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        long before = began;
        int frames = 0;
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Colouring.Coloured coloured = colouring.latest();
            if (coloured.laid() != land.laid()) {
                land.close();
                land = new LandDrawn(card, laid, painting, colouring.grid(), up, coloured);
                controls.coloured(coloured.laid(), colouring.layers());
            }
            final long now = System.nanoTime();
            final Flight flown = wanted.firstWanted() ? first
                    : Pilot.INSTANCE.flown(flight, window, (now - before) / BILLION, wanted.speed());
            before = now;
            if (!flown.equals(flight)) {
                flight = flown;
                controls.seen(flight, on());
            }
            frame(window);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            if (frames % EVENED == 0) {
                controls.told((now - last) / MILLION / EVENED);
                last = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle && coloured.laid() == colouring.layers()) {
                return;
            }
        }
    }

    /** One frame of the land, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target) {
        final Camera camera = flight.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> land.drawn(pass, flight, camera));
    }

    /** What lies on the land under the eye, by its label. */
    private String on() {
        return painting.at(flight.x(), flight.z());
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
