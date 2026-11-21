package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A field's surface as triangles ({@link Field}): sampled on a grid {@code cell} apart, only in the blocks of it the
 * surface can be in; a point in every cell the surface passes through, moved onto the surface; and a square across every
 * edge of the grid the surface crosses, between the four cells round it, in two triangles (surface nets). The finer the
 * cell, the finer the detail; the same shape at any detail.
 */
public final class Mesher {

    /** A surface: its points, x, y, z each in turn; its triangles, three points each, going round the way it faces out. */
    public record Mesh(float[] points, int[] triangles) {

        public static final Mesh EMPTY = new Mesh(new float[0], new int[0]);

        public int size() {
            return points.length / 3;
        }

        public int triangleCount() {
            return triangles.length / 3;
        }
    }

    /** How many cells a block is each way. */
    private static final int B = 16;

    private Mesher() {
    }

    /** {@code field}'s surface, its grid {@code cell} apart. */
    public static Mesh of(final Field field, final double cell) {
        final double[] box = field.box();
        if (box[0] > box[3]) {
            return Mesh.EMPTY;
        }
        final Grid g = new Grid(field, cell, new double[] {box[0] - 2 * cell, box[1] - 2 * cell, box[2] - 2 * cell},
                new int[] {cells(box[0], box[3], cell), cells(box[1], box[4], cell), cells(box[2], box[5], cell)});
        final int bx = (g.n[0] + B - 1) / B;
        final int by = (g.n[1] + B - 1) / B;
        final int bz = (g.n[2] + B - 1) / B;
        final List<Found> found = IntStream.range(0, bx * by * bz).parallel()
                .mapToObj(k -> g.block(k % bx, (k / bx) % by, k / (bx * by))).filter(Objects::nonNull).toList();
        return g.join(found);
    }

    private static int cells(final double lo, final double hi, final double cell) {
        return (int) Math.ceil((hi - lo) / cell) + 4;
    }

    /** What a block found: its cells' points (by key) and the edges it owns that the surface crosses. */
    private record Found(long[] cells, float[] at, long[] edges, boolean[] outward) {
    }

    /** The grid a field is sampled on. */
    private static final class Grid {

        private final Field field;
        private final double h;
        private final double[] lo;
        private final int[] n;

        Grid(final Field field, final double h, final double[] lo, final int[] n) {
            this.field = field;
            this.h = h;
            this.lo = lo;
            this.n = n;
        }

        /** What block {@code (bi, bj, bk)} finds; nothing if the surface is not in it. */
        Found block(final int bi, final int bj, final int bk) {
            final int i0 = bi * B;
            final int j0 = bj * B;
            final int k0 = bk * B;
            final double[] blo = {lo[0] + i0 * h, lo[1] + j0 * h, lo[2] + k0 * h};
            final double[] bhi = {blo[0] + B * h, blo[1] + B * h, blo[2] + B * h};
            final int[] near = field.near(blo, bhi, 2 * h);
            if (near.length == 0) {
                return null;
            }
            // too far from the surface for it to be in here, at the middle
            final double half = Math.sqrt(3) * B * h / 2;
            final double mid = field.at((blo[0] + bhi[0]) / 2, (blo[1] + bhi[1]) / 2, (blo[2] + bhi[2]) / 2, near);
            if (Math.abs(mid) > 3 * half) {
                return null;
            }
            final int m = B + 1;
            final float[] s = new float[m * m * m];
            boolean in = false;
            boolean out = false;
            for (int k = 0; k < m; k++) {
                for (int j = 0; j < m; j++) {
                    for (int i = 0; i < m; i++) {
                        final double v = field.at(blo[0] + i * h, blo[1] + j * h, blo[2] + k * h, near);
                        s[(k * m + j) * m + i] = (float) v;
                        in |= v < 0;
                        out |= v >= 0;
                    }
                }
            }
            if (!in || !out) {
                return null;
            }
            final List<Long> cells = new ArrayList<>();
            final FloatList at = new FloatList();
            final List<Long> edges = new ArrayList<>();
            final List<Boolean> outward = new ArrayList<>();
            final float[] corner = new float[8];
            for (int k = 0; k < B; k++) {
                for (int j = 0; j < B; j++) {
                    for (int i = 0; i < B; i++) {
                        for (int c = 0; c < 8; c++) {
                            corner[c] = s[((k + (c >> 2 & 1)) * m + j + (c >> 1 & 1)) * m + i + (c & 1)];
                        }
                        final float[] p = crossing(corner);
                        if (p != null) {
                            cells.add(key(i0 + i, j0 + j, k0 + k));
                            at.add((float) (blo[0] + (i + p[0]) * h), (float) (blo[1] + (j + p[1]) * h),
                                    (float) (blo[2] + (k + p[2]) * h));
                        }
                        // the edges from this corner, each way, that the surface crosses
                        final float here = corner[0];
                        final float[] next = {corner[1], corner[2], corner[4]};
                        for (int a = 0; a < 3; a++) {
                            if (here < 0 != next[a] < 0) {
                                edges.add(key(i0 + i, j0 + j, k0 + k) << 2 | a);
                                outward.add(here < 0);
                            }
                        }
                    }
                }
            }
            final boolean[] o = new boolean[outward.size()];
            for (int q = 0; q < o.length; q++) {
                o[q] = outward.get(q);
            }
            return new Found(cells.stream().mapToLong(Long::longValue).toArray(), at.toArray(),
                    edges.stream().mapToLong(Long::longValue).toArray(), o);
        }

