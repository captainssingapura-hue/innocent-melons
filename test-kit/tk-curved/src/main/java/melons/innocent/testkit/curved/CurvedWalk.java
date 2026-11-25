package melons.innocent.testkit.curved;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.motion.Spot;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Stepping;
import melons.innocent.testkit.TestKit;

import java.io.IOException;

/**
 * The trial of curved surfaces, walked on a test ground: a spiral ramp to walk up, a vault to walk under and over,
 * and a dome to walk over. The walk's window says what is underfoot, how the surface differs between the two feet
 * and how it differs a metre ahead; the avatar stands with each foot on its own ground.
 */
public final class CurvedWalk {

    /** How far over its feet the engine has the avatar's eyes, which is where a walk says its head is. */
    private static final double EYES = 160;

    private CurvedWalk() {
    }

    public static void main(final String[] args) throws IOException {
        final Curves curves = new Curves();
        final Treading treading = new Treading(curves);
        new TestKit("Curved surfaces: a spiral ramp, a vault and a dome", new BaseMap())
                .with((card, laid) -> new CurvedPlace(card, laid, curves, treading))
                .walked(new Stepping(new Afoot(), (step, going) -> {
                    final Vec3 head = going.head(1);
                    treading.stepped(new Spot(head.x(), head.y() - EYES, head.z()), going.yaw(1));
                }));
    }
}
