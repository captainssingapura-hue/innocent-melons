package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The soft bench on the card: a region of the body to be simulated by finite elements, what it takes in, and its
 * muscles solved at rest, drawn by the Vulkan renderer ({@link SoftView}), with a JavaFX window beside it
 * ({@link SoftConsole}). The body still, as it is bound; the muscles taken in, their parts beyond the region darker;
 * their elements; the muscles held round them seen through; the bones; the region's edges; where the taken are cut,
 * blue, and where held to their bones, yellow. What is worked out is worked out away from the screen ({@link
 * SoftWork}): every muscle once, the region each time a slider is let go, and the solve when asked, drawn as it goes.
 * The mouse in the card's window turns the view.
 *
 * <p>{@code -Dsoft.box} (six numbers: the lowest corner, then the highest, cm, the body's frame) the region first;
 * {@code -Dsoft.share} (per cent) how much of a muscle must be in the region to be taken in; {@code -Dsoft.cell} (cm)
 * how big the elements' cubes are; {@code -Dsoft.held}, {@code -Dsoft.bones}, {@code -Dsoft.surfaces} and {@code
 * -Dsoft.elements} false to begin without them, and {@code -Dsoft.solved} false to begin at rest though there is
 * a solve kept; {@code -Dsoft.solve} true to solve the first region at once;
 * {@code -Dbench.yaw}, {@code -Dbench.pitch} and {@code -Dbench.distance} (cm) where it is seen from; {@code
 * -Dbench.settle=} so many thousandths of a second after the region is first shown, or the solve has ended if one was
 * asked, ends it; {@code -Dbench.snapshot.card=} a file saves the card's picture then.</p>
 */
public final class SoftBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 900;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 740;
    private static final double BILLION = 1e9;
    private static final double MILLION = 1e6;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(noon.sunHigh(), noon.sunRound()));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);
    private final AtomicReference<SoftAsked> shown = new AtomicReference<>(first());
    private final AtomicBoolean solveAsked = new AtomicBoolean(Boolean.getBoolean("soft.solve"));
    private final AtomicBoolean stopAsked = new AtomicBoolean();
    private final SoftWork work = new SoftWork(bus, rig);
    private Orbit orbit;
    private Vec3 lookedAt;

    public static void main(final String[] args) throws IOException {
        new SoftBench().run();
    }

    private void run() throws IOException {
        bus.subscribe(SoftAsked.class, shown::set, Bus.HERE);
        bus.subscribe(SoftConsole.Solve.class, s -> (s.go() ? solveAsked : stopAsked).set(true), Bus.HERE);
        final SoftConsole console = new SoftConsole(bus, asked, shown.get());
        work.begun();
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The soft bench, drawn on " + card.name(), WIDTH, HEIGHT);
             SoftView view = new SoftView(card, new Laid().asking(card.rays()))) {
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
            work.close();
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final SoftView view,
                       final SoftConsole console) {
        final long settle = Long.getLong("bench.settle", -1);
        final boolean waitSolve = Boolean.getBoolean("soft.solve");
        long shownAt = -1;
        long last = System.nanoTime();
        boolean sent = false;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            final long now = System.nanoTime();
            if (!sent && work.made().isPresent()) {
                view.made(work.made().get().bones(), work.made().get().pieces().wholes(), work.made().get().lift());
                sent = true;
            }
            work.asked(shown.get());
            if (stopAsked.getAndSet(false)) {
                work.stop();
            }
            if (sent && solveAsked.getAndSet(false)) {
                work.solve();
            }
            final boolean firstFound = work.taken().map(f -> {
                view.found(f);
                return true;
            }).orElse(false);
            if (shownAt < 0 && (waitSolve ? work.solved() : firstFound)) {
                shownAt = now;
            }
            orbit = moved(window.mouse(), view);
            skies.stepped((now - last) / BILLION);
            last = now;
            frame(window, view);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (++frames % 30 == 0) {
                console.sky(skies.said());
            }
            if (settle >= 0 && shownAt >= 0 && (now - shownAt) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The way of looking, moved as {@code mouse} says: turned, carried across, or scrolled; looking at the region's
     * middle again, from the same side and as far off, when the region has moved. A run with nobody at it
     * ({@code -Dbench.settle}) is not moved by the mouse, so a picture it takes is the one asked for.
     */
    private Orbit moved(final Moved mouse, final SoftView view) {
        final SoftAsked a = shown.get();
        final Vec3 middle = view.world(new Vec3((a.lo().x() + a.hi().x()) / 2, (a.lo().y() + a.hi().y()) / 2,
                (a.lo().z() + a.hi().z()) / 2));
        Orbit from = orbit;
        if (from == null) {
            from = new Orbit(middle, said("bench.yaw", 200), said("bench.pitch", 12), said("bench.distance", 110));
        } else if (!middle.equals(lookedAt)) {
            from = new Orbit(middle, from.yaw(), from.pitch(), from.distance());
        }
        lookedAt = middle;
        if (Long.getLong("bench.settle", -1) >= 0) {
            return from;
        }
        final Orbit dragged = !mouse.dragged() ? from
                : mouse.other() ? from.carried(mouse.across(), mouse.down())
                : from.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), 10, 1500);
    }

    private void frame(final Target target, final SoftView view) {
        view.drawn(target, shown.get(), orbit.camera(40, 2), skies.lit(), asked.shadowed(), asked.shadowing());
    }

    /** The region first asked: as {@code -Dsoft.box} says, else the neck, from the shoulders' tops to the skull. */
    private SoftAsked first() {
        final String[] s = System.getProperty("soft.box", "-11,45,-13,11,62,6").split(",");
        final double[] b = new double[6];
        for (int i = 0; i < 6; i++) {
            b[i] = Double.parseDouble(s[i].trim());
        }
        return new SoftAsked(new V3(b[0], b[1], b[2]), new V3(b[3], b[4], b[5]),
                said("soft.share", 25) / 100, said("soft.cell", 0.5), shows("soft.held"), shows("soft.bones"),
                shows("soft.surfaces"), shows("soft.elements"), shows("soft.solved"), shows("soft.fascia"),
                Boolean.getBoolean("soft.onePiece"));
    }

    /** Whether {@code -D}{@code name} asks that shown: so unless it says false. */
    private boolean shows(final String name) {
        return Boolean.parseBoolean(System.getProperty(name, "true"));
    }

    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