        /** Where in a cell, its eight corners' values given, the surface is: where it crosses its edges, on average. */
        private static float[] crossing(final float[] c) {
            boolean in = false;
            boolean out = false;
            for (final float v : c) {
                in |= v < 0;
                out |= v >= 0;
            }
            if (!in || !out) {
                return null;
            }
            float sx = 0;
            float sy = 0;
            float sz = 0;
            int n = 0;
            for (final int[] e : EDGES) {
                final float a = c[e[0]];
                final float b = c[e[1]];
                if (a < 0 != b < 0) {
                    final float t = a / (a - b);
                    sx += (e[0] & 1) + ((e[1] & 1) - (e[0] & 1)) * t;
                    sy += (e[0] >> 1 & 1) + ((e[1] >> 1 & 1) - (e[0] >> 1 & 1)) * t;
                    sz += (e[0] >> 2 & 1) + ((e[1] >> 2 & 1) - (e[0] >> 2 & 1)) * t;
                    n++;
                }
            }
            return new float[] {sx / n, sy / n, sz / n};
        }

        /** A cell's point, found afresh: for a cell the blocks round it did not find. */
        private float[] cellAt(final int i, final int j, final int k) {
            final double[] clo = {lo[0] + i * h, lo[1] + j * h, lo[2] + k * h};
            final double[] chi = {clo[0] + h, clo[1] + h, clo[2] + h};
            final int[] near = field.near(clo, chi, 2 * h);
            final float[] c = new float[8];
            for (int q = 0; q < 8; q++) {
                c[q] = (float) field.at(clo[0] + (q & 1) * h, clo[1] + (q >> 1 & 1) * h, clo[2] + (q >> 2 & 1) * h, near);
            }
            float[] p = crossing(c);
            if (p == null) {
                p = new float[] {0.5f, 0.5f, 0.5f};
            }
            return new float[] {(float) (clo[0] + p[0] * h), (float) (clo[1] + p[1] * h), (float) (clo[2] + p[2] * h)};
        }

