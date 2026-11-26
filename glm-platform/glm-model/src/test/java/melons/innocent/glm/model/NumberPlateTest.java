package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The retro saloon's number plates: a plate's own size, white, in its black frame; the front one on the front
 * bumper's face, ahead of it, the rear one on the tail panel between the tail lamps, behind it; neither touching the
 * lamps nor the bumpers, but for the front frame on the bumper that holds it.
 */
class NumberPlateTest {

    private final Assembled built = new Assembly(new Santana().platform(null).platform()).assembled();
    private final Touching touching = new Touching();

    @Test
    void frontOnTheBumperRearBetweenTheLampsEachInItsFrame() {
        assertEquals(List.of(), built.problems(1e-6));
        for (final String end : List.of("front", "rear")) {
            final PartAt plate = part(end + " number plate");
            assertEquals(Group.PLATES, plate.part().group());
            assertEquals(Group.TRIM, part(end + " number plate frame").part().group());
            final double wide = extent(plate, true, true) - extent(plate, true, false);
            assertEquals(52, wide, 1e-6, "a plate's own size");
        }
        assertTrue(extent(part("front number plate"), false, false) >= extent(part("front bumper"), false, true)
                - 1e-6, "ahead of the front bumper's face");
        assertTrue(extent(part("rear number plate"), false, true) <= extent(part("tail panel"), false, false)
                + 1e-6, "behind the tail panel");
    }

    @Test
    void touchingNeitherTheLampsNorTheBumpers() {
        for (final String end : List.of("front", "rear")) {
            for (final String name : List.of(end + " number plate", end + " number plate frame")) {
                final PartAt plate = part(name);
                for (final PartAt q : built.placed().keySet()) {
                    final String n = q.part().name();
                    final boolean holder = name.endsWith("frame") && n.equals(end + " bumper");
                    if (!holder && (n.endsWith("bumper") || n.endsWith("strip") || n.contains("lamp")
                            || n.endsWith("indicator"))) {
                        assertFalse(touching.touch(plate.shape().form(), built.at(plate), q.shape().form(),
                                built.at(q)), name + " meets " + q.said());
                    }
                }
            }
        }
    }

    private PartAt part(final String name) {
        return built.placed().keySet().stream().filter(p -> p.part().name().equals(name) && p.side() == Side.MIDDLE)
                .findFirst().orElseThrow();
    }

    /** How far {@code part} reaches across if {@code across}, else along; its most if {@code most}, else its least. */
    private double extent(final PartAt part, final boolean across, final boolean most) {
        final Vec3 at = built.at(part);
        final var values = part.shape().form().points().stream().mapToDouble(v -> across ? v.plus(at).x()
                : v.plus(at).z());
        return most ? values.max().orElseThrow() : values.min().orElseThrow();
    }
}
