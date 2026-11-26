package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.life.body.Move;
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
 * The body bench on the card: the body shop's body standing on the ground in daylight, its skin, muscles, tendons and
 * bones each drawn in the material lab's tissue, with shadows; each group of its parts as deep as chosen, so the raw
 * skull may be seen on the skin, say. It moves as the motion bench's does, walking, running or marching, or stopped at
 * any point of its stride: going, its skeleton goes with it, every frame; stopped, its skin and muscles are posed as it
 * stands, away from the screen, and shown when they are, the bones alone shown meanwhile. Drawn in a window of the
 * card's own, with a JavaFX window beside it ({@link BodyConsole}): the mouse in the card's window turns the view (drag
 * to turn, right-drag or Shift-drag to move across, scroll to come nearer). The body is made away from the screen, a
 * long while ({@link BodyMaker}); the ground is drawn meanwhile. Closing either window ends the bench.
 *
 * <p>{@code -Dbody.head}, {@code -Dbody.neck}, {@code -Dbody.trunk}, {@code -Dbody.arms}, {@code -Dbody.legs}
 * (skin, muscles, skeleton, hidden) how deep each group is drawn first; {@code -Dbody.speed} (cm/s), {@code
 * -Dbody.at} (where in its stride, 0 to 1) and {@code -Dbody.going} how it moves first; {@code -Dbench.yaw}, {@code
 * -Dbench.pitch}, {@code -Dbench.distance} (cm) and {@code -Dbench.height} (cm) where it is seen from; the light's
 * options as the material bench has them ({@code bench.shadows}, {@code bench.sun.high}, {@code bench.sun.round});
 * {@code -Dbench.settle=} so many thousandths of a second after its skin is first shown ends it; {@code
 * -Dbench.snapshot.card=} a file saves the card's picture then; the helmet as {@link FirstShown} says.</p>
 */
public final class BodyBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 900;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 700;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double NEAREST = 15;
    private static final double FURTHEST = 2000;
    /** The longest a frame is taken to be, seconds: a stall does not throw the body on. */
    private static final double LONGEST = 0.05;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final AtomicReference<BodyShown> shown = new AtomicReference<>(new FirstShown().of());
    private final AtomicReference<Motion> motion = new AtomicReference<>(new Motion(Move.WALK,
            said("body.speed", 0), Boolean.getBoolean("body.going"), said("body.at", 0), 1, true));
    private final AtomicReference<BodyMaker.Made> made = new AtomicReference<>();
    private final Moving moving = new Moving();
    private Orbit orbit = new Orbit(new Vec3(0, said("bench.height", 95), 0), said("bench.yaw", 20),
            said("bench.pitch", 6), said("bench.distance", 300));
    private Posing posing;
    /** The stopped body last asked to be posed, and the one whose skin and muscles are on the card. */
    private Motion.Still askedFor;
    private Motion.Still onCard;

    public static void main(final String[] args) throws IOException {
        new BodyBench().run();
    }

    private void run() throws IOException {
        bus.subscribe(BodyShown.class, shown::set, Bus.HERE);
        bus.subscribe(Motion.class, motion::set, Bus.HERE);
        final BodyConsole console = new BodyConsole(bus, asked, shown.get(), motion.get());
        final Thread making = new Thread(this::make, "the body");
        making.setDaemon(true);
        making.start();
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The body bench, drawn on " + card.name(), WIDTH, HEIGHT);
             BodyView view = new BodyView(card, new Laid().asking(card.rays()))) {
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
            if (posing != null) {
                posing.close();
            }
            Platform.exit();
        }
    }

    /**
     * The body made, away from the screen, each step told as it begins; if it cannot be, why, told at once, not kept
     * until the bench ends, and the console no longer working.
     */
    private void make() {
        final long began = System.nanoTime();
        try {
            final BodyMaker.Made body = new BodyMaker().made(step -> {
                System.out.println("making " + step);
                bus.publish(new Bus.Working(true, step));
            });
            System.out.printf(Locale.ROOT, "the body made in %.1f s%n", (System.nanoTime() - began) / BILLION);
            made.set(body);
        } catch (final RuntimeException failed) {
            failed.printStackTrace();
            bus.publish(new Bus.Working(false, "the body not made: " + failed.getMessage()));
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final BodyView view,
                       final BodyConsole console) {
        final long settle = Long.getLong("bench.settle", -1);
        long fleshedAt = -1;
        long last = System.nanoTime();
        long told = last;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            final long now = System.nanoTime();
            final double seconds = Math.min(LONGEST, (now - last) / BILLION);
            last = now;
            if (posing == null && made.get() != null) {
                view.boned(made.get());
                posing = new Posing(made.get());
            }
            moved(view, seconds);
            final boolean ready = onCard != null || motion.get().playing() && posing != null;
            fleshedAt = fleshedAt < 0 && ready ? now : fleshedAt;
            orbit = moved(window.mouse());
            skies.stepped(seconds);
            frame(window, view);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            if (++frames % EVENED == 0) {
                bus.publish(new Bus.Figures("Drawn on " + card.name() + "\n" + view.said()
                        + String.format(Locale.ROOT, "\nA frame: %.1f ms", (now - told) / MILLION / EVENED)));
                console.sky(skies.said());
                told = now;
            }
            if (settle >= 0 && fleshedAt >= 0 && (now - fleshedAt) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The body moved on {@code seconds} as it is asked to move. Going, where it has got to in its stride is said on
     * the bus. Stopped, with nothing being dragged, its skin and muscles are asked to be posed as it stands, if they
     * are not yet; those posed are sent to the card when they come.
     */
    private void moved(final BodyView view, final double seconds) {
        final Motion m = motion.get();
        moving.moved(m, seconds);
        if (m.playing()) {
            bus.publish(new BodyConsole.Strided(moving.cycle()));
        }
        if (posing == null) {
            return;
        }
        if (!m.playing() && m.settled() && !m.still().equals(askedFor)) {
            askedFor = m.still();
            bus.publish(new Bus.Working(true, "posing its skin and muscles"));
            posing.ask(askedFor, moving.rig());
        }
        posing.taken().ifPresent(p -> {
            view.fleshed(p.flesh());
            onCard = p.still();
            bus.publish(new Bus.Working(false, "posing its skin and muscles"));
            System.out.printf(Locale.ROOT, "skin and muscles posed in %,d ms%n", p.ms());
        });
    }

    /** The way of looking, moved as {@code mouse} says the mouse moved: turned, or carried across, and scrolled. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, the body as it is held now, drawn on {@code target}. */
    private void frame(final Target target, final BodyView view) {
        final Motion m = motion.get();
        final boolean fleshed = !m.playing() && m.settled() && m.still().equals(onCard);
        view.drawn(target, shown.get(), new BodyView.Held(moving.rig(), moving.lift(), fleshed),
                orbit.camera(40, 5), skies.lit(), asked.shadowed(), asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
