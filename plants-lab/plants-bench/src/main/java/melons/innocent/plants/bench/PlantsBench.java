package melons.innocent.plants.bench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.vulkan.bench.Arranged;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The plants bench: one tree, or a patch of trees, looked at on the card at any stage, from any side, in whatever
 * light is set; beside it, if asked, the same as grown, and how dark each half of the picture comes out, so it can
 * be seen and measured whether a coarse stage stands for the fine one from wherever it is looked at. Two windows, as
 * the material bench has: the card's ({@link PlantsView}), where the mouse moves the view as on every bench (drag to
 * turn, right-drag or Shift-drag to move across, scroll to come nearer or go as far as three kilometres); and a
 * JavaFX window beside it ({@link Console}), whose buttons also turn the view to above, half way up or low.
 *
 * <p>For a run with nobody at it: {@code -Dbench.subject=} tree or patch, {@code -Dbench.kinds=} oak, walnut, birch
 * or mixed, {@code -Dbench.stage=} a stage by its name (none, for whatever its distance calls for), {@code
 * -Dbench.beside=true} the same as grown beside it, {@code -Dbench.rows=} trees along a patch's side, {@code
 * -Dbench.one=true} what is on show as one mesh; {@code -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code
 * -Dbench.distance=} (metres) where it is seen from; the light's options as on every bench; {@code -Dbench.settle=}
 * so many thousandths of a second ends it; {@code -Dbench.snapshot=} a file saves the JavaFX window, and {@code
 * -Dbench.snapshot.card=} a file the card's picture.
 */
public final class PlantsBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 680;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double NEAREST = 100;
    private static final double FURTHEST = 300_000;
    /** What the view turns about: half way up a tree. */
    private static final double MIDDLE = 700;
    /** The picture the darkness is measured on, drawn apart every so many frames. */
    private static final int MEASURED_WIDE = 640;
    private static final int MEASURED_HIGH = 360;
    /** How long past its time a run goes on, for the kit to save the JavaFX window, in thousandths of a second. */
    private static final long KIT_FIRST = 1500;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    /** How its two windows are arranged over the screens: as they always were, until it runs. */
    private Arranged arranged = new Arranged(false, LEFT, TOP, BESIDE);
    private final Bus bus = new Bus();
    private final AtomicReference<OnShow> shown = new AtomicReference<>(new Told().first());
    /** A way of looking asked for in the window, not yet taken up. */
    private final AtomicReference<Angle> turned = new AtomicReference<>();
    private Orbit orbit = new Orbit(new Vec3(0, MIDDLE, 0), said("bench.yaw", 20), said("bench.pitch", 12),
            METRE * said("bench.distance", 45));

    public static void main(final String[] args) throws IOException {
        new PlantsBench().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.logged(!System.getProperty("bench.log", "").isBlank());
        asked.clocked(Boolean.getBoolean("bench.sky"));
        asked.quickened(said("bench.sky.speed", asked.quickened()));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        asked.almanac(asked.almanac().told(System.getProperty("bench.sky.at", ""),
                System.getProperty("bench.sky.when", ""), System.getProperty("bench.sky.zone", "")));
        bus.subscribe(OnShow.class, shown::set, Bus.HERE);
        bus.subscribe(Angle.class, turned::set, Bus.HERE);
        final Console console = new Console(bus, asked, shown.get());
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The plants bench, drawn on " + card.name(), WIDTH, HEIGHT);
             Canvas measured = card.canvas(MEASURED_WIDE, MEASURED_HIGH);
             PlantsView view = new PlantsView(card, new Laid().asking(card.rays()), shown.get())) {
            asked.offered(view.offered());
            // a run nobody attends keeps the windows where they have always been, and as big
            arranged = new Arranged(Long.getLong("bench.settle", -1) < 0, LEFT, TOP, BESIDE);
            Platform.startup(() -> console.shown(arranged));
            arranged.shown(window, desk);
            drawn(desk, card, window, measured, view, console);
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
    private void drawn(final Desk desk, final Card card, final Window window, final Canvas measured,
                       final PlantsView view, final Console console) {
        final long settle = Long.getLong("bench.settle", -1)
                + (System.getProperty("bench.snapshot") == null ? 0 : KIT_FIRST);
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            arranged.heard(window, desk);
            orbit = moved(window.mouse());
            final long now = System.nanoTime();
            skies.stepped((now - last) / BILLION);
            last = now;
            frame(window, view);
            frames++;
            if (frames % EVENED == 0) {
                frame(measured, view);
                bus.publish(new Bus.Figures(figures(view, new Darkness(new Picture(measured.width(),
                        measured.pixels())), card) + String.format(Locale.ENGLISH, "\nA frame: %.1f ms",
                        (now - told) / MILLION / EVENED)));
                if (asked.logged()) {
                    System.out.println(LocalTime.now().truncatedTo(ChronoUnit.SECONDS) + "  " + String.format(
                            Locale.ENGLISH, "A frame: %.1f ms; ", (now - told) / MILLION / EVENED)
                            + card.memory().said() + "; " + view.said(shown.get()));
                }
                console.sky(skies.said());
                told = now;
            }
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (Long.getLong("bench.settle", -1) >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /** What the bench says of what it drew: what is on show, how far off, at what stages, and how dark. */
    private String figures(final PlantsView view, final Darkness darkness, final Card card) {
        final OnShow now = shown.get();
        final String stages = view.said(now);
        return now.said() + String.format(Locale.ENGLISH, "\nSeen from %.0f m off, %.0f° up",
                orbit.distance() / METRE, orbit.pitch()) + "\n" + stages + "\n" + darkness.said()
                + "\n" + card.memory().said() + "\n\nDrawn on " + card.name();
    }

    /** The way of looking, turned to an angle asked for in the window, if any, then moved as the mouse moved. */
    private Orbit moved(final Moved mouse) {
        final Angle angle = turned.getAndSet(null);
        final Orbit from = angle == null ? orbit
                : new Orbit(orbit.round(), orbit.yaw(), angle.pitch(), orbit.distance());
        final Orbit dragged = !mouse.dragged() ? from
                : mouse.other() ? from.carried(mouse.across(), mouse.down())
                : from.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final PlantsView view) {
        view.drawn(target, shown.get(), orbit.camera(48, 10), skies.lit(), asked.shadowed(), asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }

}
