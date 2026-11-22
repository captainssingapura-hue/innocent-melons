package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A straight round bar: every point of it as far from its middle line as it is thick, or on one of its ends; every
 * triangle facing out, whichever way the bar lies; as much surface as a bar of so many flat sides has; and none
 * made of no length or no thickness.
 */
class TubeTest {

    private static final int ROUND = 12;

    private final Vec3 from = new Vec3(10, 20, 30);

    @Test
    void everyPointIsItsThicknessFromItsMiddleLineOrOnAnEndWhicheverWayItLies() {
        for (final Vec3 to : new Vec3[] {new Vec3(10, 80, 30), new Vec3(70, 20, 30), new Vec3(40, 55, -20)}) {
            final Mesh formed = formed(new Tube(from, to, 3, ROUND));
            final Vec3 along = to.minus(from).unit();
            final double length = to.minus(from).length();
            for (final Vec3 point : formed.points()) {
                final double far = point.minus(from).dot(along);
                final double out = point.minus(from.plus(along.times(far))).length();
                assertTrue(far >= -1e-9 && far <= length + 1e-9, "between its ends: " + far);
                assertTrue(Math.abs(out - 3) < 1e-9 || out < 1e-9 && (far < 1e-9 || far > length - 1e-9),
                        "on its surface, or the middle of an end: " + out);
            }
        }
    }

    @Test
    void everyTriangleFacesOutWhicheverWayItLies() {
        for (final Vec3 to : new Vec3[] {new Vec3(10, 80, 30), new Vec3(70, 20, 30), new Vec3(40, 55, -20)}) {
            final Mesh formed = formed(new Tube(from, to, 3, ROUND));
            final Vec3 middle = from.plus(to).times(0.5);
            assertEquals(4 * ROUND, formed.triangles(), "two to a side and one to a side at each end");
            for (int triangle = 0; triangle < formed.triangles(); triangle++) {
                final Vec3 centre = formed.corner(triangle, 0).plus(formed.corner(triangle, 1))
                        .plus(formed.corner(triangle, 2)).times(1.0 / 3);
                assertTrue(formed.crossed(triangle).dot(centre.minus(middle)) > 0, "faces away from its middle");
                assertTrue(formed.facing(triangle, 1).dot(centre.minus(middle)) > 0, "and says so");
            }
        }
    }

    @Test
    void itHasAsMuchSurfaceAsABarOfSoManyFlatSides() {
        final double side = 2 * 3 * Math.sin(Math.PI / ROUND);
        final double end = ROUND * 0.5 * 3 * 3 * Math.sin(2 * Math.PI / ROUND);
        assertEquals(ROUND * side * 60 + 2 * end, formed(new Tube(from, new Vec3(10, 80, 30), 3, ROUND)).area(), 1e-9);
    }

    @Test
    void noneIsMadeOfNoLengthOrNoThicknessOrFewerThanThreeSides() {
        assertThrows(IllegalArgumentException.class, () -> new Tube(from, from, 3, ROUND));
        assertThrows(IllegalArgumentException.class, () -> new Tube(from, new Vec3(10, 80, 30), 0, ROUND));
        assertThrows(IllegalArgumentException.class, () -> new Tube(from, new Vec3(10, 80, 30), 3, 2));
    }

    private Mesh formed(final Tube tube) {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        tube.formed(forming);
        return forming.formed();
    }
}
