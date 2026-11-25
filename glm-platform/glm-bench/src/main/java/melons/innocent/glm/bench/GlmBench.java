package melons.innocent.glm.bench;

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
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.engine.EngineRange;
import melons.innocent.glm.model.BodyFigures;
import melons.innocent.glm.model.EngineBay;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.GlmPlatform;
import melons.innocent.glm.model.MadeOf;
import melons.innocent.glm.model.Proportions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * The GLM bench: the platform put together, a large SUV's, both its axles as far as each is made and its engine in
 * its bay, covered, its covers a group of their own to show or hide, on level ground,
 * looked at on the card from any side, in whatever light is set. Two windows, as the vehicles bench has: the card's,
 * where the mouse moves the view as on every bench (drag to turn, right-drag or Shift-drag to move across, scroll to
 * come nearer); and a JavaFX window beside it ({@link GlmConsole}) to show or hide each group, choose what it is
 * finished in and what its parts are made of, titanium wherever aluminium is, say. What
 * the assembly found, every part reached and every gap where parts meet again, is told beside it.
 *
 * <p>For a run with nobody at it: {@code -Dglm.engine=} the engine in its bay, L4, L5, V6, V8 or V12, or none;
 * {@code -Dbench.hidden=} the groups hidden to begin with, COVERS for the engine without its covers, say;
 * {@code -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code -Dbench.distance=}
 * (metres) where it is seen from; the light's options as on every bench; {@code -Dbench.settle=} so many thousandths
 * of a second ends it; {@code -Dbench.snapshot.card=} a file the card's picture is saved to as it ends.
 */
public final class GlmBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 780;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double NEAREST = 60;
    private static final double FURTHEST = 4_000;
    /** How near parts meeting again must be to be met. */
    private static final double MET = 1e-6;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final AtomicReference<OnShow> shown = new AtomicReference<>(new OnShow(new Finishes().coats(), hidden(),
            new MadeOf()));
    private final Proportions proportions = new Proportions();
    private final Assembled built = new Assembly(new GlmPlatform(proportions, new BodyFigures(), bay()).platform())
            .assembled();
    private final Vec3 middle = new Vec3(0, proportions.tyre(), proportions.back() / 2);
    private Orbit orbit = new Orbit(middle, said("bench.yaw", 35), said("bench.pitch", 18),
            METRE * said("bench.distance", 6));

    public static void main(final String[] args) throws IOException {
        new GlmBench().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        bus.subscribe(OnShow.class, shown::set, Bus.HERE);
        final GlmConsole console = new GlmConsole(bus, asked, shown.get(), built.placed().keySet().stream()
                .map(p -> p.part().made().material()).collect(Collectors.toSet()));
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The GLM bench, drawn on " + card.name(), WIDTH, HEIGHT);
             GlmView view = new GlmView(card, new Laid().asking(card.rays()), built, middle)) {
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
    private void drawn(final Desk desk, final Card card, final Window window, final GlmView view,
                       final GlmConsole console) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            orbit = moved(window.mouse());
            final long now = System.nanoTime();
            skies.stepped((now - last) / BILLION);
            last = now;
            frame(window, view);
            frames++;
            if (frames % EVENED == 0) {
                bus.publish(new Bus.Figures(figures(card, (now - told) / MILLION / EVENED)));
                final List<String> wrong = built.problems(MET);
                bus.publish(new Bus.Problems(wrong.isEmpty() ? "" : String.join("\n", wrong)));
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

    /** What the bench says of what it drew: its parts, where they meet again, how far off, and the card. */
    private String figures(final Card card, final double frame) {
        final double widest = built.gaps().stream().mapToDouble(Assembled.Gap::apart).max().orElse(0);
        return String.format(Locale.ENGLISH, "%d parts placed, every one reached from the underbody%n"
                + "%d places where parts meet again, the widest %.4f cm apart%n"
                + "Seen from %.1f m off, %.0f° up%nA frame: %.1f ms%n%s%n%nDrawn on %s", built.placed().size(),
                built.gaps().size(), widest, orbit.distance() / METRE, orbit.pitch(), frame, card.memory().said(),
                card.name());
    }

    /** The way of looking, moved as the mouse moved. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final GlmView view) {
        view.drawn(target, shown.get(), orbit.camera(48, 10), skies.lit(), asked.shadowed(), asked.shadowing());
    }

    /**
     * The engine in its bay, the one the run was told as {@code -Dglm.engine=} (L4, L5, V6, V8 or V12; or none), the
     * V6 where it was told none; covered, its covers shown or hidden as a group of their own.
     */
    private EngineBay bay() {
        final String called = System.getProperty("glm.engine", "V6");
        return called.equalsIgnoreCase("none") ? null : new EngineBay(new EngineRange().all().stream()
                .filter(e -> e.name().equalsIgnoreCase(called)).findFirst().orElseThrow(), true);
    }

    /** The groups the run was told to hide to begin with, as {@code -Dbench.hidden=COVERS,UNDERBODY}; or none. */
    private Set<Group> hidden() {
        final String told = System.getProperty("bench.hidden", "");
        return told.isBlank() ? Set.of() : Arrays.stream(told.split(",")).map(String::strip).map(Group::valueOf)
                .collect(Collectors.toSet());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