        /** What every block found, as one surface. */
        Mesh join(final List<Found> found) {
            final LongInt index = new LongInt(1 << 16);
            final FloatList points = new FloatList();
            for (final Found f : found) {
                for (int q = 0; q < f.cells().length; q++) {
                    index.put(f.cells()[q], points.size() / 3);
                    points.add(f.at()[3 * q], f.at()[3 * q + 1], f.at()[3 * q + 2]);
                }
            }
            final IntList triangles = new IntList();
            final int[] quad = new int[4];
            for (final Found f : found) {
                for (int q = 0; q < f.edges().length; q++) {
                    final long e = f.edges()[q];
                    final int a = (int) (e & 3);
                    final long c = e >>> 2;
                    final int i = (int) (c >>> 40);
                    final int j = (int) (c >>> 20 & 0xFFFFF);
                    final int k = (int) (c & 0xFFFFF);
                    final int[][] round = switch (a) {
                        case 0 -> new int[][] {{i, j - 1, k - 1}, {i, j, k - 1}, {i, j, k}, {i, j - 1, k}};
                        case 1 -> new int[][] {{i - 1, j, k - 1}, {i, j, k - 1}, {i, j, k}, {i - 1, j, k}};
                        default -> new int[][] {{i - 1, j - 1, k}, {i, j - 1, k}, {i, j, k}, {i - 1, j, k}};
                    };
                    boolean whole = true;
                    for (int r = 0; r < 4; r++) {
                        final int[] cc = round[r];
                        if (cc[0] < 0 || cc[1] < 0 || cc[2] < 0) {
                            whole = false;
                            break;
                        }
                        final long ck = key(cc[0], cc[1], cc[2]);
                        int v = index.get(ck);
                        if (v < 0) {
                            final float[] p = cellAt(cc[0], cc[1], cc[2]);
                            v = points.size() / 3;
                            index.put(ck, v);
                            points.add(p[0], p[1], p[2]);
                        }
                        quad[r] = v;
                    }
                    if (whole) {
                        square(points, quad, a, f.outward()[q], triangles);
                    }
                }
            }
            final float[] p = points.toArray();
            settle(p);
            return new Mesh(p, triangles.toArray());
        }

        /** The square round an edge, as two triangles facing out, along its shorter diagonal. */
        private static void square(final FloatList points, final int[] q, final int axis, final boolean outward,
                                   final IntList into) {
            final float[] p = points.raw();
            final double[] d02 = diff(p, q[2], q[0]);
            final double[] d13 = diff(p, q[3], q[1]);
            final double nx = d02[1] * d13[2] - d02[2] * d13[1];
            final double ny = d02[2] * d13[0] - d02[0] * d13[2];
            final double nz = d02[0] * d13[1] - d02[1] * d13[0];
            final double toward = (axis == 0 ? nx : axis == 1 ? ny : nz) * (outward ? 1 : -1);
            final int[] o = toward >= 0 ? q : new int[] {q[0], q[3], q[2], q[1]};
            final double l02 = d02[0] * d02[0] + d02[1] * d02[1] + d02[2] * d02[2];
            final double l13 = d13[0] * d13[0] + d13[1] * d13[1] + d13[2] * d13[2];
            if (l02 <= l13) {
                triangle(o[0], o[1], o[2], into);
                triangle(o[0], o[2], o[3], into);
            } else {
                triangle(o[1], o[2], o[3], into);
                triangle(o[1], o[3], o[0], into);
            }
        }

        private static void triangle(final int a, final int b, final int c, final IntList into) {
            if (a != b && b != c && c != a) {
                into.add(a, b, c);
            }
        }

        private static double[] diff(final float[] p, final int a, final int b) {
            return new double[] {p[3 * a] - p[3 * b], p[3 * a + 1] - p[3 * b + 1], p[3 * a + 2] - p[3 * b + 2]};
        }

