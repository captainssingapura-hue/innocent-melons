package melons.innocent.glm.model;

import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's wheel arches outside: a chrome trim round each arch and a rubber mud flap behind each wheel,
 * each side; the flaps behind their wheels; none touching a tyre, a door or a bumper.
 */
class WheelArchesTest {

    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Touching touching = new Touching();
    private final List<String> worn = List.of("front arch trim", "rear arch trim", "front mud flap", "rear mud flap");

    @Test
    void chromeRoundEachArchRubberBehindEachWheel() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
            for (final String name : worn) {
                final PartAt p = part(name, side);
                assertEquals(name.endsWith("trim") ? Material.STEEL : Material.RUBBER, p.part().made().material());
                assertTrue(p.part().made().bare(), name + " bare");
            }
        }
        assertTrue(along("front mud flap", true) < along("front tyre", false), "the front flap behind its wheel");
        assertTrue(along("rear mud flap", true) < along("tyre", false), "the rear flap behind its wheel");
    }

    @Test
    void touchingNoTyreDoorOrBumper() {
        for (final String name : worn) {
            final PartAt p = part(name, Side.RIGHT);
            for (final PartAt q : built.placed().keySet()) {
                final String n = q.part().name();
                if (n.endsWith("tyre") || n.endsWith("door") || n.endsWith("bumper")) {
                    assertFalse(touching.touch(p.shape().form(), built.at(p), q.shape().form(), built.at(q)),
                            name + " meets " + q.said());
                }
            }
        }
    }

    private PartAt part(final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }

    /** How far ahead the right {@code name} reaches, or how far behind if not {@code ahead}. */
    private double along(final String name, final boolean ahead) {
        final PartAt p = part(name, Side.RIGHT);
        final var z = p.shape().form().points().stream().mapToDouble(v -> v.plus(built.at(p)).z());
        return ahead ? z.max().orElseThrow() : z.min().orElseThrow();
    }
}
