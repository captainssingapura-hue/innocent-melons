package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's front wings: one each side, bolted to the shell; from the front door's shut line to the nose,
 * standing just proud of the body's face and no further in than their edges are turned; drawn in round the nose; and
 * clear of the front tyres, their arches over them.
 */
class FrontWingTest {

    private final SantanaLines lines = new SantanaLines();
    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Touching touching = new Touching();

    @Test
    void oneEachSideBoltedToTheShell() {
        assertEquals(List.of(), built.problems(1e-6));
        assertEquals(Group.BODYWORK, wing(Side.RIGHT).part().group());
        assertTrue(new Santana().platform(null).platform().connections().stream()
                .anyMatch(c -> c.carried().name().equals("front wing") && c.joining() == Joining.BOLTED));
        assertTrue(extent(wing(Side.LEFT), Vec3::x, false) < -80, "the left's on the left");
    }

    @Test
    void fromTheDoorToTheNoseJustProudOfTheBodysFace() {
        final PartAt right = wing(Side.RIGHT);
        final double k = lines.scale();
        assertEquals(-447 * k / 10, extent(right, Vec3::z, false), 0.01, "from the front door's shut line");
        assertEquals(802 * k / 10, extent(right, Vec3::z, true), 0.01, "to the nose");
        final Vec3 at = built.at(right);
        for (final Vec3 p : right.shape().form().points()) {
            final Vec3 q = p.plus(at);
            final double face = lines.half(q.z(), q.y());
            assertTrue(q.x() <= face + 0.2 + 1e-9, "proud of the body's face by no more than a sheet: " + q);
            assertTrue(q.x() >= nearest(q) - 2.7, "no further in than its edges are turned: " + q);
        }
        assertTrue(lines.half(85, 70) < lines.half(0, 70) - 2.5, "drawn in round the nose");
    }

    @Test
    void clearOfTheFrontTyres() {
        for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
            final PartAt wing = wing(side);
            final PartAt tyre = built.placed().keySet().stream()
                    .filter(p -> p.part().name().equals("front tyre") && p.side() == side).findFirst().orElseThrow();
            assertFalse(touching.touch(wing.shape().form(), built.at(wing), tyre.shape().form(), built.at(tyre)),
                    side + " wing meets its tyre");
        }
    }

    /** The face's least half-width within an edge's depth above or below {@code q}, where an edge there began. */
    private double nearest(final Vec3 q) {
        double least = Double.MAX_VALUE;
        for (double d = -2.7; d <= 2.7; d += 0.1) {
            least = Math.min(least, lines.half(q.z(), q.y() + d));
        }
        return least;
    }

    private PartAt wing(final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals("front wing") && p.side() == side)
                .findFirst().orElseThrow();
    }

    private double extent(final PartAt part, final ToDoubleFunction<Vec3> axis, final boolean most) {
        final Vec3 at = built.at(part);
        final var values = part.shape().form().points().stream().mapToDouble(v -> axis.applyAsDouble(v.plus(at)));
        return most ? values.max().orElseThrow() : values.min().orElseThrow();
    }
}
