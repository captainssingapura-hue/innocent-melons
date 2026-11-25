package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.bench.subject.Watermelon;
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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The renderer's own bench: a row of watermelons, each shown as from twice as far as the one before and, over it,
 * the same melon really that far off ({@link Row}); drawn by the card in a window of its own, with the bench's
 * controls in a JavaFX window beside it. The thread this is started on is the card's: it opens the card, draws
 * every frame, hears the card's window, and moves the view as the mouse moves in that window, as the engine's own
 * benches do (dragged, it turns; right-dragged or Shift-dragged, it moves across; scrolled, nearer or further).
 * JavaFX runs on its own thread and only asks ({@link Asked}). Closing either window stops the bench. Anything
 * the validation layer says stops it too, and is the error it ends with.
 *
 * <p>Where it is first looked at from is as {@code -Dbench.yaw=}, {@code -Dbench.pitch=} (degrees, looking down
 * when more than nothing) and {@code -Dbench.distance=} (metres) say, and how many times as far the first melon
 * is shown as from is as {@code -Dbench.further=} says. For a run with nobody watching:
 * {@code -Dbench.settle=} so many thousandths of a second ends it by itself after that long, and
 * {@code -Dbench.snapshot=} a file saves, as it ends, the picture the card draws for its window.
 */
public final class Bench {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 520;
    private static final int LEFT = 60;
    private static final int TOP = 120;
    /** How far right of the controls' left edge the card's window stands, so the two are side by side. */
    private static final int BESIDE = 380;
    /** How many frames' time is evened out before it is told. */
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double METRE = 100;
    /** How near the eye may be scrolled to the fruit, and how far off, in centimetres. */
    private static final double NEAREST = 30;
    private static final double FURTHEST = 3000;

    private final Daylight day = new Daylight();
    private final Watermelon watermelon = new Watermelon();
    /** Six melons side by side, 46 cm apart, and over each, 40 cm up, where its far one is seen. */
    private final Row row = new Row(new Vec3(0, watermelon.rests(), 0), 6, 46, 40);
    private final Look look = new Look(new Laid(), watermelon.rind());
    private final Asked asked = new Asked(said("bench.further", 1));
    /** Looked at from in front, a little above, round the middle of the two rows. */
    private final Orbit first = new Orbit(row.middle().plus(new Vec3(0, row.lift() / 2, 0)), said("bench.yaw", 0),
            said("bench.pitch", 8), METRE * said("bench.distance", 2.1));
    /** The way the fruit is looked at now: the card's thread's own, moved by the mouse in the card's window. */
    private Orbit orbit = first;
    /** How its two windows are arranged over the screens: as they always were, until it runs. */
    private Arranged arranged = new Arranged(false, LEFT, TOP, BESIDE);

    public static void main(final String[] args) throws IOException {
        new Bench().run();
    }

    private void run() throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("Watermelons, further and further off, drawn on " + card.name(), WIDTH,
                     HEIGHT);
             Formed fruit = new Formed(card, look, watermelon.formed(96))) {
            final Controls controls = new Controls(asked, card.name());
            // a run nobody attends keeps the windows where they have always been, and as big
            arranged = new Arranged(Long.getLong("bench.settle", -1) < 0, LEFT, TOP, BESIDE);
            Platform.startup(() -> controls.shown(arranged));
            controls.seen(orbit);
            arranged.shown(window, desk);
            drawn(desk, card, window, fruit, controls);
            final String snapshot = System.getProperty("bench.snapshot", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, fruit);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final Formed fruit,
                       final Controls controls) {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            arranged.heard(window, desk);
            final Moved mouse = window.mouse();
            final Orbit moved = asked.firstWanted() ? first : moved(mouse);
            if (!moved.equals(orbit)) {
                orbit = moved;
                controls.seen(orbit);
            }
            frame(window, fruit);
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

    /**
     * One frame of the row, looked at as it is now, drawn on {@code target}: each melon of the row as from so many
     * times as far, and over it the same melon really that far off.
     */
    private void frame(final Target target, final Formed fruit) {
        final Camera camera = orbit.camera(40, 1);
        final double aspect = (double) target.width() / target.height();
        final double further = asked.further();
        target.drawn(day.sky(), new Framed(look.laid(), camera, day.light()).filling(aspect), pass -> {
            for (int which = 0; which < row.count(); which++) {
                final double times = row.times(which, further);
                fruit.drawn(pass, row.near(which), camera, times);
                fruit.drawn(pass, row.far(which, times, camera.eye()), camera);
            }
        });
    }

    /** The number the property called {@code name} says, or else {@code otherwise}. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
