package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Lift;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.bench.play.Where;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.flight.Chasing;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.List;
import java.util.Optional;

/**
 * The GLM car driven, and the camera following it, stepped together in the engine's fixed steps, each as its source
 * tells it, kept and checked as any run is. Driven as the avatar engine drives a land vehicle ({@link Lift}), its
 * test engine: W or Up the throttle, S or Down the brake and then reverse, A and D or Left and Right the wheel, Space
 * the handbrake, Shift held to go carefully; over whatever is in its way, up ramps and onto decks, as the engine has
 * its wheels find the ground. The car itself is drawn as it is made, turned and tipped as it goes, its front wheels
 * steered and every wheel turning as it rolls ({@link CarDrawn}); its engine heard at the revs its speed makes
 * ({@link EngineHeard}); its lamps lit, F its lights and G its indicators ({@link Lamps}); its speed, revs and gear
 * over the picture. Seen behind it or from on it, as its views say ({@link DriveViews}).
 */
final class TestDriven implements Roaming {

    /** How high over the bed the camera looks; where the vehicle starts the camera off, behind and a little above. */
    private static final double LOOK = 90;
    private static final Chasing FIRST = new Chasing(0, 10, 1_100);

    private final Source source;
    private final Optional<Keeping> keeping;
    private final Optional<Skies> skies;
    private final GlmChassis chassis;
    private final CarDrawn car;
    private final EngineHeard engine;
    private final DriveViews views;
    private final Lamps lamps = new Lamps();
    private Obstacles world = Obstacles.NONE;
    private Lift now;
    private Lift before;
    private double rolled;
    private double rolledBefore;
    private Chasing chasing = FIRST;
    private Chasing chased = FIRST;
    private Clock clock = Clock.START;
    private long step;

    /**
     * {@code chassis} driven from {@code first}, told by {@code source}, drawn as {@code car}, heard as
     * {@code engine}, seen in {@code views}.
     */
    TestDriven(final Source source, final Optional<Keeping> keeping, final Optional<Skies> skies,
               final GlmChassis chassis, final CarDrawn car, final EngineHeard engine, final DriveViews views,
               final Lift first) {
        this.source = source;
        this.keeping = keeping;
        this.skies = skies;
        this.chassis = chassis;
        this.car = car;
        this.engine = engine;
        this.views = views;
        this.now = first;
        this.before = first;
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        world = blocks;
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

    private void taken(final Step told) {
        before = now;
        chased = chasing;
        final Controls asked = controls(told);
        now = now.step(asked, Clock.STEP, world, List.of());
        rolledBefore = rolled;
        rolled += now.ahead() * Clock.STEP;
        engine.driven(now.ahead(), asked.forward());
        lamps.told(told, asked.forward(), now.ahead(), Clock.STEP);
        views.told(told);
        chasing = chasing.stepped(told, Clock.STEP);
        step++;
        skies.ifPresent(sky -> sky.stepped(Clock.STEP));
        final Where stood = new Where(now.place().x(), now.place().height(), now.place().z());
        keeping.ifPresent(kept -> kept.kept(step, told, stood));
        source.check(step).ifPresent(kept -> {
            if (!kept.equals(stood)) {
                throw new IllegalStateException("the replay has gone astray at step " + step + ": the car was at "
                        + stood + ", and the kept run has it at " + kept + "; a rule has changed since it was kept");
            }
        });
    }

    /** What the driver asks of the car in a step told {@code told}: the pedals, the wheel, the handbrake. */
    private Controls controls(final Step told) {
        final double pedal = (held(told, "W", "UP") ? 1 : 0) - (held(told, "S", "DOWN") ? 1 : 0);
        final double wheel = (held(told, "D", "RIGHT") ? 1 : 0) - (held(told, "A", "LEFT") ? 1 : 0);
        final double handbrake = held(told, "SPACE") ? 1 : 0;
        return new Controls(pedal, wheel, 0, handbrake, !held(told, "SHIFT"), false, 0, List.of());
    }

    private boolean held(final Step told, final String... keys) {
        for (final String key : keys) {
            if (told.held().contains(key)) {
                return true;
            }
        }
        return false;
    }

    /** Where the car is, {@code blend} of the way from its step before the last to its last. */
    private Place place(final double blend) {
        final Place a = before.place();
        final Place b = now.place();
        final double turn = ((b.yaw() - a.yaw()) % 360 + 540) % 360 - 180;
        return new Place(a.x() + (b.x() - a.x()) * blend, a.z() + (b.z() - a.z()) * blend,
                a.height() + (b.height() - a.height()) * blend, a.yaw() + turn * blend);
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

    @Override
    public Vec3 head(final double blend) {
        final Place at = place(blend);
        return new Vec3(at.x(), at.height() + LOOK, at.z());
    }

    @Override
    public double yaw(final double blend) {
        return place(blend).yaw();
    }

    @Override
    public Camera camera(final double blend) {
        return views.camera(chasing.between(chased, blend), head(blend), place(blend), nose(blend), lean(blend));
    }

    /** How far its nose is tipped up, {@code blend} of the way between its last two steps; and its left side up. */
    private double nose(final double blend) {
        return before.tilt() + (now.tilt() - before.tilt()) * blend;
    }

    private double lean(final double blend) {
        return before.roll() + (now.roll() - before.roll()) * blend;
    }

    @Override
    public Vec3 foot(final double blend) {
        final Place at = place(blend);
        return new Vec3(at.x(), at.height() - chassis.ride(), at.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        return List.of();
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        car.carded(card, laid);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final double blend) {
        final double steer = before.steer() + (now.steer() - before.steer()) * blend;
        car.drawn(pass, camera, chassis.carried(place(blend), nose(blend), lean(blend)), steer,
                rolledBefore + (rolled - rolledBefore) * blend, lamps);
    }

    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        car.overlaid(pass, over, before.ahead() + (now.ahead() - before.ahead()) * blend, engine.rpm(),
                engine.gear());
        car.said(pass, over, views.view().title(), rolledBefore + (rolled - rolledBefore) * blend, lamps.said());
    }

    @Override
    public void uncarded() {
        car.close();
        engine.close();
    }
}
