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

import java.util.List;
import java.util.Optional;

/**
 * Test drives of the GLM car, for a walk to move: each run the car {@code built} driven from {@code start}, as the
 * avatar engine drives a land vehicle as big as {@code chassis} says, its engine running and its controls taken, at a
 * standstill to begin with.
 *
 * @param chassis how the avatar engine knows the car
 * @param built   the car as it is made, put together
 * @param shown   which of its groups are shown, and in what
 * @param start   where it starts
 */
public record TestDriving(GlmChassis chassis, Assembled built, OnShow shown, Place start) implements Roamer {

    /** It is none of the place's own pieces. */
    private static final int OWN = -1;

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        final Lift first = new Lift(OWN, chassis.chassis(), start, 0, 0, 0, true, true, 0, 0, 0, 0);
        return new TestDriven(source, keeping, Optional.of(skies), chassis, new CarDrawn(built, shown), first);
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window, the car from the keyboard:", "  W or Up: throttle",
                "  S or Down: brake, then reverse", "  A, D or Left, Right: steer", "  Space: handbrake",
                "  Shift held: carefully", "  Q, E: swing the view round; C: behind again",
                "  scroll: nearer or further", "  the mouse: look up or down");
    }
}
