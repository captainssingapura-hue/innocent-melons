package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SheetTest {

    /** A tetrahedron far from nought, its triangles all going round the one way or the other. */
    private Sheet tetrahedron(final boolean inward) {
        final float[] p = {100, 50, 30, 101, 50, 30, 100, 51, 30, 100, 50, 31};
        final int[] out = {0, 2, 1, 0, 1, 3, 0, 3, 2, 1, 2, 3};
        final int[] t = out.clone();
        if (inward) {
            for (int i = 0; i < t.length; i += 3) {
                final int b = t[i + 1];
                t[i + 1] = t[i + 2];
                t[i + 2] = b;
            }
        }
        return new Sheet(p, t);
    }

    @Test
    void everyTriangleFacesOutWhicheverWayItWasGiven() {
        for (final boolean inward : new boolean[] {false, true}) {
            final Mesh m = tetrahedron(inward).mesh();
            final Vec3 middle = new Vec3(100.25, 50.25, 30.25);
            for (int t = 0; t < m.triangles(); t++) {
                final Vec3 centre = m.corner(t, 0).plus(m.corner(t, 1)).plus(m.corner(t, 2)).times(1.0 / 3);
                assertTrue(m.crossed(t).dot(centre.minus(middle)) > 0, "triangle " + t + ", inward " + inward);
            }
        }
    }

    @Test
    void eachPointFacesAwayFromTheMiddle() {
        final Mesh m = tetrahedron(true).mesh();
        final Vec3 middle = new Vec3(100.25, 50.25, 30.25);
        for (int i = 0; i < m.points().size(); i++) {
            assertTrue(m.facings().get(i).dot(m.points().get(i).minus(middle)) > 0, "point " + i);
            assertEquals(1, m.facings().get(i).length(), 1e-6);
        }
    }

    @Test
    void aPartTakenKeepsOnlyItsTrianglesAndThePointsTheyUse() {
        final Mesh m = tetrahedron(false).mesh(t -> t == 1);
        assertEquals(1, m.triangles());
        assertEquals(3, m.points().size());
    }
}
