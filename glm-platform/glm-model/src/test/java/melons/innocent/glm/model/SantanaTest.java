package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon on the platform, its two body sides welded on: one each side, pressed steel and painted, as long,
 * as high and as wide as the original made larger; their outer faces where the drawing's lines put them, their door
 * openings' flanges turned in behind; the rear wheel inside its arch and inside the side.
 */
class SantanaTest {

    private final Santana car = new Santana();
    private final SantanaLines lines = new SantanaLines();
    private final Assembled built = new Assembly(car.platform(null).platform()).assembled();

    @Test
    void bothSidesWeldedOnPressedSteelPainted() {
        assertEquals(List.of(), built.problems(1e-6));
        final PartAt right = side(Side.RIGHT);
        final PartAt left = side(Side.LEFT);
        assertEquals(Material.STEEL, right.part().made().material());
        assertFalse(right.part().made().bare(), "painted");
        assertEquals(Group.BODYWORK, right.part().group());
        assertTrue(extent(right, Vec3::x, true) > 85 && extent(left, Vec3::x, false) < -85, "one each side");
    }

    @Test
    void asLongHighAndWideAsTheOriginalMadeLarger() {
        final PartAt right = side(Side.RIGHT);
        final double k = lines.scale();
        assertEquals(-395 * k / 10, extent(right, Vec3::z, true), 0.01, "from the windscreen's pillar");
        assertEquals(-3_470 * k / 10, extent(right, Vec3::z, false), 0.01, "to the tail");
        assertEquals(1_352 * k / 10, extent(right, Vec3::y, true), 0.01, "its roof's edge");
        assertEquals(292 * k / 10, extent(right, Vec3::y, false), 0.01, "its sill's foot");
        assertEquals(848 * k / 10, extent(right, Vec3::x, true), 0.05, "as wide, half the car, as the original");
    }

    @Test
    void itsFacesOnTheLinesItsFlangesTurnedInBehind() {
        final PartAt right = side(Side.RIGHT);
        final Mesh form = right.shape().form();
        final Vec3 at = built.at(right);
        for (final Vec3 p : form.points()) {
            final Vec3 q = p.plus(at);
            assertTrue(q.x() <= lines.half(q.z(), q.y()) + 0.1, "proud of its face by no more than the sheet: "
                    + q);
            assertTrue(q.x() >= nearest(q) - 3.2, "no further in than a flange from its face: " + q);
        }
        for (int t = 0; t < form.triangles(); t++) {
            assertTrue(Double.isFinite(form.crossed(t).length()), "every triangle whole");
        }
    }

    @Test
    void theRearWheelInsideItsArchAndInsideTheSide() {
        final Proportions p = car.proportions();
        final double top = 2 * p.tyre();
        final double archTop = (309 + 375) * lines.scale() / 10;
        assertTrue(top < archTop - 5, "the tyre's top " + top + " under the arch's " + archTop);
        final double outside = p.halfTrack() + p.width() * 0.1 / 2;
        assertTrue(outside < lines.half(p.back(), p.tyre()), "the tyre's outside " + outside + " inside the side");
    }

    /** The face's least half-width within a flange's depth above or below {@code q}, where a flange there began. */
    private double nearest(final Vec3 q) {
        double least = Double.MAX_VALUE;
        for (double d = -3.2; d <= 3.2; d += 0.2) {
            least = Math.min(least, lines.half(q.z(), q.y() + d));
        }
        return least;
    }

    private PartAt side(final Side side) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals("body side") && p.side() == side)
                .findFirst().orElseThrow();
    }

    private double extent(final PartAt part, final ToDoubleFunction<Vec3> axis, final boolean most) {
        final Vec3 at = built.at(part);
        final var values = part.shape().form().points().stream().mapToDouble(v -> axis.applyAsDouble(v.plus(at)));
        return most ? values.max().orElseThrow() : values.min().orElseThrow();
    }
}