        /** Every point moved onto the surface, along the way the field rises, no further than within its cell. */
        private void settle(final float[] p) {
            final double e = h * 0.25;
            IntStream.range(0, p.length / 3).parallel().forEach(v -> {
                double x = p[3 * v];
                double y = p[3 * v + 1];
                double z = p[3 * v + 2];
                final double[] plo = {x - 2 * h, y - 2 * h, z - 2 * h};
                final double[] phi = {x + 2 * h, y + 2 * h, z + 2 * h};
                final int[] near = field.near(plo, phi, 2 * h);
                final double x0 = x;
                final double y0 = y;
                final double z0 = z;
                for (int step = 0; step < 3; step++) {
                    final double f = field.at(x, y, z, near);
                    final double gx = (field.at(x + e, y, z, near) - field.at(x - e, y, z, near)) / (2 * e);
                    final double gy = (field.at(x, y + e, z, near) - field.at(x, y - e, z, near)) / (2 * e);
                    final double gz = (field.at(x, y, z + e, near) - field.at(x, y, z - e, near)) / (2 * e);
                    final double g2 = gx * gx + gy * gy + gz * gz;
                    if (g2 < 1e-12) {
                        break;
                    }
                    x -= gx * f / g2;
                    y -= gy * f / g2;
                    z -= gz * f / g2;
                }
                // never further than its cell allows, so it does not jump to another surface near by
                final double dx = x - x0;
                final double dy = y - y0;
                final double dz = z - z0;
                final double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
                final double most = 0.75 * h;
                final double k = d > most ? most / d : 1;
                p[3 * v] = (float) (x0 + dx * k);
                p[3 * v + 1] = (float) (y0 + dy * k);
                p[3 * v + 2] = (float) (z0 + dz * k);
            });
        }
    }

    /** The cells' eight corners' edges, by corner: bit 0 x, bit 1 y, bit 2 z. */
    private static final int[][] EDGES = {
        {0, 1}, {2, 3}, {4, 5}, {6, 7}, {0, 2}, {1, 3}, {4, 6}, {5, 7}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

    private static long key(final int i, final int j, final int k) {
        return (long) i << 40 | (long) j << 20 | k;
    }

    /** A growing list of floats. */
    private static final class FloatList {
        private float[] a = new float[1024];
        private int n;

        void add(final float x, final float y, final float z) {
            if (n + 3 > a.length) {
                a = Arrays.copyOf(a, a.length * 2);
            }
            a[n++] = x;
            a[n++] = y;
            a[n++] = z;
        }

        public int size() {
            return n;
        }

        float[] raw() {
            return a;
        }

        float[] toArray() {
            return Arrays.copyOf(a, n);
        }
    }

    /** A growing list of ints. */
    private static final class IntList {
        private int[] a = new int[1024];
        private int n;

        void add(final int x, final int y, final int z) {
            if (n + 3 > a.length) {
                a = Arrays.copyOf(a, a.length * 2);
            }
            a[n++] = x;
            a[n++] = y;
            a[n++] = z;
        }

        int[] toArray() {
            return Arrays.copyOf(a, n);
        }
    }

    /** Ints by long keys, never negative, open addressed. */
    private static final class LongInt {
        private long[] keys;
        private int[] values;
        private int size;

        LongInt(final int capacity) {
            keys = new long[capacity];
            values = new int[capacity];
            Arrays.fill(keys, -1);
        }

        int get(final long k) {
            int i = slot(k, keys.length);
            while (keys[i] != -1) {
                if (keys[i] == k) {
                    return values[i];
                }
                i = (i + 1) & (keys.length - 1);
            }
            return -1;
        }

        void put(final long k, final int v) {
            if (2 * (size + 1) > keys.length) {
                grow();
            }
            int i = slot(k, keys.length);
            while (keys[i] != -1 && keys[i] != k) {
                i = (i + 1) & (keys.length - 1);
            }
            if (keys[i] == -1) {
                size++;
            }
            keys[i] = k;
            values[i] = v;
        }

        private void grow() {
            final long[] ok = keys;
            final int[] ov = values;
            keys = new long[ok.length * 2];
            values = new int[ok.length * 2];
            Arrays.fill(keys, -1);
            size = 0;
            for (int i = 0; i < ok.length; i++) {
                if (ok[i] != -1) {
                    put(ok[i], ov[i]);
                }
            }
        }

        private static int slot(final long k, final int length) {
            long x = k * 0x9E3779B97F4A7C15L;
            x ^= x >>> 29;
            return (int) (x & (length - 1));
        }
    }
}
