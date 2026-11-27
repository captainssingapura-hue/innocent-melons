package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.vulkan.Motion;
import com.example.fxsuite.life.body.vulkan.Moving;
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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The avatar bench on the card: a line-up of figures standing side by side on the ground in daylight, with shadows,
 * all moving alike, walking, running or marching on the spot, or standing; each as tall as asked. Its first batch two
 * robots built as the body shop's male model is ({@link AvatarMaker}): the framed one shown in a moment, the shelled
 * one when its shell is made. Drawn in a window of the card's own, with a JavaFX window beside it
 * ({@link LineupConsole}): the mouse in the card's window turns the view about the picked figure (drag to turn,
 * right-drag or Shift-drag to move across, scroll to come nearer); picking another looks at it. Closing either window
 * ends the bench.
 *
 * <p>{@code -Dbench.yaw}, {@code -Dbench.pitch} and {@code -Dbench.distance} (cm) where it is seen from;
 * {@code -Dlineup.tall} (cm) how tall each stands first, {@code -Dlineup.picked} which is picked first (counted from
 * 0), {@code -Dlineup.speed} (cm/s) and {@code -Dlineup.going} how
 * they move first; the light's options as the material bench has them ({@code bench.shadows},
 * {@code bench.shadows.mode}, {@code bench.sun.high}, {@code bench.sun.round}); {@code -Dbench.settle=} so many
 * thousandths of a second after the whole batch is made ends it; {@code -Dbench.snapshot.card=} a file saves the
 * card's picture then.</p>
 */
public final class AvatarBench {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 850;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 660;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    private static final double NEAREST = 15;
    private static final double FURTHEST = 3000;
    private static final double LONGEST = 0.05;
    /** How high up a figure the view looks, as a share of how tall it stands. */
    private static final double LOOKED_AT = 0.5;

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final AvatarMaker maker = new AvatarMaker();
    private final AtomicReference<LineupShown> shown = new AtomicReference<>(first());
    private final AtomicReference<Motion> motion = new AtomicReference<>(new Motion(Move.WALK,
            said("lineup.speed", 0), Boolean.getBoolean("lineup.going"), 0, 1, true));
    private final ConcurrentLinkedQueue<Avatar> made = new ConcurrentLinkedQueue<>();
    private final Moving moving = new Moving();
    private Orbit orbit = new Orbit(new Vec3(0, 0, 0), said("bench.yaw", 20),
            said("bench.pitch", 6), said("bench.distance", 420));
    private int lookedAt = -1;

    public static void main(final String[] args) throws IOException {
        new AvatarBench().run();
    }

    private void run() throws IOException {
        bus.subscribe(LineupShown.class, shown::set, Bus.HERE);
        bus.subscribe(Motion.class, motion::set, Bus.HERE);
        final LineupConsole console = new LineupConsole(bus, asked, maker.batch(), shown.get(), motion.get());
        final Thread making = new Thread(this::make, "the line-up");
        making.setDaemon(true);
        making.start();
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The avatar bench, drawn on " + card.name(), WIDTH, HEIGHT);
             LineupView view = new LineupView(card, new Laid().asking(card.rays()))) {
            asked.offered(view.offered());
            asked.shadowing(System.getProperty("bench.shadows.mode", ""));
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

    /** The line-up to begin with: each of the batch as tall as the run was told, the one it was told picked. */
    private LineupShown first() {
        LineupShown all = new LineupShown(maker.batch().size());
        final double tall = said("lineup.tall", all.tall().getFirst());
        for (int i = 0; i < all.tall().size(); i++) {
            all = all.picking(i).standing(tall);
        }
        return all.picking(Integer.getInteger("lineup.picked", 0));
    }

    /** The batch made, away from the screen, each step told as it begins; if it cannot be, why, told at once. */
    private void make() {
        final long began = System.nanoTime();
        try {
            maker.made(step -> {
                System.out.println("making " + step);
                bus.publish(new Bus.Working(true, "making " + step));
            }, avatar -> {
                System.out.printf(Locale.ROOT, "%s made at %.1f s, %,d triangles%n", avatar.kind().title(),
                        (System.nanoTime() - began) / BILLION, avatar.triangles());
                made.add(avatar);
            });
            bus.publish(new Bus.Working(false, "the line-up made"));
        } catch (final RuntimeException failed) {
            failed.printStackTrace();
            bus.publish(new Bus.Working(false, "the line-up not made: " + failed.getMessage()));
        }
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final LineupView view,
                       final LineupConsole console) {
        final long settle = Long.getLong("bench.settle", -1);
        final int all = maker.batch().size();
        long madeAt = -1;
        long last = System.nanoTime();
        long told = last;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            final long now = System.nanoTime();
            final double seconds = Math.min(LONGEST, (now - last) / BILLION);
            last = now;
            for (Avatar a = made.poll(); a != null; a = made.poll()) {
                view.add(a);
            }
            madeAt = madeAt < 0 && view.count() == all ? now : madeAt;
            moving.moved(motion.get(), seconds);
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
            if (settle >= 0 && madeAt >= 0 && (now - madeAt) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The way of looking, moved as {@code mouse} says: turned, or carried across, and scrolled; brought round to the
     * picked figure when another is picked, as far off and from the same side.
     */
    private Orbit moved(final Moved mouse) {
        final LineupShown s = shown.get();
        Orbit o = orbit;
        if (s.picked() != lookedAt) {
            lookedAt = s.picked();
            o = new Orbit(new Vec3(s.along(lookedAt), LOOKED_AT * s.tall().get(lookedAt), 0), o.yaw(), o.pitch(),
                    o.distance());
        }
        final Orbit dragged = !mouse.dragged() ? o
                : mouse.other() ? o.carried(mouse.across(), mouse.down())
                : o.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), NEAREST, FURTHEST);
    }

    /** One frame of what is on show, the figures as they are held now, drawn on {@code target}. */
    private void frame(final Target target, final LineupView view) {
        view.drawn(target, shown.get(), moving.rig(), moving.lift(), orbit.camera(40, 5), skies.lit(),
                asked.shadowed(), asked.shadowing());
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
