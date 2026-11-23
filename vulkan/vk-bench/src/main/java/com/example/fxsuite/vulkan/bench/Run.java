package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Avatar;
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

import java.util.List;
import java.util.Optional;

/**
 * A walk's run: the engine's avatar stepped by the engine's simulation in its fixed steps, each step told what its
 * source tells it (a script, someone playing, or a run kept and read back), and the chase camera stepped with it.
 * Since the camera moves with the steps and not with the frames, the same steps make the same run, camera and all,
 * however many frames a second it is drawn at. Each step may be kept as it is taken; and where the source knows
 * where the avatar stood after a step, the run is checked against it, and stops, saying where, as soon as it has
 * gone astray.
 */
public final class Run {

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private Avatar avatar;
    private Avatar before;
    private Clock clock = Clock.START;
    private long step;
    private final Following following;
    /** What happens in the simulation, step by step, as the engine keeps it: read by the sound alone. */
    private final Journal journal = new Journal();
    private final Journal.Reader forSound = journal.reader();
    private Obstacles inTheWay = Obstacles.NONE;

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
    }

    /** What is in the avatar's way from now on. */
    public void inTheWay(final Obstacles blocks) {
        this.inTheWay = blocks;
    }

    /** What is in the avatar's way. */
    public Obstacles inTheWay() {
        return inTheWay;
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
        final Controls controls = Keys.controls(told.held(), told.pressed(), told.turn(), false, following.lookUp());
        final Avatar.Stepped stepped = avatar.step(controls, Clock.STEP, inTheWay);
        avatar = stepped.avatar();
        step++;
        following.stepped(avatar.body(), told.tilt(), told.circling(), told.scrolled(), told.behind());
        journal.append(step, stepped.events());
        skies.ifPresent(sky -> sky.stepped(Clock.STEP));
        final com.example.fxsuite.avatar.sim.Vec3 at = avatar.body().position();
        final Where stood = new Where(at.x(), at.y(), at.z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the avatar stood at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
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
        return avatar.between(before, blend);
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

    /** How many steps have been taken. */
    public long steps() {
        return step;
    }
}
