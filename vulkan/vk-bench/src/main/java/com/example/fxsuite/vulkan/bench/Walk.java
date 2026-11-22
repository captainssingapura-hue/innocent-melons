package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Journal;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Finish;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shadows;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The avatar walking about a place, drawn by the card: the way anything being built is gone round and looked at
 * close to. The avatar is the engine's own: it is moved by the engine's simulation, in the engine's fixed steps,
 * bumping into what the place puts in its way and sliding round it, and it holds the pose the engine's walk gives
 * it. Nothing here writes to the simulation but the keys and the mouse: what is drawn is read from it, between its
 * last two steps, as the engine's own renderer reads it. It is heard as well: the engine's own sound, its footsteps,
 * landings and bumps, made from what the simulation says happened.
 *
 * <p>The keys and the mouse are the engine's game's own, in the card's window: the mouse turns the avatar with no
 * button held, W and S go ahead and back, A and D sideways, at full speed unless Shift is held; Space jumps; F
 * takes off on the jets built into its back, or lands, and flying Space climbs and Ctrl sinks; Q and E
 * swing the view round and C puts it back; the wheel brings it nearer; Esc lets the mouse go or takes it back. A
 * JavaFX window beside it says so. For a run with nobody at the keys: {@code -Dbench.script=} a script as the engine
 * writes them drives the avatar and ends the run when it ends, {@code -Dbench.settle=} so many thousandths of a
 * second ends it after that long, and {@code -Dbench.snapshot=} a file saves, as it ends, the picture the card draws
 * for its window.
 *
 * <p>What stands in the place, and the avatar, cast shadows on the ground and on each other: the sun's own picture
 * of the ground round the avatar is drawn first each frame, and the frame is drawn reading it. A box in the JavaFX
 * window switches them off and on; {@code -Dbench.shadows=false} begins with them off. The picture is
 * {@code -Dbench.shadows.pixels=} pixels each way (4096 unless said) and covers {@code -Dbench.shadows.across=}
 * metres of ground round the avatar (120 unless said): the fewer metres, the finer a shadow's edge, and beyond
 * them there are no shadows.
 *
 * <p>Where the sun stands is set in the JavaFX window too, by two sliders: how high it is, and which way round. It
 * begins as the bench's day has it, or as {@code -Dbench.sun.high=} and {@code -Dbench.sun.round=} say, in degrees.
 * The sun keeps its brightness and its colour wherever it stands; the sky does not change with it.
 *
 * <p>A second box there makes it night, and day again: the sky dark, the moon where the sun stood and casting the
 * same shadows by a dim blue light, and little light on anything else, so that what gives light of its own (the
 * avatar's visor, its beacon, its jets) stands out. {@code -Dbench.night=true} begins by night.
 *
 * <p>Or the sun and the moon stand where they truly do, over a place on the Earth at a date and an hour, the hour
 * running on as fast as its clock is set to run ({@link Skies}); all set in the JavaFX window. {@code
 * -Dbench.sky=true} begins that way; {@code -Dbench.sky.at=} so far north, a comma, and so far east, in degrees;
 * {@code -Dbench.sky.when=} a date and an hour as {@code 2026-06-21T12:00}, by the clocks of {@code
 * -Dbench.sky.zone=}; and {@code -Dbench.sky.speed=} how many times as fast as time the clock runs.
 */
public final class Walk {

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
    private static final double METRE = 100;

    private final String title;
    private final BiFunction<Card, Laid, Place> placing;
    private final Laid laid = new Laid();
    /** The avatar as it is drawn here: 1.9 m to the top of its head, its body gold and its arms and legs titanium. */
    private final Courier courier = new Courier(190, Finish.GILT);
    /** The bench's day, for where its sun stands: where the sliders begin unless the run is told otherwise. */
    private final Light noon = new Daylight().light();
    /** What the JavaFX window asks: shadows or none, and where the sun stands; to begin with, as the run is told. */
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    /** Each frame's light and sky: by the sliders, or by the almanac and the clock. */
    private final Skies skies = new Skies(asked);
    /** How wide the ground the sun's picture covers round the avatar is, and as far through, in centimetres. */
    private final double shaded = said("bench.shadows.across", 120) * METRE;
    private final Driven driven = new Driven(System.getProperty("bench.script", ""));
    /** The avatar after the simulation's last step, and after the step before it. */
    private Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), false);
    private Avatar before = avatar;
    private Clock clock = Clock.START;
    private long step;
    private final Following following = new Following(avatar.body());
    /** What the mouse has turned the avatar by and no step has yet taken, in degrees. */
    private double turning;
    /** What a script has tilted the view by and no frame has yet shown, in degrees. */
    private double tilting;
    private boolean taken;
    /** What happens in the simulation, step by step, as the engine keeps it: read here by the sound alone. */
    private final Journal journal = new Journal();
    private final Journal.Reader forSound = journal.reader();
    /** How long a run with nobody watching lasts, in thousandths of a second; less than nothing, for no such run. */
    private final long settle = Long.getLong("bench.settle", -1);
    /** What is in the avatar's way in the place it walks, once the place is made. */
    private Obstacles inTheWay = Obstacles.NONE;

    /**
     * A walk called {@code title}, about the place {@code placing} makes on the card it is given, with the layouts
     * it is given. The avatar starts at the world's middle, facing along the world.
     */
    public Walk(final String title, final BiFunction<Card, Laid, Place> placing) {
        this.title = title;
        this.placing = placing;
    }

    /** The walk, until a window is closed, the script or the time is up, or the validation layer speaks. */
    public void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.clocked(Boolean.getBoolean("bench.sky"));
        asked.quickened(said("bench.sky.speed", asked.quickened()));
        asked.almanac(asked.almanac().told(System.getProperty("bench.sky.at", ""),
                System.getProperty("bench.sky.when", ""), System.getProperty("bench.sky.zone", "")));
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window(title + ", drawn on " + card.name(), WIDTH, HEIGHT);
             Place place = placing.apply(card, laid);
             Cast cast = new Cast(card, laid);
             Shadows shadows = new Shadows(card, laid, Integer.getInteger("bench.shadows.pixels", 4096));
             Hearing hearing = Hearing.open(driven.scripted() || settle >= 0)) {
            final Guide guide = new Guide(asked, card.name(), List.of("In the card's window:",
                    "  mouse: turn (Esc lets it go)", "  W, S: ahead and back", "  A, D: sideways", "  Shift: walk",
                    "  Space: jump", "  F: fly, or land; flying, Space climbs and Ctrl sinks",
                    "  Q, E: swing the view round; C: back behind", "  scroll: nearer or further"));
            Platform.startup(() -> guide.shown(LEFT, TOP));
            window.shown(LEFT + BESIDE, TOP);
            final Staged staged = new Staged(place, cast, shadows);
            drawn(desk, card, window, staged, guide, hearing);
            final String snapshot = System.getProperty("bench.snapshot", "");
            if (!snapshot.isEmpty()) {
                try (Canvas canvas = card.canvas(window.width(), window.height())) {
                    frame(canvas, staged, avatar);
                    ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
                }
            }
        } finally {
            Platform.exit();
        }
    }

    private void drawn(final Desk desk, final Card card, final Window window, final Staged staged,
                       final Guide guide, final Hearing hearing) {
        inTheWay = staged.place().inTheWay();
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
            following.followed(shown.body(), (taken ? -mouse.down() * SENSITIVITY : 0) + tilting,
                    (driven.held(Key.E) ? 1 : 0) - (driven.held(Key.Q) ? 1 : 0), mouse.scrolled(),
                    driven.taken(Key.C), seconds);
            tilting = 0;
            frame(window, staged, shown);
            // heard as the engine's game hears it: what happened in the steps just taken, from where the view is
            hearing.hear(shown, forSound.read().events(), following.chase());
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            if (frames % EVENED == 0) {
                guide.told((now - told) / MILLION / EVENED);
                guide.sky(skies.said());
                told = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /**
     * The simulation taken on by the steps {@code seconds} have earned, each as the keys or the script say, among
     * what is in the way; how far on the step after those is, for drawing between the last two.
     */
    private double stepped(final double seconds) {
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int done = 0; done < ticked.steps() && !driven.over(step); done++) {
            final double turn = turning / (ticked.steps() - done);
            turning -= turn;
            if (driven.flies(step + 1)) {
                // its jets are part of it, with no wings to catch on anything: the engine's body flies as it is
                avatar = new Avatar(avatar.body().toggledFlight(), false);
            }
            tilting += driven.tilt(step + 1);
            before = avatar;
            final Avatar.Stepped stepped = avatar.step(driven.controls(step + 1, turn, following.lookUp()),
                    Clock.STEP, inTheWay);
            avatar = stepped.avatar();
            step++;
            journal.append(step, stepped.events());
            skies.stepped(Clock.STEP);
        }
        return ticked.blend();
    }

    /**
     * One frame: the ground, the place, and the avatar where {@code shown} has it, drawn on {@code target}. With
     * shadows, the place and the avatar are drawn first into the sun's picture of the ground round the avatar, and
     * the frame is drawn reading it; the ground casts none.
     */
    private void frame(final Target target, final Staged staged, final Avatar shown) {
        final Camera camera = following.camera(inTheWay);
        final List<Courier.Placed> figure = courier.placed(shown.body(), shown.body().thrust(),
                Gait.of(shown, following.yaw(), following.lookUp()));
        final double aspect = (double) target.width() / target.height();
        final Daylight time = skies.lit();
        final Light lit = time.light();
        final Framed framed = new Framed(laid, camera, lit);
        final Consumer<Pass> standing = pass -> {
            staged.place().drawn(pass, camera);
            staged.cast().figure(pass, figure, camera);
        };
        final Consumer<Pass> all = pass -> {
            staged.cast().ground(pass, camera);
            standing.accept(pass);
        };
        if (asked.shadowed()) {
            final int pixels = staged.shadows().pixels();
            final Beam beam = new Beam(new Vec3(shown.body().position().x(), 0, shown.body().position().z()),
                    lit.sunTowards(), shaded, shaded).settled(pixels);
            target.drawn(time.sky(), framed.filling(aspect, beam, pixels),
                    staged.shadows().cast(framed, beam, standing), all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }

    /** What a frame is drawn of, on the card: the place, the avatar's boxes and the ground, and the shadows. */
    private record Staged(Place place, Cast cast, Shadows shadows) {
    }
}
