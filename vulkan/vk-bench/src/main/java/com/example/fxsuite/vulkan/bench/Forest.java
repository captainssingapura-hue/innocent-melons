package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Journal;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The avatar running through a wood of giant watermelons, drawn by the card. The avatar is the engine's own: it is
 * moved by the engine's simulation, in the engine's fixed steps, bumping into the melons and sliding round them,
 * and it holds the pose the engine's walk gives it. Nothing here writes to the simulation but the keys: what is
 * drawn is read from it, between its last two steps, as the engine's own renderer reads it. It is heard as well: the
 * engine's own sound, its footsteps, landings and bumps, made from what the simulation says happened.
 *
 * <p>The keys and the mouse are the engine's game's own, in the card's window: the mouse turns the avatar with no
 * button held, W and S go ahead and back, A and D sideways, at full speed unless Shift is held; Space jumps; Q and E
 * swing the view round and C puts it back; the wheel brings it nearer; Esc lets the mouse go or takes it back. A
 * JavaFX window beside it says so. For a run with nobody
 * at the keys: {@code -Dbench.script=} a script as the engine writes them drives the avatar and ends the run when
 * it ends, {@code -Dbench.settle=} so many thousandths of a second ends it after that long, and
 * {@code -Dbench.snapshot=} a file saves, as it ends, the picture the card draws for its window.
 */
public final class Forest {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 620;
    private static final int LEFT = 60;
    private static final int TOP = 120;
    private static final int BESIDE = 380;
    private static final int EVENED = 30;
    private static final double MILLION = 1e6;
    private static final double BILLION = 1e9;
    /** How many degrees the avatar turns, or the view tilts, for each unit the mouse moves: the engine's own. */
    private static final double SENSITIVITY = 0.15;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Watermelon watermelon = new Watermelon();
    private final Courier courier = new Courier();
    private final Wood wood = new Wood(11, 1100);
    private final Obstacles inTheWay = wood.obstacles();
    private final List<Mat4> melons = wood.giants().stream().map(giant -> wood.stands(giant, watermelon.rests()))
            .toList();
    private final Asked asked = new Asked(1);
    private final Driven driven = new Driven(System.getProperty("bench.script", ""));
    /** The avatar after the simulation's last step, and after the step before it. */
    private Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), false);
    private Avatar before = avatar;
    private Clock clock = Clock.START;
    private long step;
    private final Following following = new Following(avatar.body());
    /** What the mouse has turned the avatar by and no step has yet taken, in degrees. */
    private double turning;
    private boolean taken;
    /** What happens in the simulation, step by step, as the engine keeps it: read here by the sound alone. */
    private final Journal journal = new Journal();
    private final Journal.Reader forSound = journal.reader();
    /** How long a run with nobody watching lasts, in thousandths of a second; less than nothing, for no such run. */
    private final long settle = Long.getLong("bench.settle", -1);

    public static void main(final String[] args) throws IOException {
        new Forest().run();
    }

    private void run() throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The courier among giant watermelons, drawn on " + card.name(), WIDTH,
                     HEIGHT);
             Formed melon = new Formed(card, new Look(laid, watermelon.rind()), watermelon.formed(96));
             Cast cast = new Cast(card, laid);
             Hearing hearing = Hearing.open(driven.scripted() || settle >= 0)) {
            final Guide guide = new Guide(asked, card.name(), List.of("In the card's window:",
                    "  mouse: turn (Esc lets it go)", "  W, S: ahead and back", "  A, D: sideways", "  Shift: walk",
                    "  Space: jump", "  Q, E: swing the view round; C: back behind", "  scroll: nearer or further"));
            Platform.startup(() -> guide.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, melon, cast, guide, hearing);
            final String snapshot = System.getProperty("bench.snapshot", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, melon, cast, avatar);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    /** Frame after frame, until a window is closed, the script or the time is up, or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final Formed melon, final Cast cast,
                       final Guide guide, final Hearing hearing) {
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped() && !driven.over(step)) {
            desk.heard();
            driven.heard(window);
            if (driven.taken(Key.ESCAPE) || frames == 0 && !driven.scripted() && settle < 0) {
                taken = !taken;
                window.taken(taken);
            }
            final Moved mouse = window.mouse();
            final long now = System.nanoTime();
            final double seconds = (now - last) / BILLION;
            last = now;
            turning += taken ? mouse.across() * SENSITIVITY : 0;
            final Avatar shown = avatar.between(before, stepped(seconds));
            following.followed(shown.body(), taken ? -mouse.down() * SENSITIVITY : 0,
                    (driven.held(Key.E) ? 1 : 0) - (driven.held(Key.Q) ? 1 : 0), mouse.scrolled(),
                    driven.taken(Key.C), seconds);
            frame(window, melon, cast, shown);
            // heard as the engine's game hears it: what happened in the steps just taken, from where the view is
            hearing.hear(shown, forSound.read().events(), following.chase());
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            if (frames % EVENED == 0) {
                guide.told((now - told) / MILLION / EVENED);
                told = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The simulation taken on by the steps {@code seconds} have earned, each as the keys or the script say; how far
     * on the step after those is, for drawing between the last two.
     */
    private double stepped(final double seconds) {
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int done = 0; done < ticked.steps() && !driven.over(step); done++) {
            final double turn = turning / (ticked.steps() - done);
            turning -= turn;
            before = avatar;
            final Avatar.Stepped stepped = avatar.step(driven.controls(step + 1, turn, following.lookUp()),
                    Clock.STEP, inTheWay);
            avatar = stepped.avatar();
            step++;
            journal.append(step, stepped.events());
        }
        return ticked.blend();
    }

    /** One frame: the ground, the wood, and the avatar where {@code shown} has it, drawn on {@code target}. */
    private void frame(final Target target, final Formed melon, final Cast cast, final Avatar shown) {
        final Camera camera = following.camera();
        final List<Courier.Placed> figure = courier.placed(shown.body(),
                Gait.of(shown, following.yaw(), following.lookUp()));
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect), pass -> {
            cast.ground(pass, camera);
            melons.forEach(stands -> melon.drawn(pass, stands, camera, 1));
            cast.figure(pass, figure, camera);
        });
    }
}
