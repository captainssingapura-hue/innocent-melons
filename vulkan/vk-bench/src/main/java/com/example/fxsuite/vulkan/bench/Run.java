package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Event;
import com.example.fxsuite.avatar.sim.Journal;
import com.example.fxsuite.avatar.sim.Keys;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.view.Chase;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.play.Where;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Motion;
import melons.innocent.core.motion.Moving;
import melons.innocent.core.motion.Spot;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * A walk's run: the engine's avatar stepped by the engine's simulation in its fixed steps, each step told what its
 * source tells it (a script, someone playing, or a run kept and read back), and the chase camera stepped with it.
 * Since the camera moves with the steps and not with the frames, the same steps make the same run, camera and all,
 * however many frames a second it is drawn at. Each step may be kept as it is taken; and where the source knows
 * where the avatar stood after a step, the run is checked against it, and stops, saying where, as soon as it has
 * gone astray.
 */
public final class Run {

    /** How far over its ceiling a flying avatar may be carried before it is brought down, in centimetres. */
    private static final double OVER = 30;
    /** How far from the engine's own middle its avatar goes before it is brought back to it, in centimetres. */
    private static final double FAR = 100_000;

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private Avatar avatar;
    private Avatar before;
    private Clock clock = Clock.START;
    private long step;
    /** How many jumps the avatar has made. */
    private int jumps;
    /** How high its jets take it at most, in centimetres: no limit, unless it is given one. */
    private double ceiling = Double.POSITIVE_INFINITY;
    /** The gear its jets are in. */
    private Gear gear = Gear.THIRD;
    private final Following following;
    /** The avatar's motion, detected from its steps: the core engine's. */
    private final Moving moving;
    /** What happens in the simulation, step by step, as the engine keeps it: read by the sound alone. */
    private final Journal journal = new Journal();
    private final Journal.Reader forSound = journal.reader();
    private Obstacles inTheWay = Obstacles.NONE;
    /** What is in the way as the place says it, in the world: {@link #inTheWay} is the same as the engine has it. */
    private Obstacles placed = Obstacles.NONE;
    /** How far across and along the engine's own middle is from the world's: see {@link #recentred}. */
    private double across;
    private double along;

