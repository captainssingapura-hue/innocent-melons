package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's inner arch liners: one in each arch, black plastic; touching nothing of the chassis, its tyres,
 * wheels, brakes, carriers, links, springs, dampers or steering; and the front tyres inside their liners' reach from
 * the wheel's middle all round, so clear of them however far they are steered.
 */
class ArchLinersTest {

    private final Santana car = new Santana();
    private final Assembled built = new Assembly(car.platform(null).platform()).assembled();
    private final Touching touching = new Touching();
    private final Set<Group> chassis = Set.of(Group.SUBFRAMES, Group.LINKS, Group.CARRIERS, Group.SPRINGS,
            Group.DAMPERS, Group.BRAKES, Group.CALIPERS, Group.STEERING, Group.WHEELS, Group.TYRES);

    @Test
    void oneInEachArchTouchingNothingOfTheChassis() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final String name : List.of("front arch liner", "rear arch liner")) {
            for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
                final PartAt liner = part(name, side);
                assertEquals(Material.PLASTIC, liner.part().made().material());
                for (final PartAt q : built.placed().keySet()) {
                    if (chassis.contains(q.part().group())) {
                        assertFalse(touching.touch(liner.shape().form(), built.at(liner), q.shape().form(),
                                built.at(q)), liner.said() + " meets " + q.said());
                    }
                }
            }
        }
    }

    @Test
    void theFrontTyresInsideTheirLinersReachHoweverSteered() {
        final Proportions p = car.proportions();
        final PartAt tyre = part("front tyre", Side.RIGHT);
        final Vec3 middle = new Vec3(p.halfTrack(), p.tyre(), 0);
        final double reach = tyre.shape().form().points().stream()
                .mapToDouble(v -> v.plus(built.at(tyre)).minus(middle).length()).max().orElseThrow();
        assertTrue(reach < p.tyre() + 2.7, "the tyre reaches " + reach + " from its middle");
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}
