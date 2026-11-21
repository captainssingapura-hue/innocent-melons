package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * How near a point is to what is beneath the fat ({@link Beneath}), the muscles and the bones: its nearest triangle
 * among those asked for, how far, and the point on it, as far between its corners as it lies; found by looking in
 * cubes round the point, nearest first, each cube knowing the triangles in it, until none nearer can be.
 */
public final class Nearness {

    /** How big each cube is, cm. */
    private static final double CELL = 2;

    private final Beneath beneath;
    private final Map<Long, int[]> cells = new HashMap<>();

    /** The nearest point of a triangle: how far, which triangle, how much of each corner it is. */
    public record Near(double distance, int triangle, double a, double b, double c) {
    }

    public Nearness(final Beneath beneath) {
        this.beneath = beneath;
        final Map<Long, List<Integer>> by = new HashMap<>();
        final int[] t = beneath.triangles();
        for (int i = 0; i < t.length / 3; i++) {
            final int[] lo = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
            final int[] hi = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
            for (int k = 0; k < 3; k++) {
                final int p = t[3 * i + k];
                for (int d = 0; d < 3; d++) {
                    final int c = (int) Math.floor(beneath.points()[3 * p + d] / CELL);
                    lo[d] = Math.min(lo[d], c);
                    hi[d] = Math.max(hi[d], c);
                }
            }
            for (int x = lo[0]; x <= hi[0]; x++) {
                for (int y = lo[1]; y <= hi[1]; y++) {
                    for (int z = lo[2]; z <= hi[2]; z++) {
                        by.computeIfAbsent(key(x, y, z), k -> new ArrayList<>()).add(i);
                    }
                }
            }
        }
        by.forEach((k, v) -> cells.put(k, v.stream().mapToInt(Integer::intValue).toArray()));
    }

    /** What is beneath. */
    public Beneath beneath() {
        return beneath;
    }

    /**
     * The nearest point to {@code p} of the triangles {@code counts} says, no further than {@code within}; at
     * {@code within}, none, if none is nearer.
     */
    public Near nearest(final V3 p, final double within, final IntPredicate counts) {
        final int cx = (int) Math.floor(p.x() / CELL);
        final int cy = (int) Math.floor(p.y() / CELL);
        final int cz = (int) Math.floor(p.z() / CELL);
        Near best = new Near(within, -1, 0, 0, 0);
        final int shells = (int) Math.ceil(within / CELL) + 1;
        for (int s = 0; s <= shells && (s - 1) * CELL < best.distance(); s++) {
            for (int x = -s; x <= s; x++) {
                for (int y = -s; y <= s; y++) {
                    for (int z = -s; z <= s; z++) {
                        if (Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z))) != s) {
                            continue;
                        }
                        best = nearestIn(cells.get(key(cx + x, cy + y, cz + z)), p, counts, best);
                    }
                }
            }
        }
        return best;
    }

    private Near nearestIn(final int[] in, final V3 p, final IntPredicate counts, final Near best) {
        Near b = best;
        if (in == null) {
            return b;
        }
        for (final int tri : in) {
            if (counts.test(tri)) {
                final Near n = Closest.INSTANCE.on(beneath, tri, p);
                b = n.distance() < b.distance() ? n : b;
            }
        }
        return b;
    }

    /**
     * Whether {@code p}, nearest {@code n}, lies behind the triangle it is nearest, the way it faces out: inside what
     * is beneath, if that is one closed surface facing out.
     */
    public boolean inside(final Near n, final V3 p) {
        final int[] t = beneath.triangles();
        final V3 a = beneath.point(t[3 * n.triangle()]);
        final V3 b = beneath.point(t[3 * n.triangle() + 1]);
        final V3 c = beneath.point(t[3 * n.triangle() + 2]);
        final V3 q = a.times(n.a()).plus(b.times(n.b())).plus(c.times(n.c()));
        return p.minus(q).dot(b.minus(a).cross(c.minus(a))) < 0;
    }

    /** How the point {@code n} is on follows the body: its corners', as much as it is each. */
    public Weights weights(final Near n) {
        final int[] t = beneath.triangles();
        final Weights[] w = beneath.weights();
        final Weights ab = w[t[3 * n.triangle()]].toward(w[t[3 * n.triangle() + 1]], n.b() / Math.max(1e-12,
                n.a() + n.b()));
        return ab.toward(w[t[3 * n.triangle() + 2]], n.c());
    }

    private long key(final int x, final int y, final int z) {
        return ((long) (x & 0x1FFFFF) << 42) | ((long) (y & 0x1FFFFF) << 21) | (z & 0x1FFFFF);
    }
}
