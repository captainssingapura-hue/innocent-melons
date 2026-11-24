package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CageLatticeTest {

    private static final SoftRegion ALL = new SoftRegion(new V3(-20, -20, -20), new V3(20, 20, 20));

    /** A strap along the way the first number grows, {@code long}, its rings {@code wide} across and {@code deep}. */
    private Cage strap(final double length, final double wide, final double deep, final int rings) {
        final List<V3> points = new ArrayList<>();
        final List<String> names = new ArrayList<>();
        for (int i = 0; i < rings; i++) {
            final double x = length * i / (rings - 1);
            for (int q = 0; q < 8; q++) {
                final double a = 2 * Math.PI * q / 8;
                points.add(new V3(x, Math.cos(a) * wide / 2, Math.sin(a) * deep / 2));
                names.add(i + " " + q);
            }
        }
        return new Cage(points, names, List.of(), List.of());
    }

    /** Points spread through the strap's inside, as a surface lying within it would have them. */
    private Mesher.Mesh inside(final double length, final double wide, final double deep) {
        final List<Float> p = new ArrayList<>();
        for (double x = 0.2; x < length - 0.1; x += 0.37) {
            for (int q = 0; q < 8; q++) {
                final double a = 2 * Math.PI * (q + 0.5) / 8;
                p.add((float) x);
                p.add((float) (Math.cos(a) * wide / 2 * 0.6));
                p.add((float) (Math.sin(a) * deep / 2 * 0.6));
            }
        }
        final float[] f = new float[p.size()];
        for (int i = 0; i < f.length; i++) {
            f[i] = p.get(i);
        }
        return new Mesher.Mesh(f, new int[0]);
    }

    @Test
    void aStrapThinnerThanAnyCubeIsOnePieceOfItsOwnRoom() {
        final Lattice l = CageLattice.INSTANCE.of(ALL, strap(8, 2, 0.2, 3), false, inside(8, 2, 0.2), 0.5);
        assertEquals(1, pieces(l), "one piece");
        final double octagon = 2 * Math.sqrt(2) * (2 / 2.0) * (0.2 / 2.0);
        assertEquals(octagon * 8, l.volume(), 1e-3, "its room an octagon's along it");
    }

    @Test
    void everyPointOfTheSurfaceIsCarriedWhereItIs() {
        final Mesher.Mesh surface = inside(8, 2, 0.3);
        final Lattice l = CageLattice.INSTANCE.of(ALL, strap(8, 2, 0.3, 3), false, surface, 0.5);
        for (int s = 0; s < l.holder().length; s++) {
            final V3 was = new V3(surface.points()[3 * s], surface.points()[3 * s + 1], surface.points()[3 * s + 2]);
            assertEquals(0, l.carried(s).orElseThrow().minus(was).length(), 1e-4, "point " + s);
        }
    }

    @Test
    void neighboursMeetFaceToFace() {
        final Lattice l = CageLattice.INSTANCE.of(ALL, strap(6, 2, 1, 4), false, inside(6, 2, 1), 0.5);
        final Map<String, Integer> faces = new HashMap<>();
        for (int t = 0; t < l.tetCount(); t++) {
            for (final int[] f : faces(l, t)) {
                faces.merge(f[0] + " " + f[1] + " " + f[2], 1, Integer::sum);
            }
        }
        faces.values().forEach(n -> assertTrue(n <= 2, "a face of more than two elements"));
        // closed: each edge of its outside an edge of two of its outside faces, no more, no fewer
        final Map<String, Integer> edges = new HashMap<>();
        faces.forEach((f, n) -> {
            if (n == 1) {
                final String[] c = f.split(" ");
                for (final String e : List.of(c[0] + " " + c[1], c[1] + " " + c[2], c[0] + " " + c[2])) {
                    edges.merge(e, 1, Integer::sum);
                }
            }
        });
        edges.forEach((e, n) -> assertEquals(2, n, "the outside closed at " + e));
    }

    @Test
    void cutByTheRegionItIsFixedBeyondAndThePointsBeyondStay() {
        final SoftRegion half = new SoftRegion(new V3(-20, -20, -20), new V3(4, 20, 20));
        final Mesher.Mesh surface = inside(8, 2, 0.5);
        final Lattice l = CageLattice.INSTANCE.of(half, strap(8, 2, 0.5, 3), false, surface, 0.5);
        assertTrue(l.fixedCount() > 0);
        for (int i = 0; i < l.nodeCount(); i++) {
            assertEquals(l.node(i).x() >= 4 - 1e-6, l.fixed()[i], "node " + i);
            assertTrue(l.node(i).x() <= 4.5 + 1e-6, "nothing kept far beyond: " + l.node(i).x());
        }
        for (int s = 0; s < l.holder().length; s++) {
            assertEquals(surface.points()[3 * s] <= 4, l.carried(s).isPresent(), "point " + s);
        }
    }

    private List<int[]> faces(final Lattice l, final int t) {
        final List<int[]> out = new ArrayList<>();
        for (int skip = 0; skip < 4; skip++) {
            final int[] f = new int[3];
            int k = 0;
            for (int c = 0; c < 4; c++) {
                if (c != skip) {
                    f[k++] = l.tets()[4 * t + c];
                }
            }
            Arrays.sort(f);
            out.add(f);
        }
        return out;
    }

    private int pieces(final Lattice l) {
        final int[] parent = new int[l.tetCount()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = i;
        }
        final Map<String, Integer> seen = new HashMap<>();
        for (int t = 0; t < l.tetCount(); t++) {
            for (final int[] f : faces(l, t)) {
                final Integer o = seen.putIfAbsent(f[0] + " " + f[1] + " " + f[2], t);
                if (o != null) {
                    parent[find(parent, o)] = find(parent, t);
                }
            }
        }
        int n = 0;
        for (int i = 0; i < parent.length; i++) {
            n += find(parent, i) == i ? 1 : 0;
        }
        return n;
    }

    private int find(final int[] p, final int i) {
        int j = i;
        while (p[j] != j) {
            j = p[j];
        }
        return j;
    }
}
