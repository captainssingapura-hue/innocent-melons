package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
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
import java.util.ArrayList;
import java.util.List;

/**
 * The terrain's bench: a ground drawn on the card as a sheet ({@link Sheet}), in one of the lab's materials and lit
 * by the day, in a window of its own, with its controls in a JavaFX window beside it
 * ({@link Controls}). No game engine: the land is only looked at. The thread this is started on is the card's: it
 * opens the card, draws every frame and moves the view as the mouse moves in the card's window (dragged, it turns;
 * right-dragged or Shift-dragged, it is carried across; scrolled, nearer or further). Closing either window ends it;
 * anything the validation layer says ends it too, and is the error it ends with.
 *
 * <p>{@code -Dbench.terrain=} says which ground: {@code melon}, the Melon country (the first), or {@code rolling}, the
 * rolling country it began with; for the Melon country, {@code -Dbench.paint=} how it is painted: {@code features},
 * each land feature in its colour (the first), or {@code dry}, its dry ground in concrete. {@code -Dbench.every=}
 * says how far apart the sheet's points are, in metres; {@code -Dbench.across=} and {@code -Dbench.along=} (metres)
 * what is first looked at, and {@code -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code -Dbench.distance=}
 * (degrees, degrees down, metres) where from; {@code -Dbench.up=} how many times as high the land is drawn as it is,
 * so a shallow bed shows; {@code -Dbench.sun=} and
 * {@code -Dbench.sunRound=} (degrees) how high the sun stands and from which way, low from the south-west at first,
 * so the land's relief shows. For a run with nobody watching, {@code -Dbench.settle=} so many thousandths of a
 * second ends it after that long, and {@code -Dbench.snapshot=} a file saves, as it ends, the picture the card draws
 * for its window.
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
    private static final double METRE = 100;
    /** How near the eye may come to what it looks at, and how far off it may go, in centimetres. */
    private static final double NEAREST = 500;
    private static final double FURTHEST = 3_000_000;
    /** How wide the camera sees, in degrees, and how near it begins to see, in centimetres. */
    private static final double FIELD = 45;
    private static final double NEAR = 50;

    private final Daylight day;
    /** How many times as high the land is drawn as it is. */
    private final double up;
    private final Wanted wanted = new Wanted();
    private final Ground ground;
    private final Laid laid = new Laid();
    private final Painting painting;
    private final Orbit first;
    /** The way the land is looked at now: the card's thread's own, moved by the mouse in the card's window. */
    private Orbit orbit;
    /** The land on the card now, as far as its colouring has come: the card's thread's own. */
    private Land land;

    /** A bench for {@code ground}, painted as {@code painting} says. */
    public TerrainBench(final Ground ground, final Painting painting) {
        this.ground = ground;
        final Daylight plain = new Daylight();
        this.day = new Daylight(plain.light().sunAt(said("bench.sun", 30), said("bench.sunRound", 225)), plain.sky());
        this.up = said("bench.up", 1);
        this.painting = painting;
        final Extent e = ground.extent();
        final double x = METRE * said("bench.across", (e.x0() + e.x1()) / 2 / METRE);
        final double z = METRE * said("bench.along", (e.z0() + e.z1()) / 2 / METRE);
        this.first = new Orbit(new Vec3(x, ground.height(x, z) * up, z), said("bench.yaw", 0), said("bench.pitch", 28),
                METRE * said("bench.distance", Math.max(e.across(), e.along()) / METRE));
        this.orbit = first;
    }

    public static void main(final String[] args) throws IOException {
        final Plainly concrete = new Plainly("concrete", new Shades().of(new Materials.Concrete()));
        if ("rolling".equals(System.getProperty("bench.terrain", "melon"))) {
            new TerrainBench(new Rolling(), concrete).run("Rolling country, three kilometres square, in concrete");
        } else if ("dry".equals(System.getProperty("bench.paint", "features"))) {
            final Terrain melon = MelonCountry.INSTANCE.terrain();
            new TerrainBench(melon, concrete).run(melon.name() + ", its dry ground, in concrete");
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
            land = new Land(card, colouring.grid(), colouring.latest());
            colouring.start();
            controls.seen(orbit, on());
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
     * Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks; the land
     * drawn again as each layer of its colouring is laid.
     */
    private void drawn(final Desk desk, final Card card, final Window window, final Colouring colouring,
                       final Controls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Colouring.Coloured coloured = colouring.latest();
            if (coloured.laid() != land.laid()) {
                land.close();
                land = new Land(card, colouring.grid(), coloured);
                controls.coloured(coloured.laid(), colouring.layers());
            }
            final Orbit moved = wanted.firstWanted() ? first : moved(window.mouse());
            if (!moved.equals(orbit)) {
                orbit = moved;
                controls.seen(orbit, on());
            }
            frame(window);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            final long now = System.nanoTime();
            if (frames % EVENED == 0) {
                controls.told((now - last) / MILLION / EVENED);
                last = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle && coloured.laid() == colouring.layers()) {
                return;
            }
        }
    }

    /** The way of looking, moved as {@code mouse} says the mouse moved: turned, or carried across, and scrolled. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of the land, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target) {
        final Camera camera = orbit.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> land.parts().forEach(part -> part.drawn(pass, new Vec3(0, 0, 0), camera)));
    }

    /** What lies where the land is looked at, by its label. */
    private String on() {
        return painting.at(orbit.round().x(), orbit.round().z());
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }

    /** The land on the card: a sheet for each layer it is coloured in so far, each in its surface. Closed, all gone. */
    private final class Land implements AutoCloseable {

        private final List<Formed> parts = new ArrayList<>();
        private final int laid;

        Land(final Card card, final Grid grid, final Colouring.Coloured coloured) {
            this.laid = coloured.laid();
            Sheet.INSTANCE.parts(grid, up, coloured.layers()).forEach((layer, sheet) ->
                    parts.add(new Formed(card, new Look(TerrainBench.this.laid, painting.surface(layer)), sheet)));
        }

        int laid() {
            return laid;
        }

        List<Formed> parts() {
            return parts;
        }

        @Override
        public void close() {
            parts.forEach(Formed::close);
        }
    }
}
