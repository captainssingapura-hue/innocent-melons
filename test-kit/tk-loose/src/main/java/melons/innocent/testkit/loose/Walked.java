package melons.innocent.testkit.loose;

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
import com.example.fxsuite.vulkan.flight.Chasing;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.walk.Standing;

import java.util.List;
import java.util.Optional;

/**
 * The walk's own figure, gone by the core: what a walk shows and follows, where the one who walks is the core's
 * walker and not the old engine's avatar. Each step of the walk is one tick of the trial's world, things and walker
 * together, the keys of the step turned into what the walker is asked. Nothing of the old engine is stepped.
 *
 * <p>What is borrowed from the old engine is only how a figure is drawn: its pacer, which says how many ticks a
 * frame takes and reads the one clock; and its gait, asked how a body going so holds itself, for the bot's limbs.
 * The camera is the flights' own, which follows behind and is swung, tilted and brought nearer as theirs is.
 */
final class Walked implements Roaming {

    /** How far over its feet a walk says its head is. */
    private static final double EYES = 160;

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Skies skies;
    private final Trial trial;
    private final Bot bot;
    private final Spot first;
    private final double facing;
    private Standing now;
    private Standing before;
    private double phase;
    private double phased;
    private Chasing chasing = new Chasing();
    private Chasing chased = chasing;
    private Clock clock = Clock.START;
    private long step;

    /** A walk of {@code trial}, its figure set down at {@code first} facing {@code facing} degrees. */
    Walked(final Source source, final Optional<Keeping> keeping, final Skies skies, final Trial trial,
           final Bot bot, final Spot first, final double facing) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.trial = trial;
        this.bot = bot;
        this.first = first;
        this.facing = facing;
        trial.second().set(first, facing);
        this.now = trial.second().standing().orElseThrow();
        this.before = now;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        // what is in the walker's way is the core's to find, among the world's own things
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

    /** One step of the walk: the keys become what the walker is asked, and the trial's world is a tick on. */
    private void taken(final Step told) {
        if (!trial.second().set()) {
            trial.second().set(first, facing);
        }
        before = trial.second().standing().orElseThrow();
        chased = chasing;
        phased = phase;
        trial.second().wished(new Keyed(told.held(), told.pressed()).wish(told.turn()));
        trial.ticked();
        now = trial.second().standing().orElseThrow();
        phase += (now.stride() - before.stride()) / new Paced().stride(now.going().length()) * 2 * Math.PI;
        chasing = chasing.stepped(told, Clock.STEP);
        step++;
        skies.stepped(Clock.STEP);
        final Where stood = new Where(now.feet().across(), now.feet().up(), now.feet().along());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the figure was at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    /** How many steps the walk has taken: each one tick of the trial's world. */
    long steps() {
        return step;
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

    /** Where its feet are, between the last two steps. */
    private Spot feet(final double blend) {
        return before.feet().plus(now.feet().minus(before.feet()).times(blend));
    }

    @Override
    public Vec3 head(final double blend) {
        final Spot feet = feet(blend);
        return new Vec3(feet.across(), feet.up() + EYES, feet.along());
    }

    @Override
    public double yaw(final double blend) {
        return before.yaw() + ((now.yaw() - before.yaw() + 540) % 360 - 180) * blend;
    }

    @Override
    public double tilt(final double blend) {
        return new Chasing().down() - chasing.between(chased, blend).down();
    }

    @Override
    public Camera camera(final double blend) {
        return chasing.between(chased, blend).camera(head(blend), yaw(blend), (x, z) -> 0);
    }

    @Override
    public Vec3 foot(final double blend) {
        final Spot feet = feet(blend);
        return new Vec3(feet.across(), feet.up(), feet.along());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        final Spot feet = feet(blend);
        final double yaw = yaw(blend);
        final double way = Math.toRadians(yaw);
        final Spot going = now.going();
        // a body of the old engine's where the walker is, its way up (down is up), only for its gait to be asked
        final Body body = new Body(new com.example.fxsuite.avatar.sim.Vec3(feet.across(), -feet.up(), feet.along()),
                yaw, yaw, going.across() * Math.sin(way) + going.along() * Math.cos(way),
                going.across() * Math.cos(way) - going.along() * Math.sin(way), 0, going.up(), false, now.afoot(),
                phased + (phase - phased) * blend, step * Clock.STEP);
        return bot.placed(body, 0, Gait.of(new Avatar(body, false), yaw, tilt(blend)));
    }
}
