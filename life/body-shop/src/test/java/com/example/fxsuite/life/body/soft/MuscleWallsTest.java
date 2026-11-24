package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MuscleWallsTest {

    /** A box from {@code lo} to {@code hi}. */
    private Mesher.Mesh box(final V3 lo, final V3 hi) {
        final float[] p = new float[8 * 3];
        for (int b = 0; b < 8; b++) {
            p[3 * b] = (float) ((b & 1) == 0 ? lo.x() : hi.x());
            p[3 * b + 1] = (float) ((b >> 1 & 1) == 0 ? lo.y() : hi.y());
            p[3 * b + 2] = (float) ((b >> 2 & 1) == 0 ? lo.z() : hi.z());
        }
        final int[] t = {0, 2, 1, 1, 2, 3, 4, 5, 6, 5, 7, 6, 0, 1, 4, 1, 5, 4, 2, 6, 3, 3, 6, 7, 0, 4, 2, 2, 4, 6,
            1, 3, 5, 3, 7, 5};
        return new Mesher.Mesh(p, t);
    }

    @Test
    void aMuscleMadeAWallIsAsFarOffAsItIsAndDeepInItAsDeep() {
        final MuscleWalls walls = new MuscleWalls(new SoftRegion(new V3(-5, -5, -5), new V3(5, 5, 5)), 0.1);
        walls.add(box(new V3(-1, -1, -1), new V3(1, 1, 1)));
        assertEquals(1.0, walls.distance(2, 0, 0), 0.1, "a centimetre off one side");
        assertEquals(-0.6, walls.distance(0.4, 0, 0), 0.1, "six millimetres in from it");
        assertTrue(walls.out(0.4, 0, 0).x() > 0.9, "the way out the nearest side");
        assertTrue(walls.out(0, -0.7, 0).y() < -0.9, "and from below, down");
        walls.add(box(new V3(1, -1, -1), new V3(3, 1, 1)));
        assertEquals(-0.6, walls.distance(2.4, 0, 0), 0.1, "another added, in it as deep");
        assertTrue(walls.distance(1, 0, 0) < -0.5, "where the two meet, inside both");
    }
}
