package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's tail lamps: a housing each side in the tail panel, its reversing lamp white, its tail lamp red and
 * its indicator amber from the number plate out; all of it inside the body, over the bumper and under the boot lid,
 * clear of both.
 */
class TailLampsTest {

    private final SantanaLines lines = new SantanaLines();
    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Touching touching = new Touching();
    private final Map<String, Group> lenses = Map.of("reversing lamp", Group.LAMPS, "tail lamp", Group.TAIL_LAMPS,
            "rear indicator", Group.SIGNALS);

    @Test
    void eachSideAHousingItsLensesInTheirColoursFromThePlateOut() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
            part("tail lamp housing", side);
            lenses.forEach((name, group) -> assertEquals(group, part(name, side).part().group(), name));
        }
        assertTrue(middle("reversing lamp") < middle("tail lamp") && middle("tail lamp") < middle("rear indicator"),
                "from the number plate out");
    }

    @Test
    void insideTheBodyOverTheBumperUnderTheLidClearOfBoth() {
        final List<PartAt> around = List.of(part("rear bumper", Side.MIDDLE), part("boot lid", Side.MIDDLE));
        for (final PartAt p : built.placed().keySet()) {
            if (lenses.containsKey(p.part().name()) || p.part().name().equals("tail lamp housing")) {
                for (final Vec3 v : p.shape().form().points()) {
                    final Vec3 q = v.plus(built.at(p));
                    assertTrue(Math.abs(q.x()) < lines.half(q.z(), q.y()), p.said() + " outside the body at " + q);
                    assertTrue(q.z() > -3_554 * lines.scale() / 10, p.said() + " behind the bumper at " + q);
                }
                for (final PartAt q : around) {
                    assertFalse(touching.touch(p.shape().form(), built.at(p), q.shape().form(), built.at(q)),
                            p.said() + " meets " + q.said());
                }
            }
        }
    }

    /** How far out the right {@code name}'s middle is. */
    private double middle(final String name) {
        final PartAt p = part(name, Side.RIGHT);
        return p.shape().form().points().stream().mapToDouble(v -> v.plus(built.at(p)).x()).average().orElseThrow();
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}
