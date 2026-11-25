package melons.innocent.testkit.loose;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Spot;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Stepping;
import melons.innocent.testkit.TestKit;

import java.io.IOException;
import java.util.List;

/**
 * The trial of the core engine's world at rest and its loose body, walked round on a test ground: a crate that
 * falls, a stack that comes down when its pillar goes, a beam that tips when one of its two pillars goes, a ramp
 * things roll down, and a wagon that carries what is on it. The supports are taken away from the walk's window, or
 * of themselves {@code -Dloose.after=} seconds in. What lies about is handled as its kind lets it be: taken up and
 * put down with T, thrown with X, and cut, crushed or eaten from the window's list.
 */
public final class LooseWalk {

    private LooseWalk() {
    }

    public static void main(final String[] args) throws IOException {
        final Trial trial = new Trial();
        final Handling handling = new Handling(trial);
        new TestKit("Loose bodies: the world at rest, what falls, and what is handled", new BaseMap())
                .with((card, laid) -> new LoosePlace(card, laid, trial, handling))
                .walked(new Stepping(new Afoot(), (step, going) -> {
                    final Vec3 head = going.head(1);
                    handling.stepped(step, new Spot(head.x(), head.y(), head.z()), going.yaw(1));
                    trial.ticked();
                }, List.of("  T: take up what is at hand, or put down what is held",
                        "  X: throw what is held, the way you face"), handling::hints));
    }
}
