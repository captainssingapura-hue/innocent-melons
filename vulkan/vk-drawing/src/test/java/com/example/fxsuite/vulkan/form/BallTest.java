package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A ball at a place: every point of it as far from its middle as the ball is big; every triangle facing out and
 * saying so; nearly as much surface as a true ball has; and none made of no size.
 */
class BallTest {

    private final Vec3 middle = new Vec3(10, 20, 30);
    private final Mesh formed = formed(new Ball(middle, 5, 24));

    @Test
    void everyPointIsOnItsSurfaceAndEveryTriangleFacesOutAndSaysSo() {
        for (final Vec3 point : formed.points()) {
            assertEquals(5, point.minus(middle).length(), 1e-9);
        }
        assertEquals(2 * 24 + 2 * 24 * 10, formed.triangles(), "a fan at each end and two to a step in each band");
        for (int triangle = 0; triangle < formed.triangles(); triangle++) {
            final Vec3 centre = formed.corner(triangle, 0).plus(formed.corner(triangle, 1))
                    .plus(formed.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(formed.crossed(triangle).dot(centre.minus(middle)) > 0, "faces away from its middle");
            assertTrue(formed.facing(triangle, 1).dot(centre.minus(middle)) > 0, "and says so");
        }
    }

    @Test
    void itHasNearlyAsMuchSurfaceAsATrueBall() {
        final double whole = 4 * Math.PI * 5 * 5;
        assertTrue(formed.area() < whole && formed.area() > 0.97 * whole, formed.area() + " of " + whole);
    }

    @Test
    void noneIsMadeOfNoSizeOrFewerThanFourPointsRound() {
        assertThrows(IllegalArgumentException.class, () -> new Ball(middle, 0, 24));
        assertThrows(IllegalArgumentException.class, () -> new Ball(middle, 5, 3));
    }

    private Mesh formed(final Ball ball) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        ball.formed(forming);
        return forming.formed();
    }
}