    /**
     * A run told by {@code source}, kept by {@code keeping} where there is one, its sky's clock {@code skies}
     * moved on with its steps. The avatar starts at the world's middle, facing along the world.
     */
    public Run(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        this(source, keeping, Optional.of(skies), Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0));
    }

    /**
     * A run told by {@code source}, kept by {@code keeping} where there is one, the avatar standing at first as
     * {@code start} has it; moving {@code skies}' clock on with its steps where it is given one, as a companion's
     * run, which goes with another, is not.
     */
    public Run(final Source source, final Optional<Keeping> keeping, final Optional<Skies> skies, final Body start) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.avatar = new Avatar(start, false);
        this.before = avatar;
        this.following = new Following(start);
        this.moving = new Moving(spot(start), start.yaw());
        recentred();
    }

    /** What is in the avatar's way from now on, in the world. */
    public void inTheWay(final Obstacles blocks) {
        this.placed = blocks;
        if (!movable()) {
            avatar = moved(avatar, across, along);
            before = moved(before, across, along);
            across = 0;
            along = 0;
        }
        this.inTheWay = within();
    }

    /** What is in the avatar's way, in the world. */
    public Obstacles inTheWay() {
        return placed;
    }

    /**
     * The engine's avatar brought back to the engine's own middle when it has gone a kilometre from it, and how far
     * that middle now is from the world's kept: the engine's level world ends five kilometres out every way, and
     * this is how a run goes on past that with the engine as it is. Everything the run says of the avatar it says
     * in the world, so nothing else knows. Only where what is in the way is boxes on level ground, which can be
     * moved with it: among a building's own shapes, or on surveyed land, the avatar is left where the engine has it.
     */
    private void recentred() {
        final com.example.fxsuite.avatar.sim.Vec3 at = avatar.body().position();
        if (movable() && Math.max(Math.abs(at.x()), Math.abs(at.z())) > FAR) {
            across += at.x();
            along += at.z();
            avatar = moved(avatar, -at.x(), -at.z());
            before = moved(before, -at.x(), -at.z());
            inTheWay = within();
        }
    }

    /** Whether what is in the way can be moved with the avatar: boxes alone, on level ground. */
    private boolean movable() {
        return placed.volumes().isEmpty() && placed.gates().isEmpty() && placed.land().level();
    }

    /** What is in the way as the engine has it: the world's boxes, as far from the engine's middle as they are. */
    private Obstacles within() {
        return across == 0 && along == 0 ? placed : new Obstacles(placed.blocks().stream().map(block -> new Block(
                block.x() - across, block.z() - along, block.halfWidth(), block.halfDepth(), block.yaw(),
                block.bottom(), block.top(), block.toe())).toList());
    }

    /** {@code moving} moved so far across and along, otherwise as it is. */
    private Avatar moved(final Avatar moving, final double dx, final double dz) {
        return dx == 0 && dz == 0 ? moving : new Avatar(moving.body().shifted(dx, dz), moving.wearingPack(),
                moving.sweep(), moving.arms());
    }

    /**
     * The run taken on by the steps {@code seconds} have earned, each as its source tells it, among what is in the
     * way; how far on the step after those is, for drawing between the last two.
     */
    public double stepped(final double seconds) {
        final Clock.Ticked ticked = clock.advance(seconds);
        clock = ticked.clock();
        for (int done = 0; done < ticked.steps() && !over(); done++) {
            taken(source.step(step + 1, ticked.steps() - done));
        }
        return ticked.blend();
    }

    /** One step taken as {@code told} says. */
    private void taken(final Step told) {
        if (told.flies()) {
            // its jets are part of it, with no wings to catch on anything: the engine's body flies as it is
            avatar = new Avatar(avatar.body().toggledFlight(), false);
        }
        before = avatar;
        if (told.held().contains("SHIFT") || told.pressed().contains("SHIFT")) {
            gear = told.pressed().contains("UP") ? gear.higher()
                    : told.pressed().contains("DOWN") ? gear.lower() : gear;
        }
        final Controls controls = geared(Keys.controls(under(told.held()), told.pressed(), told.turn(), false,
                following.lookUp()));
        final Avatar.Stepped stepped = avatar.step(controls, Clock.STEP, inTheWay);
        avatar = stepped.avatar();
        step++;
        recentred();
        if (told.pressed().contains("V")) {
            following.viewed();
        }
        following.stepped(moved(avatar, across, along).body(), told.tilt(), told.circling(), told.scrolled(),
                told.behind());
        journal.append(step, stepped.events());
        jumps += (int) stepped.events().stream().filter(Event.Jumped.class::isInstance).count();
        skies.ifPresent(sky -> sky.stepped(Clock.STEP));
        final com.example.fxsuite.avatar.sim.Vec3 at = moved(avatar, across, along).body().position();
        moving.stepped(spot(moved(avatar, across, along).body()), avatar.body().yaw(), Clock.STEP);
        final Where stood = new Where(at.x(), at.y(), at.z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the avatar stood at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    /**
     * The same run with its jets taking it no higher than {@code centimetres} over the ground: for a place that is
     * not yet fit to be seen from high up. It is the keys that are held to it, the simulation being as it is.
     */
    public Run under(final double centimetres) {
        ceiling = centimetres;
        return this;
    }

    /**
     * The keys {@code held}, as the engine is told them: flying at its ceiling the key that climbs is let go, and
     * over it, as a climb's own way carries it a little, the key that sinks is held, till it is under again.
     */
    private Set<String> under(final Set<String> held) {
        final double high = -avatar.body().position().y();
        if (!avatar.body().flying() || high < ceiling) {
            return held;
        }
        final Set<String> lowered = new TreeSet<>(held);
        lowered.remove("SPACE");
        if (high > ceiling + OVER) {
            lowered.add("CONTROL");
        }
        return lowered;
    }

    /**
     * What the engine is told, as the jets' gear has it: flying, ahead and sideways so much of the engine's own top
     * speed as the gear's fastest is, and always flat out, the gear and not Shift saying how fast; on foot, as told.
     */
    private Controls geared(final Controls told) {
        return !avatar.body().flying() ? told : new Controls(told.forward() * gear.pushed(),
                told.sideways() * gear.pushed(), told.turn(), told.lift(), true, told.trigger(), told.aim(),
                told.actions(), told.bomb(), told.strafe());
    }

    /** Where {@code body} is, in the world, as the core engine has a spot: up the way its second number grows. */
    private Spot spot(final Body body) {
        return new Spot(body.position().x(), -body.position().y(), body.position().z());
    }

    /** How the avatar is moving, as the core engine detects it from the run's steps. */
    public Motion motion() {
        return moving.motion();
    }

    /** The gear its jets are in: Shift with the up arrow takes the next higher, with the down arrow the next lower. */
    public Gear gear() {
        return gear;
    }

    /** Whether the run is over: its source has nothing more to tell. */
    public boolean over() {
        return source.over(step);
    }

    /** How long the run is, in seconds of its own time; of someone playing, no end. */
    public double seconds() {
        return source.ends() ? source.length() * Clock.STEP : Double.POSITIVE_INFINITY;
    }

    /** Whether the run ends of itself, as a script or a kept run does. */
    public boolean ends() {
        return source.ends();
    }

    /** The avatar {@code blend} of the way from its step before the last to its last, as it is drawn. */
    public Avatar shown(final double blend) {
        return moved(avatar.between(before, blend), across, along);
    }

    /** Where the avatar's head is when it is {@code shown}, in the renderer's frame: height counted up. */
    public Vec3 head(final Avatar shown) {
        final com.example.fxsuite.avatar.sim.Vec3 at = shown.body().position();
        return new Vec3(at.x(), Chase.EYE_HEIGHT - at.y(), at.z());
    }

    /** The chase camera, stepped with the run. */
    public Following following() {
        return following;
    }

    /** What has happened in the steps taken since this was last asked, for what is heard. */
    public List<Event> happened() {
        return forSound.read().events();
    }

    /**
     * Whether the jump the avatar is in, or made last, is one led with the other leg: every second jump, so that
     * jumps made one after another change legs.
     */
    public boolean otherLeg() {
        return jumps % 2 == 0;
    }

    /** How many steps have been taken. */
    public long steps() {
        return step;
    }
}
