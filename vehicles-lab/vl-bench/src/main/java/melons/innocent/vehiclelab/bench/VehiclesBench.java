package melons.innocent.vehiclelab.bench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The vehicles bench: the boxer on level ground, looked at on the card from any side, in whatever light is set. Two
 * windows, as the plants bench has: the card's, where the mouse moves the view as on every bench (drag to turn,
 * right-drag or Shift-drag to move across, scroll to come nearer); and a JavaFX window beside it ({@link Console}) to
 * choose each of its finishes and its colour, turn its bars and set its wheels turning.
 *
 * <p>For a run with nobody at it: {@code -Dbench.steer=} degrees its bars are turned, {@code -Dbench.rolling=true}
 * its wheels turning; {@code -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code -Dbench.distance=} (metres) where it is
 * seen from; the light's options as on every bench; {@code -Dbench.settle=} so many thousandths of a second ends it;
 * {@code -Dbench.snapshot.card=} a file the card's picture is saved to as it ends.
 */
public final class VehiclesBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 740;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double NEAREST = 80;
    private static final double FURTHEST = 3_000;
    /** What the view turns about: half way up the bike; how fast its wheels turn, in degrees a second. */
    private static final double MIDDLE = 55;
    private static final double TURNING = 240;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final AtomicReference<OnShow> shown = new AtomicReference<>(new OnShow(new OnShow().livery(),
            said("bench.steer", 0), Boolean.getBoolean("bench.rolling")));
    private Orbit orbit = new Orbit(new Vec3(0, MIDDLE, 0), said("bench.yaw", 35), said("bench.pitch", 12),
            METRE * said("bench.distance", 4));
    /** How far its wheels have turned, in degrees. */
    private double roll;

    public static void main(final String[] args) throws IOException {
        new VehiclesBench().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        bus.subscribe(OnShow.class, shown::set, Bus.HERE);
        final Console console = new Console(bus, asked, shown.get());
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The vehicles bench, drawn on " + card.name(), WIDTH, HEIGHT);
             BikeView view = new BikeView(card, new Laid().asking(card.rays()))) {
            asked.offered(view.offered());
            Platform.startup(() -> console.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, view, console);
            final String snapshot = System.getProperty("bench.snapshot.card", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, view);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final BikeView view,
                       final Console console) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            orbit = moved(window.mouse());
            final long now = System.nanoTime();
            final double seconds = (now - last) / BILLION;
            skies.stepped(seconds);
            if (shown.get().rolling()) {
                roll = (roll + TURNING * seconds) % 360;
            }
            last = now;
            frame(window, view);
            frames++;
            if (frames % EVENED == 0) {
                bus.publish(new Bus.Figures(String.format(Locale.ENGLISH, "Seen from %.1f m off, %.0f° up\n"
                        + "A frame: %.1f ms\n%s\n\nDrawn on %s", orbit.distance() / METRE, orbit.pitch(),
                        (now - told) / MILLION / EVENED, card.memory().said(), card.name())));
                console.sky(skies.said());
                told = now;
            }
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (settle >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /** The way of looking, moved as the mouse moved. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final BikeView view) {
        view.drawn(target, shown.get(), roll, orbit.camera(48, 10), skies.lit(), asked.shadowed(),
                asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
