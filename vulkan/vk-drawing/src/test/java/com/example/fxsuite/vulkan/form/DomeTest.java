package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Half of a round body, set down on its cut: every point on or above the ground and on the body's surface, its
 * foot on the ground all round, every triangle facing out, and half as big all over as the whole body.
 */
class DomeTest {

    private final Dome dome = new Dome(36, 26, 28);
    private final Mesh formed = dome.formed(48, 48);

    @Test
    void everyPointIsOnOrAboveTheGroundAndOnTheBodysSurfaceWithItsFootOnTheGround() {
        double lowest = Double.MAX_VALUE;
        double highest = 0;
        for (final Vec3 point : formed.points()) {
            assertTrue(point.y() >= -1e-9, "above the ground: " + point);
            assertEquals(1, Math.pow(point.x() / 18, 2) + Math.pow(point.y() / 13, 2) + Math.pow(point.z() / 14, 2),
                    1e-9, "on the body's surface");
            lowest = Math.min(lowest, point.y());
            highest = Math.max(highest, point.y());
        }
        assertEquals(0, lowest, 1e-9);
        assertEquals(13, highest, 1e-9, "half as high as the whole");
    }

    @Test
    void everyTriangleFacesOutAndItIsHalfAsBigAllOverAsTheWholeBody() {
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            assertTrue(formed.crossed(triangle).dot(formed.corner(triangle, 0).plus(formed.corner(triangle, 1))
                    .plus(formed.corner(triangle, 2))) > 0, "faces away from the middle");
            assertTrue(formed.facing(triangle, 1).dot(formed.corner(triangle, 1)) > 0, "and says so");
        }
        assertEquals(new Ellipsoid(36, 26, 28).formed(96, 48).area() / 2, formed.area(), 2);
    }
}
