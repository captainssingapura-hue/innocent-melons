package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.vulkan.bench.film.Encoder;
import com.example.fxsuite.vulkan.bench.film.Shots;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Played;
import com.example.fxsuite.vulkan.bench.play.Replayed;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.subject.Courier;
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
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
 * <p>A run can be kept and played again, and filmed ({@link Run}, {@link Filming}). {@code -Dbench.record=} a file
 * keeps each step of the run as it is taken, whoever drives it; {@code -Dbench.replay=} a file plays a kept run
 * back, step for step, and stops, saying where, if it goes astray. {@code -Dbench.film=} a file makes the run (a
 * script or a kept run, which end of themselves) into a video, drawn offscreen at the run's own time, with no
 * windows: {@code -Dbench.film.rate=} frames a second (30), {@code -Dbench.film.width=} and {@code
 * -Dbench.film.height=} pixels (1280 by 720), {@code -Dbench.film.shots=} a file of where the film's camera is,
 * shot by shot (as played, unless said), and {@code -Dbench.film.ffmpeg=} the encoder ({@code ffmpeg}, as found).
 * A film is silent: the engine's recording of sound keeps time by the clock, not by the run.
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
    /** How long a film's frame waits, at most, for its place to settle, in thousandths of a second. */
    private static final long WAITING = 20_000;

    private final String title;
    private final BiFunction<Card, Laid, Place> placing;
    /** What moves the figures: the engine's own avatar, unless the walk is told otherwise. */
    private final Roamer roamer;
    /** Whether it is opened beside a bench whose JavaFX is already running. */
    private boolean beside;
    /** The layouts of every look: asking rays, once the card is open, where the card answers them. */
    private Laid laid = new Laid();
    /** The bench's day, for where its sun stands: where the sliders begin unless the run is told otherwise. */
    private final Light noon = new Daylight().light();
    /** What the JavaFX window asks: shadows or none, and where the sun stands; to begin with, as the run is told. */
    private final Asked asked = new Asked(1, Boolean.parseBoolean(System.getProperty("bench.shadows", "true")),
            new Asked.Sun(said("bench.sun.high", noon.sunHigh()), said("bench.sun.round", noon.sunRound())));
    /** Each frame's light and sky: by the sliders, or by the almanac and the clock. */
    private final Skies skies = new Skies(asked);
    /** How wide the ground the sun's picture covers round the avatar is, and as far through, in centimetres. */
    private final double shaded = said("bench.shadows.across", 120) * METRE;
    /** The keys held in the card's window. */
    private final Driven driven = new Driven();
    /** Someone playing, told what was done each frame; the run's source unless a script or a kept run is given. */
    private final Played played = new Played();
    private boolean taken;
    /** How long a run with nobody watching lasts, in thousandths of a second; less than nothing, for no such run. */
    private final long settle = Long.getLong("bench.settle", -1);

    /**
     * A walk called {@code title}, about the place {@code placing} makes on the card it is given, with the layouts
     * it is given. The avatar starts at the world's middle, facing along the world.
     */
    public Walk(final String title, final BiFunction<Card, Laid, Place> placing) {
        this(title, placing, new Afoot());
    }

    /** The same, its figures moved as {@code roamer} moves them. */
    public Walk(final String title, final BiFunction<Card, Laid, Place> placing, final Roamer roamer) {
        this.title = title;
        this.placing = placing;
        this.roamer = roamer;
    }

    /**
     * The same walk, opened beside a bench whose JavaFX is already running: its window is shown on that, and when
     * the walk ends only its own windows go.
     */
    public Walk beside() {
        beside = true;
        return this;
    }

    /** Asked to end, as closing a window of its asks. From any thread. */
    public void stop() {
        asked.stop();
    }

    /** The walk, until a window is closed, the run or the time is up, or the validation layer speaks. */
    public void run() throws IOException {
        asked.night(Boolean.getBoolean("bench.night"));
        asked.logged(!System.getProperty("bench.log", "").isBlank());
        asked.clocked(Boolean.getBoolean("bench.sky"));
        asked.quickened(said("bench.sky.speed", asked.quickened()));
        asked.shadowing(System.getProperty("bench.shadows.mode", ""));
        asked.almanac(asked.almanac().told(System.getProperty("bench.sky.at", ""),
                System.getProperty("bench.sky.when", ""), System.getProperty("bench.sky.zone", "")));
        final String kept = System.getProperty("bench.record", "");
        try (Keeping keeping = kept.isEmpty() ? null : new Keeping(Path.of(kept), title)) {
            final Roaming roaming = roamer.roaming(source(), Optional.ofNullable(keeping), skies);
            final String film = System.getProperty("bench.film", "");
            if (film.isEmpty()) {
                walked(roaming);
            } else {
                filmed(roaming, Path.of(film));
            }
        }
    }

    /** Where the run's steps come from: a kept run, a script, or the keys and the mouse. */
    private Source source() {
        final String replay = System.getProperty("bench.replay", "");
        final String script = System.getProperty("bench.script", "");
        return !replay.isEmpty() ? new Replayed(Path.of(replay)) : !script.isEmpty() ? new Scripted(script) : played;
    }

    /** The walk in its windows, the card's and JavaFX's, until it ends. */
    private void walked(final Roaming roaming) throws IOException {
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window(title + ", drawn on " + card.name(), WIDTH, HEIGHT);
             Place place = placing.apply(card, laid = new Laid().asking(card.rays()));
             Cast cast = new Cast(card, laid);
             Shaded shadows = new Shaded(card, laid, Integer.getInteger("bench.shadows.pixels", 4096));
             Overlay overlay = new Overlay(card, laid);
             Carded carded = new Carded(roaming, card, laid);
             Hearing hearing = Hearing.open(roaming.ends() || settle >= 0)) {
            final Guide guide = new Guide(asked, card.name(), roamer.keys(), place.dials());
            asked.offered(shadows.offered());
            try (Shown shown = new Shown(guide, beside, LEFT, TOP)) {
                window.shown(LEFT + BESIDE, TOP);
                final Staged staged = new Staged(place, cast, shadows, overlay);
                drawn(desk, card, window, staged, guide, hearing, roaming);
                final String snapshot = System.getProperty("bench.snapshot", "");
                if (!snapshot.isEmpty()) {
                    try (Canvas canvas = card.canvas(window.width(), window.height())) {
                        frame(canvas, staged, roaming, roaming.camera(1), 1);
                        ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                                new File(snapshot));
                    }
                }
            }
        }
    }

    private void drawn(final Desk desk, final Card card, final Window window, final Staged staged,
                       final Guide guide, final Hearing hearing, final Roaming roaming) {
        roaming.inTheWay(staged.place().inTheWay());
        final long began = System.nanoTime();
        long last = began;
        long told = began;
        int frames = 0;
        while (window.open() && !asked.stopped() && !roaming.over()) {
            desk.heard();
            driven.heard(window);
            if (!roaming.pointing() && (driven.taken(Key.ESCAPE) || frames == 0 && !roaming.ends() && settle < 0)) {
                taken = !taken;
                window.taken(taken);
            }
            final Moved mouse = window.mouse();
            // what a click means is told to the next step with the keys pressed, so a kept run plays it again
            final List<String> pressed = new ArrayList<>(driven.pressed());
            window.clicks().forEach(click -> pressed.addAll(roaming.clicked(click, window.width(), window.height())));
            // the mouse's button held, while the mouse is the walk's, is told as a key held: the trigger
            final Set<String> held = driven.held();
            if (taken && window.pressing()) {
                held.add(new Step().trigger());
            }
            played.told(held, pressed, taken ? mouse.across() * SENSITIVITY : 0,
                    taken ? -mouse.down() * SENSITIVITY : 0, mouse.scrolled());
            final long now = System.nanoTime();
            final double blend = roaming.stepped((now - last) / BILLION);
            last = now;
            frame(window, staged, roaming, roaming.camera(blend), blend);
            roaming.heard(hearing, blend);
            final List<String> said = card.heard();
            if (!said.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", said));
            }
            frames++;
            if (frames % EVENED == 0) {
                guide.told((now - told) / MILLION / EVENED, List.of(card.memory().said(), staged.place().said()));
                guide.sky(skies.said());
                told = now;
            }
            if (settle >= 0 && (now - began) / MILLION >= settle) {
                return;
            }
        }
    }

    /** The run made into a film in {@code film}, with no windows, drawn at the run's own time. */
    private void filmed(final Roaming roaming, final Path film) throws IOException {
        if (!roaming.ends()) {
            throw new IllegalArgumentException("a film is made of a run that ends of itself: give -Dbench.script= or"
                    + " -Dbench.replay=");
        }
        final String shots = System.getProperty("bench.film.shots", "");
        final Filming filming = new Filming(said("bench.film.rate", 30), Integer.getInteger("bench.film.width", 1280),
                Integer.getInteger("bench.film.height", 720),
                shots.isEmpty() ? new Shots() : new Shots(Files.readString(Path.of(shots))));
        try (Card card = new Card();
             Place place = placing.apply(card, laid = new Laid().asking(card.rays()));
             Cast cast = new Cast(card, laid);
             Shaded shadows = new Shaded(card, laid, Integer.getInteger("bench.shadows.pixels", 4096));
             Overlay overlay = new Overlay(card, laid);
             Carded carded = new Carded(roaming, card, laid);
             Encoder encoder = filming.encoder(film, System.getProperty("bench.film.ffmpeg", "ffmpeg"))) {
            asked.offered(shadows.offered());
            final Staged staged = new Staged(place, cast, shadows, overlay);
            roaming.inTheWay(place.inTheWay());
            filming.filmed(card, roaming, encoder, (target, camera, blend) -> {
                settled(place, camera);
                frame(target, staged, roaming, camera, blend);
            });
            System.out.printf("filmed %d frames, %.1f s of the run, to %s%n", encoder.frames(),
                    encoder.frames() / filming.rate(), film.toAbsolutePath());
        }
    }

    /**
     * What the run puts on the card of its own, for as long as the card is open: put on as this is made, and taken off
     * as it is closed, which is before the card is, since it is opened after it.
     */
    private record Carded(Roaming roaming) implements AutoCloseable {

        Carded(final Roaming roaming, final Card card, final Laid laid) {
            this(roaming);
            roaming.carded(card, laid);
        }

        @Override
        public void close() {
            roaming.uncarded();
        }
    }

    /**
     * One frame: the ground, the place, and every figure {@code blend} of the way between its last two steps, drawn
     * on {@code target} as {@code camera} sees it, the place readied for that camera first. A figure looks where the
     * run's player looked, wherever the camera is. With shadows, the place and the figures cast them and the ground
     * casts none; they are found the way last asked for: from the sun's picture of the ground round the lead, drawn
     * first, or by rays toward the sun.
     */
    private void frame(final Target target, final Staged staged, final Roaming roaming, final Camera camera,
                       final double blend) {
        roaming.motion().ifPresent(staged.place()::moving);
        staged.place().readied(camera);
        staged.place().rearranged().ifPresent(roaming::inTheWay);
        final List<Courier.Placed> figures = roaming.figures(blend);
        final double aspect = (double) target.width() / target.height();
        final Daylight time = skies.lit();
        final Light lit = time.light();
        final Framed framed = new Framed(laid, camera, lit);
        final Consumer<Pass> standing = pass -> {
            staged.place().drawn(pass, camera);
            staged.cast().figure(pass, figures, camera);
            roaming.drawn(pass, camera, blend);
            // last, what is seen through: it is laid over all the rest, and leaves nothing in the sun's picture
            staged.place().seenThrough(pass, camera);
        };
        final Consumer<Pass> all = pass -> {
            if (staged.place().onTheFlat()) {
                staged.cast().ground(pass, camera);
            }
            standing.accept(pass);
            roaming.overlaid(pass, staged.overlay().over(camera, aspect), blend);
        };
        if (asked.shadowed()) {
            final Beam beam = new Beam(roaming.foot(blend), lit.sunTowards(), shaded, shaded);
            staged.shadows().drawn(target, time.sky(), framed, asked.shadowing(), beam, standing, all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /**
     * {@code place} readied for {@code camera} again and again till it has settled, or {@link #WAITING} has gone by:
     * a film's frame is drawn at the run's own time, so waiting for what streams onto the card costs no frame.
     */
    private void settled(final Place place, final Camera camera) {
        final long began = System.nanoTime();
        place.readied(camera);
        while (!place.settled() && (System.nanoTime() - began) / MILLION < WAITING) {
            Thread.onSpinWait();
            place.readied(camera);
        }
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }

    /** What a frame is drawn of, on the card: the place, the avatar's boxes and the ground, and the shadows. */
    private record Staged(Place place, Cast cast, Shaded shadows, Overlay overlay) {
    }
}
