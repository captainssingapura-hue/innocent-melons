package melons.innocent.glm.lab;

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
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.RearAxle;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The GLM part lab: the platform's parts one at a time, the rear subframe to begin with, alone in their own frames or
 * put together with the rest, inside a grid as a drawing office rules one, on the card, in whatever light is set. Two
 * windows, as every bench has: the card's, where the mouse moves the view (drag to turn, right-drag or Shift-drag to
 * move across, scroll to come nearer), turning about what is looked at whenever another part or level is chosen; and
 * a JavaFX window beside it ({@link LabConsole}) to choose the part, how it is looked at, and what is shown round it.
 *
 * <p>For a run with nobody at it: {@code -Dlab.part=} the start of a part's name, as the window lists it ("Links:
 * right toe"); {@code -Dlab.level=assembly}; {@code -Dlab.others=} ghost, finished, group or hidden; {@code
 * -Dlab.fine=true}; {@code -Dlab.grid=false}; {@code -Dlab.ground=false}; {@code -Dbench.yaw=}, {@code
 * -Dbench.pitch=}; the light's options as on every bench; {@code
 * -Dbench.settle=} so many thousandths of a second ends it; {@code -Dbench.snapshot.card=} a file the card's picture
 * is saved to as it ends.
 */
public final class PartLab {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 780;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    /** How far off the eye stands from what it looks at, as a share of its size; how near and how far it may go. */
    private static final double BACK = 1.6;
    private static final double NEAREST = 5;
    private static final double FURTHEST = 4_000;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final Assembled built = new Assembly(new RearAxle().platform()).assembled();
    private final Finishes finishes = new Finishes();
    private final AtomicReference<LabShow> shown = new AtomicReference<>();
    private Orbit orbit;

    public static void main(final String[] args) throws IOException {
        new PartLab().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        final LabConsole console = new LabConsole(bus, asked, built);
        final LabShow first = first(console.parts());
        shown.set(first);
        bus.subscribe(LabShow.class, shown::set, Bus.HERE);
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The GLM part lab, drawn on " + card.name(), WIDTH, HEIGHT);
             LabView view = new LabView(card, new Laid().asking(card.rays()), built)) {
            asked.offered(view.offered());
            Platform.startup(() -> console.shown(LEFT, TOP, first));
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, view, console);
        } finally {
            Platform.exit();
        }
    }

    /** What is shown to begin with: the part the run names, the rear subframe if it names none, as the run says. */
    private LabShow first(final List<PartAt> parts) {
        final String named = System.getProperty("lab.part", "Subframes").toLowerCase(Locale.ROOT);
        final PartAt part = parts.stream().filter(p -> (p.part().group().title() + ": " + p.said())
                .toLowerCase(Locale.ROOT).startsWith(named)).findFirst().orElse(parts.getFirst());
        return new LabShow(part, "assembly".equals(System.getProperty("lab.level")) ? LabShow.Level.ASSEMBLY
                : LabShow.Level.PART, others(), !"false".equals(System.getProperty("lab.grid")),
                !"false".equals(System.getProperty("lab.ground")), true,
                Boolean.getBoolean("lab.fine") ? Spacing.FINE : Spacing.NORMAL);
    }

    /** How the rest are shown to begin with, as the run says: as glass if it says nothing. */
    private LabShow.Others others() {
        return LabShow.Others.valueOf(System.getProperty("lab.others", "ghost").toUpperCase(Locale.ROOT));
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final LabView view,
                       final LabConsole console) throws IOException {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        LabShow was = null;
        LabScene.Settled scene = null;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            final LabShow now = shown.get();
            if (!now.equals(was)) {
                scene = new LabScene(built, now, finishes).settled();
                view.ruled(scene);
                if (was == null || !now.part().equals(was.part()) || now.level() != was.level()) {
                    orbit = new Orbit(scene.middle(), orbit == null ? said("bench.yaw", 35) : orbit.yaw(),
                            orbit == null ? said("bench.pitch", 20) : orbit.pitch(), scene.size() * BACK);
                }
                was = now;
            }
            orbit = moved(window.mouse());
            final long time = System.nanoTime();
            skies.stepped((time - last) / BILLION);
            last = time;
            frame(window, view, scene);
            if (++frames % EVENED == 0) {
                bus.publish(new Bus.Figures(String.format(Locale.ENGLISH, "%s%nThe grid ruled every %.0f cm%n"
                        + "Seen from %.2f m off, %.0f° up%n%s%nDrawn on %s", now.part().said(), scene.grid().step(),
                        orbit.distance() / METRE, orbit.pitch(), card.memory().said(), card.name())));
                console.sky(skies.said());
            }
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (settle >= 0 && (time - began) / MILLION >= settle) {
                break;
            }
        }
        final String snapshot = System.getProperty("bench.snapshot.card", "");
        if (!snapshot.isEmpty() && scene != null) {
            try (Canvas canvas = card.canvas(window.width(), window.height())) {
                frame(canvas, view, scene);
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
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

    /** One frame of {@code scene}, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final LabView view, final LabScene.Settled scene) {
        view.drawn(target, scene, orbit.camera(48, 1), skies.lit(), asked.shadowed(), asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
