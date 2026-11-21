package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The surface finder, true to a shape it is given: a ball's surface on the ball, closed, every triangle facing out. */
class MesherTest {

    @Test
    void theSurfaceIsWhereTheShapeIs() {
        final Field ball = new Field(List.of(new Field.Element("ball",
                Field.Ellipsoid.of(V3.ZERO, new V3(5, 0, 0), new V3(0, 5, 0), new V3(0, 0, 5)), Field.Op.ADD, 0)));
        final Mesher.Mesh m = Mesher.of(ball, 0.25);
        final float[] p = m.points();
        for (int i = 0; i < p.length; i += 3) {
            final double r = Math.sqrt(p[i] * p[i] + p[i + 1] * p[i + 1] + p[i + 2] * p[i + 2]);
            assertEquals(5, r, 0.02, "on the sphere");
        }
        final Map<Long, Integer> edges = new HashMap<>();
        final int[] t = m.triangles();
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = at(p, t[i]);
            final V3 b = at(p, t[i + 1]);
            final V3 c = at(p, t[i + 2]);
            assertTrue(b.minus(a).cross(c.minus(a)).dot(a.plus(b).plus(c)) > 0, "facing out");
            for (int k = 0; k < 3; k++) {
                final long x = t[i + k];
                final long y = t[i + (k + 1) % 3];
                edges.merge(Math.min(x, y) << 32 | Math.max(x, y), 1, Integer::sum);
            }
        }
        assertTrue(edges.values().stream().allMatch(n -> n == 2), "closed");
    }

    private static V3 at(final float[] p, final int i) {
        return new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2]);
    }
}
