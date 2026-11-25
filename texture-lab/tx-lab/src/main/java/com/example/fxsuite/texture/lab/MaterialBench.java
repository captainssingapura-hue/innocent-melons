package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.texture.materials.Material;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The material bench: any material of the lab's tree, looked at on a wall, a ball and a block, in whatever light is
 * set. The first bench whose view is the card's: the three shapes are drawn in a window of the card's own
 * ({@link MaterialView}), and a JavaFX window beside it, laid out by the engine's bench kit, chooses the material,
 * sets the light and says what the material is ({@link Console}). It is the pattern for the benches that follow.
 *
 * <p>How the two windows work together. The thread this is started on is the card's: it opens the card, draws every
 * frame, and hears the card's window, where the mouse turns the view as it does on every bench (drag to turn,
 * right-drag or Shift-drag to move across, scroll to come nearer). JavaFX runs on its own thread and touches
 * nothing of the card: what is on show goes out as a message on the bench's bus, and the card's thread takes up
 * the newest as each frame begins; what is drawn says its figures back on the same bus. Closing either window ends
 * the bench.
 *
 * <p>For a run with nobody at it: {@code -Dbench.material=} a material by its name in the tree, {@code
 * -Dbench.shapes=} the shapes it stands on, by name with commas between, {@code -Dbench.tint=} their colour, for
 * paint, written as {@code #a3151c}, {@code -Dbench.further=} how many times as far it is to seem, {@code
 * -Dbench.yaw=}, {@code -Dbench.pitch=} and {@code -Dbench.distance=} (metres) where it is seen from; the light's
 * options as a walk has them ({@code bench.shadows}, {@code bench.night}, {@code bench.sun.high}, {@code
 * bench.sun.round}, {@code bench.sky} and its kin); {@code
 * -Dbench.settle=} so many thousandths of a second ends it after that long; {@code -Dbench.snapshot=} a file
 * saves the JavaFX window, as the kit does for every bench, and {@code -Dbench.snapshot.card=} a file saves the
 * card's picture.
 */
public final class MaterialBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 680;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double METRE = 100;
    private static final double NEAREST = 40;
    private static final double FURTHEST = 4000;
    /** How long past its time a run goes on, for the kit to save the JavaFX window, in thousandths of a second. */
    private static final long KIT_FIRST = 1500;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    /** How its two windows are arranged over the screens: as they always were, until it runs. */
    private Arranged arranged = new Arranged(false, LEFT, TOP, BESIDE);
    private final Bus bus = new Bus();
    /** What is on show: the newest said on the bus, taken up by the card's thread as each frame begins. */
    private final AtomicReference<OnShow> shown = new AtomicReference<>(first());
    /** The way the stand is looked at: the card's thread's own, moved by the mouse in the card's window. */
    private Orbit orbit = new Orbit(new Stand(shown.get().shapes()).middle(), said("bench.yaw", 18),
            said("bench.pitch", 12), METRE * said("bench.distance", 9));
    /** The middle of the row the last frame looked at: the row moves when the shapes on show change. */
    private Vec3 lookedAt = new Stand(shown.get().shapes()).middle();

    public static void main(final String[] args) throws IOException {
        new MaterialBench().run();
    }

    private void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.clocked(Boolean.getBoolean("bench.sky"));
        asked.quickened(said("bench.sky.speed", asked.quickened()));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        asked.almanac(asked.almanac().told(System.getProperty("bench.sky.at", ""),
                System.getProperty("bench.sky.when", ""), System.getProperty("bench.sky.zone", "")));
        bus.subscribe(OnShow.class, shown::set, Bus.HERE);
        final Console console = new Console(bus, asked, shown.get());
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The material bench, drawn on " + card.name(), WIDTH, HEIGHT);
             MaterialView view = new MaterialView(card, new Laid().asking(card.rays()))) {
            asked.offered(view.offered());
            // a run nobody attends keeps the windows where they have always been, and as big
            arranged = new Arranged(Long.getLong("bench.settle", -1) < 0, LEFT, TOP, BESIDE);
            Platform.startup(() -> console.shown(arranged));
            arranged.shown(window, desk);
            drawn(desk, card, window, view, console);
            final String snapshot = System.getProperty("bench.snapshot.card", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, view);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            console.close();
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final MaterialView view,
                       final Console console) {
        // asked for its own picture too, the JavaFX window is given time to take it first, as the kit takes it
        final long settle = Long.getLong("bench.settle", -1)
                + (System.getProperty("bench.snapshot") == null ? 0 : KIT_FIRST);
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            arranged.heard(window, desk);
            orbit = moved(window.mouse(), new Stand(shown.get().shapes()).middle());
            final long now = System.nanoTime();
            skies.stepped((now - last) / BILLION);
            last = now;
            frame(window, view);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            if (frames % EVENED == 0) {
                bus.publish(new Bus.Figures(new Told(shown.get().material()).said() + "\n\nDrawn on "
                        + card.name() + String.format("\nA frame: %.1f ms", (now - told) / MILLION / EVENED)));
                console.sky(skies.said());
                told = now;
            }
            if (Long.getLong("bench.settle", -1) >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The way of looking, moved as {@code mouse} says the mouse moved: turned, or carried across, and scrolled. When
     * the shapes on show have changed so that the row's middle is elsewhere, it looks at {@code middle} again, from
     * the same side and as far off.
     */
    private Orbit moved(final Moved mouse, final Vec3 middle) {
        final Orbit from = middle.equals(lookedAt) ? orbit
                : new Orbit(middle, orbit.yaw(), orbit.pitch(), orbit.distance());
        lookedAt = middle;
        final Orbit dragged = !mouse.dragged() ? from
                : mouse.other() ? from.carried(mouse.across(), mouse.down())
                : from.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, looked at as it is now, drawn on {@code target}. */
    private void frame(final Target target, final MaterialView view) {
        view.drawn(target, shown.get(), orbit.camera(48, 10), skies.lit(), asked.shadowed(),
                asked.shadowing());
    }

    /**
     * What is on show to begin with: the material the run was told, on the shapes and in the colour it was told;
     * where it was told no shapes or no colour, on those that suit the material ({@link Suited}).
     */
    private OnShow first() {
        final Material material = new Called(System.getProperty("bench.material", "ChineseGreyBrick")).material();
        final Suited suited = new Suited(material);
        final String shapes = System.getProperty("bench.shapes");
        final String tint = System.getProperty("bench.tint");
        return new OnShow(material, said("bench.further", 1),
                shapes == null ? suited.shapes() : new Called(shapes).shapes(),
                tint == null ? suited.colour() : new Called(tint).colour());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
