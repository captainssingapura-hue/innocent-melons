package com.example.fxsuite.life.body.soft;

import java.util.Arrays;

/**
 * The triangles of a region's muscles' surfaces sorted into a grid over them, so that those near a point are found
 * without going over all: each triangle in every cell its box touches, the grid made afresh each time over where the
 * points are now. And the nearest of another muscle's triangles to a point, within a cell's reach: where on it, and the
 * way it faces out of its muscle.
 */
final class TriangleGrid {

    private final double cell;
    private final double[] lo = new double[3];
    private final int[] n = new int[3];
    private int[] start = new int[1];
    private int[] in = new int[0];
    /** For each muscle, while a point is looked about: how near its nearest triangle, which, and which were seen. */
    private double[] best = new double[0];
    private int[] which = new int[0];
    private int[] touched = new int[0];

    /** A grid of cells {@code cell} a side: as far as a point is looked about. */
    TriangleGrid(final double cell) {
        this.cell = cell;
    }

    /** The triangles of {@code s}, its points at {@code at}, sorted into a grid over them afresh. */
    void sorted(final double[] at, final SurfaceContact.Surfaces s) {
        final double[] hi = new double[3];
        Arrays.fill(lo, Double.MAX_VALUE);
        Arrays.fill(hi, -Double.MAX_VALUE);
        for (int p = 0; p < s.count(); p++) {
            for (int c = 0; c < 3; c++) {
                lo[c] = Math.min(lo[c], at[3 * p + c]);
                hi[c] = Math.max(hi[c], at[3 * p + c]);
            }
        }
        for (int c = 0; c < 3; c++) {
            n[c] = Math.max(1, (int) Math.ceil((hi[c] - lo[c]) / cell) + 1);
        }
        final int cells = n[0] * n[1] * n[2];
        if (start.length != cells + 1) {
            start = new int[cells + 1];
        } else {
            Arrays.fill(start, 0);
        }
        final int[] box = new int[6];
        for (int t = 0; t < s.triangles(); t++) {
            box(at, s, t, box);
            for (int k = box[2]; k <= box[5]; k++) {
                for (int j = box[1]; j <= box[4]; j++) {
                    for (int i = box[0]; i <= box[3]; i++) {
                        start[(k * n[1] + j) * n[0] + i + 1]++;
                    }
                }
            }
        }
        for (int c = 0; c < cells; c++) {
            start[c + 1] += start[c];
        }
        if (in.length < start[cells]) {
            in = new int[start[cells]];
        }
        final int[] next = Arrays.copyOf(start, cells);
        for (int t = 0; t < s.triangles(); t++) {
            box(at, s, t, box);
            for (int k = box[2]; k <= box[5]; k++) {
                for (int j = box[1]; j <= box[4]; j++) {
                    for (int i = box[0]; i <= box[3]; i++) {
                        in[next[(k * n[1] + j) * n[0] + i]++] = t;
                    }
                }
            }
        }
    }

    /**
     * Into {@code box}, the cells triangle {@code t}'s box touches, widened by half a cell, so that a point in a cell
     * finds there every triangle within half a cell of it: lowest across, up and ahead, then highest.
     */
    private void box(final double[] at, final SurfaceContact.Surfaces s, final int t, final int[] box) {
        for (int c = 0; c < 3; c++) {
            double least = Double.MAX_VALUE;
            double most = -Double.MAX_VALUE;
            for (int k = 0; k < 3; k++) {
                final double v = at[3 * s.corner(t, k) + c];
                least = Math.min(least, v);
                most = Math.max(most, v);
            }
            box[c] = Math.clamp((int) Math.floor((least - cell / 2 - lo[c]) / cell), 0, n[c] - 1);
            box[3 + c] = Math.clamp((int) Math.floor((most + cell / 2 - lo[c]) / cell), 0, n[c] - 1);
        }
    }

    /**
     * For each other muscle with a triangle within half a cell of point {@code p} at {@code x}, {@code y}, {@code z}:
     * the nearest point of its triangles, into {@code q}, three numbers each, and the way that triangle faces out of
     * its muscle, one long, into {@code out}. How many muscles.
     */
    int nearestEach(final double[] at, final SurfaceContact.Surfaces s, final int p, final double x, final double y,
                    final double z, final double[] q, final double[] out) {
        if (best.length < s.muscles()) {
            best = new double[s.muscles()];
            which = new int[s.muscles()];
            touched = new int[s.muscles()];
            Arrays.fill(which, -1);
        }
        final int ci = Math.clamp((int) Math.floor((x - lo[0]) / cell), 0, n[0] - 1);
        final int cj = Math.clamp((int) Math.floor((y - lo[1]) / cell), 0, n[1] - 1);
        final int ck = Math.clamp((int) Math.floor((z - lo[2]) / cell), 0, n[2] - 1);
        final int cellOf = (ck * n[1] + cj) * n[0] + ci;
        final double[] c = new double[3];
        int seen = 0;
        for (int k = start[cellOf]; k < start[cellOf + 1]; k++) {
            final int t = in[k];
            final int o = s.triangleOwner(t);
            if (o == s.owner(p)) {
                continue;
            }
            closest(at, s, t, x, y, z, c);
            final double d = (c[0] - x) * (c[0] - x) + (c[1] - y) * (c[1] - y) + (c[2] - z) * (c[2] - z);
            if (which[o] < 0) {
                touched[seen++] = o;
                best[o] = Double.MAX_VALUE;
            }
            if (d < best[o]) {
                best[o] = d;
                which[o] = t;
            }
        }
        int found = 0;
        for (int i = 0; i < seen; i++) {
            final int o = touched[i];
            final int t = which[o];
            which[o] = -1;
            closest(at, s, t, x, y, z, c);
            if (facing(at, s, t, out, 3 * found)) {
                System.arraycopy(c, 0, q, 3 * found, 3);
                found++;
            }
        }
        return found;
    }

