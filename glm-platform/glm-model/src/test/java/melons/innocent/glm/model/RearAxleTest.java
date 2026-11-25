package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The rear axle put together: every part reached from the underbody, every link met at the carrier where it was made
 * to, the springs and dampers at their tops; a part on each side, the left the right's mirror; its wheel where the
 * car's figures put it and its tyre on the ground; the underbody and the subframe clear of the tyres; the same for a
 * saloon's figures; and figures out of the platform's range refused.
 */
class RearAxleTest {

    private final Proportions suv = new Proportions();

    @Test
    void everyPartReachedEveryLinkMetWhereItWasMadeTo() {
        final Platform platform = new RearAxle().platform();
        assertEquals(List.of(), platform.problems());
        final Assembled built = new Assembly(platform).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        assertEquals(2 + 2 * 13, built.placed().size(), "the underbody and subframe once, thirteen parts each side");
        assertEquals(3 + 2 * (4 + 2), built.gaps().size(), "met again: three mounts, and each side four links and"
                + " the spring's and the damper's tops");
    }

    @Test
    void theLeftTheRightsMirror() {
        final Assembled built = new Assembly(new RearAxle().platform()).assembled();
        for (final Map.Entry<PartAt, Vec3> e : built.placed().entrySet()) {
            if (e.getKey().side() == Side.RIGHT) {
                final Vec3 right = e.getKey().point(e.getKey().part().points().getFirst().name()).at()
                        .plus(e.getValue());
                final PartAt other = new PartAt(e.getKey().part(), Side.LEFT);
                final Vec3 left = other.point(other.part().points().getFirst().name()).at().plus(built.at(other));
                assertEquals(right.x(), -left.x(), 1e-9, e.getKey().said());
                assertEquals(right.y(), left.y(), 1e-9);
                assertEquals(right.z(), left.z(), 1e-9);
            }
        }
    }

    @Test
    void itsWheelWhereTheFiguresPutItItsTyreOnTheGround() {
        final Assembled built = new Assembly(new RearAxle().platform()).assembled();
        final PartAt tyre = part(built, "tyre", Side.RIGHT);
        final Vec3 at = built.at(tyre);
        assertEquals(suv.halfTrack(), at.x(), 1e-9);
        assertEquals(suv.tyre(), at.y(), 1e-9);
        assertEquals(-suv.wheelbase(), at.z(), 1e-9);
        final double lowest = tyre.shape().form().points().stream().mapToDouble(p -> p.y() + at.y()).min()
                .orElseThrow();
        assertEquals(0, lowest, 0.05, "on the ground");
    }

    @Test
    void theUnderbodyAndSubframeClearOfTheTyres() {
        final Assembled built = new Assembly(new RearAxle().platform()).assembled();
        final double inner = suv.halfTrack() - suv.width() * 0.1 / 2;
        for (final PartAt p : built.placed().keySet()) {
            if (p.part().group() == Group.UNDERBODY || p.part().group() == Group.SUBFRAMES) {
                final double out = p.shape().form().points().stream().mapToDouble(v -> v.x() + built.at(p).x())
                        .max().orElseThrow();
                assertTrue(out < inner, p.said() + " reaches " + out + ", the tyre from " + inner);
            }
        }
    }

    @Test
    void aSaloonsFiguresTooAndFiguresOutOfRangeRefused() {
        final Proportions saloon = new Proportions(292.4, 162.7, 245, 45, 19);
        final Assembled built = new Assembly(new RearAxle(saloon).platform()).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        assertEquals(-saloon.wheelbase(), built.at(part(built, "wheel", Side.LEFT)).z(), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> new Proportions(350, 167.9, 285, 45, 20));
        assertThrows(IllegalArgumentException.class, () -> new Proportions(299.4, 167.9, 285, 45, 26));
    }

    private PartAt part(final Assembled built, final String name, final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == side)
                .findFirst().orElseThrow();
    }
}
