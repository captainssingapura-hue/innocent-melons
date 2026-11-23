package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.play.Where;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.List;
import java.util.Optional;

/**
 * A flight: a figure on its jets ({@link Aloft}) and the camera following it ({@link Chasing}), stepped together in
 * the engine's fixed steps, each step as its source tells it (someone playing, a script, or a run kept and read
 * back), and kept above whatever is {@link Below}. Each step may be kept as it is taken, and where the source knows
 * where the figure was after a step the flight is checked against it, and stops, saying where, as soon as it has
 * gone astray. Its steps move the sky's clock on, where it is given one.
 *
 * <p>Nothing of the engine is stepped or changed: the engine's clock only shares the steps out, and the figure is
 * drawn by asking the engine's own gait how a body flying so would hold itself.
 */
public final class Flown implements Roaming {

    /** How high a figure's head is over its feet, for the camera to look at. */
    private static final double HEAD = 170;

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private final Below below;
    private final Bot bot;
    private Aloft now;
    private Aloft before;
    private Chasing chasing = new Chasing();
    private Chasing chased = chasing;
    private Clock clock = Clock.START;
    private long step;

    /**
     * A flight told by {@code source}, kept by {@code keeping} where there is one, its sky's clock {@code skies}
     * moved on, over {@code below}, the figure drawn as {@code bot}, beginning as {@code first}.
     */
    public Flown(final Source source, final Optional<Keeping> keeping, final Optional<Skies> skies, final Below below,
                 final Bot bot, final Aloft first) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.below = below;
        this.bot = bot;
        final double lowest = below.under(first.feet().x(), first.feet().z()) + first.clear();
        this.now = first.feet().y() >= lowest ? first
                : new Aloft(new Vec3(first.feet().x(), lowest, first.feet().z()), first.yaw(), first.gear());
        this.before = now;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        // nothing is in a flight's way: it keeps above what is below it, and that is all
    }

    @Override
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
        before = now;
        chased = chasing;
        now = now.stepped(told, Clock.STEP, below);
        chasing = chasing.stepped(told, Clock.STEP);
        step++;
        skies.ifPresent(sky -> sky.stepped(Clock.STEP));
        final Where stood = new Where(now.feet().x(), now.feet().y(), now.feet().z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the figure was at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    @Override
    public boolean over() {
        return source.over(step);
    }

    @Override
    public boolean ends() {
        return source.ends();
    }

    @Override
    public double seconds() {
        return source.ends() ? source.length() * Clock.STEP : Double.POSITIVE_INFINITY;
    }

    /** The figure {@code blend} of the way from its step before the last to its last. */
    public Aloft shown(final double blend) {
        return now.between(before, blend);
    }

    @Override
    public Vec3 head(final double blend) {
        return shown(blend).feet().plus(new Vec3(0, HEAD, 0));
    }

    @Override
    public double yaw(final double blend) {
        return shown(blend).yaw();
    }

    @Override
    public Camera camera(final double blend) {
        return chasing.between(chased, blend).camera(head(blend), yaw(blend), below);
    }

    @Override
    public Vec3 foot(final double blend) {
        final Vec3 feet = shown(blend).feet();
        return new Vec3(feet.x(), below.under(feet.x(), feet.z()), feet.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        final Aloft shown = shown(blend);
        // a body flying where the figure is, the engine's way up (down is up), for its gait to hold itself as it would
        final Body body = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(shown.feet().x(), -shown.feet().y(),
                shown.feet().z()), shown.yaw()).toggledFlight();
        final Avatar flying = new Avatar(body, false);
        final Chasing looking = chasing.between(chased, blend);
        return bot.placed(body, shown.thrust(), Gait.of(flying, shown.yaw(), -looking.down()));
    }

    /** How many steps have been taken. */
    public long steps() {
        return step;
    }
}