    /** Into {@code out} from {@code from}, the way triangle {@code t} faces out of its muscle; whether it faces any. */
    private boolean facing(final double[] at, final SurfaceContact.Surfaces s, final int t, final double[] out,
                           final int from) {
        final int a = 3 * s.corner(t, 0);
        final int b = 3 * s.corner(t, 1);
        final int e = 3 * s.corner(t, 2);
        final double ux = at[b] - at[a];
        final double uy = at[b + 1] - at[a + 1];
        final double uz = at[b + 2] - at[a + 2];
        final double vx = at[e] - at[a];
        final double vy = at[e + 1] - at[a + 1];
        final double vz = at[e + 2] - at[a + 2];
        final double nx = uy * vz - uz * vy;
        final double ny = uz * vx - ux * vz;
        final double nz = ux * vy - uy * vx;
        final double length = Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 1e-12) {
            return false;
        }
        final double sign = s.turned(s.triangleOwner(t)) ? -1 : 1;
        out[from] = sign * nx / length;
        out[from + 1] = sign * ny / length;
        out[from + 2] = sign * nz / length;
        return true;
    }

    /** Into {@code c}, the point of triangle {@code t} nearest {@code x}, {@code y}, {@code z}. */
    private void closest(final double[] at, final SurfaceContact.Surfaces s, final int t, final double x,
                         final double y, final double z, final double[] c) {
        final int a = 3 * s.corner(t, 0);
        final int b = 3 * s.corner(t, 1);
        final int e = 3 * s.corner(t, 2);
        final double[] ab = {at[b] - at[a], at[b + 1] - at[a + 1], at[b + 2] - at[a + 2]};
        final double[] ac = {at[e] - at[a], at[e + 1] - at[a + 1], at[e + 2] - at[a + 2]};
        final double[] ap = {x - at[a], y - at[a + 1], z - at[a + 2]};
        final double d1 = dot(ab, ap);
        final double d2 = dot(ac, ap);
        if (d1 <= 0 && d2 <= 0) {
            set(c, at, a, ab, ac, 0, 0);
            return;
        }
        final double[] bp = {x - at[b], y - at[b + 1], z - at[b + 2]};
        final double d3 = dot(ab, bp);
        final double d4 = dot(ac, bp);
        if (d3 >= 0 && d4 <= d3) {
            set(c, at, a, ab, ac, 1, 0);
            return;
        }
        final double vc = d1 * d4 - d3 * d2;
        if (vc <= 0 && d1 >= 0 && d3 <= 0) {
            set(c, at, a, ab, ac, d1 / (d1 - d3), 0);
            return;
        }
        final double[] cp = {x - at[e], y - at[e + 1], z - at[e + 2]};
        final double d5 = dot(ab, cp);
        final double d6 = dot(ac, cp);
        if (d6 >= 0 && d5 <= d6) {
            set(c, at, a, ab, ac, 0, 1);
            return;
        }
        final double vb = d5 * d2 - d1 * d6;
        if (vb <= 0 && d2 >= 0 && d6 <= 0) {
            set(c, at, a, ab, ac, 0, d2 / (d2 - d6));
            return;
        }
        final double va = d3 * d6 - d5 * d4;
        if (va <= 0 && d4 - d3 >= 0 && d5 - d6 >= 0) {
            final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
            set(c, at, a, ab, ac, 1 - w, w);
            return;
        }
        final double denom = 1 / (va + vb + vc);
        set(c, at, a, ab, ac, vb * denom, vc * denom);
    }

    private void set(final double[] c, final double[] at, final int a, final double[] ab, final double[] ac,
                     final double v, final double w) {
        for (int k = 0; k < 3; k++) {
            c[k] = at[a + k] + v * ab[k] + w * ac[k];
        }
    }

    private double dot(final double[] u, final double[] v) {
        return u[0] * v[0] + u[1] * v[1] + u[2] * v[2];
    }
}
