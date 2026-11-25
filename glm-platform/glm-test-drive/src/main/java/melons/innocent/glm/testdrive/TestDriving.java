package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Lift;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.Roamer;
import com.example.fxsuite.vulkan.bench.Roaming;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;
import melons.innocent.glm.bench.OnShow;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.engine.EngineFigures;

import java.util.List;
import java.util.Optional;

/**
 * Test drives of the GLM car, for a walk to move: each run the car {@code built} driven from {@code start}, as the
 * avatar engine drives a land vehicle as big as {@code chassis} says, its engine running and its controls taken, at a
 * standstill to begin with; its wheels steered and turning as {@code wheeling} says; the engine in its bay heard,
 * unless {@code quiet}; seen in {@code view} to begin with ({@link DriveViews}).
 *
 * @param chassis  how the avatar engine knows the car
 * @param built    the car as it is made, put together
 * @param shown    which of its groups are shown, and in what
 * @param wheeling how its wheels steer and turn
 * @param engine   the engine in its bay; or none
 * @param quiet    whether it is kept quiet, as a run with nobody listening is
 * @param view     the view it is seen in to begin with
 * @param start    where it starts
 */
public record TestDriving(GlmChassis chassis, Assembled built, OnShow shown, Wheeling wheeling,
                          Optional<EngineFigures> engine, boolean quiet, DriveViews.View view, Place start)
        implements Roamer {

    /** It is none of the place's own pieces. */
    private static final int OWN = -1;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final Lift first = new Lift(OWN, chassis.chassis(), start, 0, 0, 0, true, true, 0, 0, 0, 0);
        return new TestDriven(source, keeping, Optional.of(skies), chassis, new CarDrawn(built, shown, wheeling),
                new EngineHeard(engine, quiet), new DriveViews(chassis, view), first);
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, the car from the keyboard:", "  W or Up: throttle",
                "  S or Down: brake, then reverse", "  A, D or Left, Right: steer", "  Space: handbrake",
                "  Shift held: carefully", "  C: the next view: chase far, chase near, hood, cockpit, bumper",
                "  V: the driver's seat, and back", "  R held: look back",
                "  behind it, Q and E swing the view round, the mouse looks up or down, scroll nearer or further");
    }
}
