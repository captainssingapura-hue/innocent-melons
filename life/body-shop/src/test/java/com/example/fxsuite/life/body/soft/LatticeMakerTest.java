package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LatticeMakerTest {

    /** A box from {@code lo} to {@code hi}, its faces outward, a point at each corner and in each face's middle. */
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

    private final Mesher.Mesh muscle = box(new V3(1.1, 2.05, -0.95), new V3(3.3, 3.9, 1.2));

    @Test
    void everyPointOfTheSurfaceInTheRegionIsCarriedWhereItIs() {
        final Lattice l = LatticeMaker.INSTANCE.of(new SoftRegion(new V3(0, 0, -2), new V3(5, 5, 3)), muscle, 0.5);
        for (int s = 0; s < l.holder().length; s++) {
            final V3 was = new V3(muscle.points()[3 * s], muscle.points()[3 * s + 1], muscle.points()[3 * s + 2]);
            final V3 now = l.carried(s).orElseThrow();
            assertEquals(0, now.minus(was).length(), 1e-4, "point " + s);
            float sum = 0;
            for (int k = 0; k < 4; k++) {
                assertTrue(l.weights()[4 * s + k] >= -1e-6, "point " + s + " weight " + k);
                sum += l.weights()[4 * s + k];
            }
            assertEquals(1, sum, 1e-5);
        }
    }

    @Test
    void itFillsTheMuscleAndLittleMore() {
        final Lattice l = LatticeMaker.INSTANCE.of(new SoftRegion(new V3(0, 0, -2), new V3(5, 5, 3)), muscle, 0.25);
        final double room = 2.2 * 1.85 * 2.15;
        assertTrue(l.volume() >= room, "the lattice holds the muscle: " + l.volume() + " for " + room);
        assertTrue(l.volume() < room * 1.6, "and not much more: " + l.volume() + " for " + room);
        assertEquals(0, l.fixedCount(), "nothing cut");
    }

    @Test
    void neighboursMeetFaceToFace() {
        final Lattice l = LatticeMaker.INSTANCE.of(new SoftRegion(new V3(0, 0, -2), new V3(5, 5, 3)), muscle, 0.5);
        final Map<String, Integer> faces = new HashMap<>();
        for (int t = 0; t < l.tetCount(); t++) {
            for (int skip = 0; skip < 4; skip++) {
                final int[] f = new int[3];
                int k = 0;
                for (int c = 0; c < 4; c++) {
                    if (c != skip) {
                        f[k++] = l.tets()[4 * t + c];
                    }
                }
                java.util.Arrays.sort(f);
                faces.merge(f[0] + " " + f[1] + " " + f[2], 1, Integer::sum);
            }
        }
        for (final Map.Entry<String, Integer> e : faces.entrySet()) {
            assertTrue(e.getValue() <= 2, "a face of more than two elements: " + e.getKey());
        }
        for (int t = 0; t < l.tetCount(); t++) {
            final V3 a = l.node(l.tets()[4 * t]);
            final double six = l.node(l.tets()[4 * t + 1]).minus(a)
                    .cross(l.node(l.tets()[4 * t + 2]).minus(a)).dot(l.node(l.tets()[4 * t + 3]).minus(a));
            assertEquals(0.125 / 6, Math.abs(six) / 6, 1e-6, "every element a sixth of its cube");
        }
    }

    @Test
    void cutByTheRegionItIsFixedBeyondAndThePointsBeyondStay() {
        final SoftRegion half = new SoftRegion(new V3(0, 0, -2), new V3(5, 3.0, 3));
        final Lattice l = LatticeMaker.INSTANCE.of(half, muscle, 0.5);
        assertTrue(l.fixedCount() > 0, "fixed where it is cut");
        for (int i = 0; i < l.nodeCount(); i++) {
            final V3 v = l.node(i);
            final boolean inside = v.y() < 3.0 - 1e-6 && v.x() > 0 && v.z() > -2 && v.x() < 5 && v.z() < 3;
            assertEquals(!inside, l.fixed()[i], "node " + i);
        }
        for (int s = 0; s < l.holder().length; s++) {
            final boolean in = half.has(new V3(muscle.points()[3 * s], muscle.points()[3 * s + 1],
                    muscle.points()[3 * s + 2]));
            assertEquals(in, l.carried(s).isPresent(), "point " + s);
        }
        assertFalse(l.tetCount() == 0);
    }
}
