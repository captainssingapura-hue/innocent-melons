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
 * put down with T; whatever else can be done with it chosen from a menu G opens; and thrown by holding the mouse's
 * button to gather power and letting go.
 */
public final class LooseWalk {

    /** How far over its feet the engine has the avatar's eyes, which is where a walk says its head is. */
    private static final double EYES = 160;

    private LooseWalk() {
    }

    public static void main(final String[] args) throws IOException {
        final Trial trial = new Trial();
        final Handling handling = new Handling(trial);
        // the walk's figure goes by the core unless the run asks for the old engine's avatar
        final boolean old = "old".equals(System.getProperty("loose.avatar", "core"));
        final boolean second = old && Boolean.getBoolean("loose.second");
        new TestKit("Loose bodies: the world at rest, what falls, and what is handled", new BaseMap())
                .with((card, laid) -> new LoosePlace(card, laid, trial, handling))
                .walked(new Stepping(old ? new Afoot() : new ByTheCore(trial), (step, going) -> {
                    final Vec3 head = going.head(1);
                    final Spot feet = new Spot(head.x(), head.y() - EYES, head.z());
                    handling.stepped(step, new Spot(head.x(), head.y(), head.z()), going.yaw(1), going.tilt(1));
                    // by the core, the walk's own step has ticked the trial already, its walker in it
                    if (old) {
                        trial.stood(feet, going.yaw(1));
                        if (second) {
                            trial.second().asked(step.held(), step.pressed(), feet, going.yaw(1));
                        }
                        trial.ticked();
                    }
                }, List.of("  T: take up what is at hand, or put down what is held",
                        "  G: more to do with it; up and down choose, T does it",
                        "  readied to throw: hold the mouse's button for power,",
                        "    let go to throw, the way you face and look"), handling::hints));
    }
}
