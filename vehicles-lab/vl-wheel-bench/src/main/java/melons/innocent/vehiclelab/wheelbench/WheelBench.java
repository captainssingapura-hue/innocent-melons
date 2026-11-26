package melons.innocent.vehiclelab.wheelbench;

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
import melons.innocent.vehiclelab.wheels.RimDesign;
import melons.innocent.vehiclelab.wheels.WheelSize;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The wheels bench: one wheel and its tyre standing on level ground, of any design and at any size, looked at on the
 * card from any side, in whatever light is set, turning if asked. Two windows, as every bench has: the card's, where
 * the mouse moves the view (drag to turn, right-drag or Shift-drag to move across, scroll to come nearer); and a JavaFX
 * window beside it ({@link WheelConsole}) to choose its rim's design and set its size.
 *
 * <p>For a run with nobody at it: {@code -Dwheel.design=} CLASSIC or SPOKED; {@code -Dwheel.width=} in millimetres,
 * {@code -Dwheel.aspect=} in hundredths, {@code -Dwheel.rim=} in inches; {@code -Dwheel.turning=true};
 * {@code -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code -Dbench.distance=} (metres) where it is seen from;
 * {@code -Dbench.settle=} so many thousandths of a second ends it; {@code -Dbench.snapshot.card=} a file the card's
 * picture is saved to as it ends.
 */
public final class WheelBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 740;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double NEAREST = 30;
    private static final double FURTHEST = 1_500;
    /** How fast it turns, in degrees a second. */
    private static final double TURNING = 90;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final AtomicReference<WheelShown> shown = new AtomicReference<>(first());
    private Orbit orbit = new Orbit(new Vec3(0, shown.get().size().radius(), 0), said("bench.yaw", 70),
            said("bench.pitch", 10), METRE * said("bench.distance", 1.6));
    /** How far it has turned, in degrees. */
    private double roll;

    public static void main(final String[] args) throws IOException {
        new WheelBench().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        bus.subscribe(WheelShown.class, shown::set, Bus.HERE);
        final WheelConsole console = new WheelConsole(bus, asked);
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The wheels bench, drawn on " + card.name(), WIDTH, HEIGHT);
             WheelView view = new WheelView(card, new Laid().asking(card.rays()))) {
            asked.offered(view.offered());
            final WheelShown begun = shown.get();
            Platform.startup(() -> console.shown(LEFT, TOP, begun));
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

    /** What is on show to begin with, as the run says, the retro saloon's wheel unless. */
    private WheelShown first() {
        final WheelShown plain = new WheelShown();
        return new WheelShown(RimDesign.valueOf(System.getProperty("wheel.design", plain.design().name())),
                new WheelSize(said("wheel.width", plain.size().width()), said("wheel.aspect", plain.size().aspect()),
                        said("wheel.rim", plain.size().rim())), Boolean.getBoolean("wheel.turning"));
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final WheelView view,
                       final WheelConsole console) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            orbit = moved(window.mouse());
            final long now = System.nanoTime();
            final double seconds = (now - last) / BILLION;
            last = now;
            skies.stepped(seconds);
            if (shown.get().turning()) {
                roll = (roll + TURNING * seconds) % 360;
            }
            frame(window, view);
            if (++frames % EVENED == 0) {
                bus.publish(new Bus.Figures(figures(shown.get())));
                console.sky(skies.said());
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

    /** What the bench says back of {@code shown}. */
    private String figures(final WheelShown shown) {
        final WheelSize size = shown.size();
        return String.format(Locale.ENGLISH, "%s, %s%n%.0f mm across the tyre, %.0f mm across the rim%n"
                        + "Its wall %.0f mm tall, the tyre %.0f mm wide%nSeen from %.1f m off", shown.design().title(),
                size.said(), size.radius() * 20, size.rimRadius() * 20, size.wall() * 10, size.wide() * 10,
                orbit.distance() / METRE);
    }

    /** The way of looking, moved as the mouse moved. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final WheelView view) {
        view.drawn(target, shown.get(), roll, orbit.camera(48, 5), skies.lit(), asked.shadowed(),
                asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
