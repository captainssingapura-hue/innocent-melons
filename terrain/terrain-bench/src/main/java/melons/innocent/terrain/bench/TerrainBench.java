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
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The terrain's bench: a ground drawn on the card as a sheet ({@link Sheet}), coloured as grassland by how high it
 * is ({@link Grassland}) and lit by the day, in a window of its own, with its controls in a JavaFX window beside it
 * ({@link Controls}). No game engine: the land is only looked at. The thread this is started on is the card's: it
 * opens the card, draws every frame and moves the view as the mouse moves in the card's window (dragged, it turns;
 * right-dragged or Shift-dragged, it is carried across; scrolled, nearer or further). Closing either window ends it;
 * anything the validation layer says ends it too, and is the error it ends with.
 *
 * <p>{@code -Dbench.every=} says how far apart the sheet's points are, in metres; {@code -Dbench.yaw=},
 * {@code -Dbench.pitch=} and {@code -Dbench.distance=} (degrees, degrees down, metres) where it is first looked at
 * from. For a run with nobody watching, {@code -Dbench.settle=} so many thousandths of a second ends it after that
 * long, and {@code -Dbench.snapshot=} a file saves, as it ends, the picture the card draws for its window.
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

    private final Daylight day = new Daylight();
    private final Wanted wanted = new Wanted();
    private final Ground ground;
    private final Look look;
    private final Orbit first;
    /** The way the land is looked at now: the card's thread's own, moved by the mouse in the card's window. */
    private Orbit orbit;

    /** A bench for {@code ground}, coloured as {@code grass}. */
    public TerrainBench(final Ground ground, final Grassland grass) {
        this.ground = ground;
        this.look = new Look(new Laid(), grass.surface());
        final Extent e = ground.extent();
        final double x = (e.x0() + e.x1()) / 2;
        final double z = (e.z0() + e.z1()) / 2;
        this.first = new Orbit(new Vec3(x, ground.height(x, z), z), said("bench.yaw", 20), said("bench.pitch", 25),
                METRE * said("bench.distance", Math.max(e.across(), e.along()) / METRE));
        this.orbit = first;
    }

    public static void main(final String[] args) throws IOException {
        new TerrainBench(new Rolling(), new Grassland(4_000, 16_000)).run("Rolling country, three kilometres square");
    }

    /** Opened, {@code about} said of what is drawn; until a window is closed or the time asked for is up. */
    public void run(final String about) throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Terrain bench, drawn on " + card.name(), WIDTH, HEIGHT);
             Formed land = new Formed(card, look, Sheet.INSTANCE.of(ground, METRE * said("bench.every", 10)))) {
            final Controls controls = new Controls(wanted, about);
            Platform.startup(() -> controls.shown(LEFT, TOP));
            controls.seen(orbit);
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, land, controls);
            final String snapshot = System.getProperty("bench.snapshot", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, land);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final Formed land,
                       final Controls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        while (window.open() && !wanted.stopped()) {
            desk.heard();
            final Orbit moved = wanted.firstWanted() ? first : moved(window.mouse());
            if (!moved.equals(orbit)) {
                orbit = moved;
                controls.seen(orbit);
            }
            frame(window, land);
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
            if (settle >= 0 && (now - began) / MILLION >= settle) {
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
    private void frame(final Target target, final Formed land) {
        final Camera camera = orbit.camera(FIELD, NEAR);
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(look.laid(), camera, day.light()).filling(aspect),
                pass -> land.drawn(pass, new Vec3(0, 0, 0), camera));
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
