package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Closest;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A surface of triangles made a solid, as a field of distances sampled on a grid ({@link Field.Sampled}): near the
 * surface, within {@link #BAND} of it, how far each of the grid's points is from it, less than nothing behind it, the
 * way its nearest triangle faces; further off, only which side it is, as far as the band either way. Which side, for a
 * blank closed but at its neck: inside where a line straight up crosses it an odd number of times ({@link Crossings});
 * for a piece of a head, the side of the blank it is on. Each triangle writes only the points near it, the grid taken a
 * slab at a time on as many threads as there are.
 */
public record MeshSolid() implements StatelessFunctionalObject {

    public static final MeshSolid INSTANCE = new MeshSolid();

    /** How far from the surface its distances are found, cm. */
    private static final double BAND = 0.75;

    /** A grid: its lowest corner, how far apart its points are, how many each way, its values, which are found. */
    private record Grid(double[] lo, double cell, int[] n, float[] v, boolean[] found) {

        int at(final int i, final int j, final int k) {
            return (k * n[1] + j) * n[0] + i;
        }

        V3 point(final int i, final int j, final int k) {
            return new V3(lo[0] + i * cell, lo[1] + j * cell, lo[2] + k * cell);
        }

        Field.Sampled sampled() {
            final double[] box = {lo[0], lo[1], lo[2], lo[0] + (n[0] - 1) * cell, lo[1] + (n[1] - 1) * cell,
                lo[2] + (n[2] - 1) * cell};
            return new Field.Sampled(lo, cell, n, v, box);
        }
    }

    /**
     * The blank {@code m} made a solid on a grid {@code cell} apart, round it but no lower than it, its neck left open
     * at the grid's floor, never reached from outside.
     */
    public Field.Sampled closed(final Mesher.Mesh m, final double cell) {
        final double[] b = bounds(m);
        final Grid g = band(m, new double[] {b[0] - 1, b[1], b[2] - 1, b[3] + 1, b[4] + 1, b[5] + 1}, cell);
        final boolean[] in = Crossings.INSTANCE.inside(m, g.lo(), g.cell(), g.n());
        for (int at = 0; at < in.length; at++) {
            if (!g.found()[at]) {
                g.v()[at] = (float) (in[at] ? -BAND : BAND);
            }
        }
        return g.sampled();
    }

    /**
     * The surface {@code m} made a solid within {@code box} on a grid {@code cell} apart; off its band, inside where
     * {@code inside} is.
     */
    public Field.Sampled within(final Mesher.Mesh m, final double[] box, final double cell, final Field.Shape inside) {
        final Grid g = band(m, box, cell);
        IntStream.range(0, g.n()[2]).parallel().forEach(k -> {
            for (int j = 0; j < g.n()[1]; j++) {
                for (int i = 0; i < g.n()[0]; i++) {
                    if (!g.found()[g.at(i, j, k)]) {
                        final V3 p = g.point(i, j, k);
                        g.v()[g.at(i, j, k)] = (float) (inside.at(p.x(), p.y(), p.z()) < 0 ? -BAND : BAND);
                    }
                }
            }
        });
        return g.sampled();
    }

    /** The grid over {@code box}, {@code cell} apart, its points within the band of {@code m}'s triangles found. */
    private Grid band(final Mesher.Mesh m, final double[] box, final double cell) {
        final int[] n = new int[3];
        for (int a = 0; a < 3; a++) {
            n[a] = (int) Math.ceil((box[a + 3] - box[a]) / cell) + 1;
        }
        final Grid g = new Grid(new double[] {box[0], box[1], box[2]}, cell, n, new float[n[0] * n[1] * n[2]],
                new boolean[n[0] * n[1] * n[2]]);
        final Beneath b = beneath(m);
        final V3[] normals = normals(b);
        final double way = facing(b, normals);
        final List<List<Integer>> slabs = slabs(b, g);
        IntStream.range(0, n[2]).parallel().forEach(k -> {
            for (final int tri : slabs.get(k)) {
                near(g, b, normals, tri, k, way);
            }
        });
        return g;
    }

    /** The points of slab {@code k} near triangle {@code tri} given their distance from it, if nearer than before. */
    private void near(final Grid g, final Beneath b, final V3[] normals, final int tri, final int k,
            final double way) {
        final int[] lo = new int[2];
        final int[] hi = new int[2];
        for (int a = 0; a < 2; a++) {
            double min = 1e9;
            double max = -1e9;
            for (int c = 0; c < 3; c++) {
                final double x = b.points()[3 * b.triangles()[3 * tri + c] + a];
                min = Math.min(min, x);
                max = Math.max(max, x);
            }
            lo[a] = Math.max(0, (int) Math.floor((min - BAND - g.lo()[a]) / g.cell()));
            hi[a] = Math.min(g.n()[a] - 1, (int) Math.ceil((max + BAND - g.lo()[a]) / g.cell()));
        }
        for (int j = lo[1]; j <= hi[1]; j++) {
            for (int i = lo[0]; i <= hi[0]; i++) {
                final V3 p = g.point(i, j, k);
                final Nearness.Near n = Closest.INSTANCE.on(b, tri, p);
                final int at = g.at(i, j, k);
                if (n.distance() < BAND && (!g.found()[at] || n.distance() < Math.abs(g.v()[at]))) {
                    g.v()[at] = (float) (side(b, normals, n, p) * way < 0 ? -n.distance() : n.distance());
                    g.found()[at] = true;
                }
            }
        }
    }

    /** Each slab of the grid, the triangles whose box, grown by the band, reaches it. */
    private List<List<Integer>> slabs(final Beneath b, final Grid g) {
        final List<List<Integer>> slabs = new ArrayList<>();
        for (int k = 0; k < g.n()[2]; k++) {
            slabs.add(new ArrayList<>());
        }
        for (int tri = 0; tri < b.triangles().length / 3; tri++) {
            double min = 1e9;
            double max = -1e9;
            for (int c = 0; c < 3; c++) {
                final double z = b.points()[3 * b.triangles()[3 * tri + c] + 2];
                min = Math.min(min, z);
                max = Math.max(max, z);
            }
            final int k0 = Math.max(0, (int) Math.floor((min - BAND - g.lo()[2]) / g.cell()));
            final int k1 = Math.min(g.n()[2] - 1, (int) Math.ceil((max + BAND - g.lo()[2]) / g.cell()));
            for (int k = k0; k <= k1; k++) {
                slabs.get(k).add(tri);
            }
        }
        return slabs;
    }

    /**
     * Which side {@code p} is of the triangle it is nearest, {@code n}: ahead of it, more than nothing, the way the
     * surface faces there, its corners' {@code normals} taken as much as the nearest point is near each, so that a
     * point off a sharp edge, as near the triangles either side of it, is not taken now for one's and now the other's.
     */
    private double side(final Beneath b, final V3[] normals, final Nearness.Near n, final V3 p) {
        final int[] t = b.triangles();
        final int i = t[3 * n.triangle()];
        final int j = t[3 * n.triangle() + 1];
        final int k = t[3 * n.triangle() + 2];
        final V3 q = b.point(i).times(n.a()).plus(b.point(j).times(n.b())).plus(b.point(k).times(n.c()));
        return p.minus(q).dot(normals[i].times(n.a()).plus(normals[j].times(n.b())).plus(normals[k].times(n.c())));
    }

    /** The way the surface faces at each of its points, the way its corners go round: its triangles' there, by area. */
    private V3[] normals(final Beneath b) {
        final V3[] n = new V3[b.size()];
        Arrays.fill(n, V3.ZERO);
        final int[] t = b.triangles();
        for (int k = 0; k < t.length; k += 3) {
            final V3 a = b.point(t[k]);
            final V3 f = b.point(t[k + 1]).minus(a).cross(b.point(t[k + 2]).minus(a));
            for (int c = 0; c < 3; c++) {
                n[t[k + c]] = n[t[k + c]].plus(f);
            }
        }
        for (int i = 0; i < n.length; i++) {
            n[i] = n[i].length() > 0 ? n[i].unit() : n[i];
        }
        return n;
    }

    /**
     * Which way the surface's triangles face out: one, if the middle of its points is behind its triangle nearest it,
     * as a closed surface facing out has it; less than nothing, if not.
     */
    private double facing(final Beneath b, final V3[] normals) {
        V3 mid = V3.ZERO;
        for (int i = 0; i < b.size(); i++) {
            mid = mid.plus(b.point(i));
        }
        mid = mid.times(1.0 / b.size());
        Nearness.Near best = null;
        for (int tri = 0; tri < b.triangles().length / 3; tri++) {
            final Nearness.Near n = Closest.INSTANCE.on(b, tri, mid);
            best = best == null || n.distance() < best.distance() ? n : best;
        }
        return side(b, normals, best, mid) < 0 ? 1 : -1;
    }

    /** The lowest and the highest of {@code m}'s points each way. */
    private double[] bounds(final Mesher.Mesh m) {
        final double[] b = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int i = 0; i < m.points().length; i++) {
            b[i % 3] = Math.min(b[i % 3], m.points()[i]);
            b[i % 3 + 3] = Math.max(b[i % 3 + 3], m.points()[i]);
        }
        return b;
    }

    /** {@code m} as a surface to find nearest points on. */
    private Beneath beneath(final Mesher.Mesh m) {
        final Part[] main = new Part[m.size()];
        Arrays.fill(main, Part.HEAD);
        final Weights[] w = new Weights[main.length];
        Arrays.fill(w, new Weights(new float[Part.values().length]));
        return new Beneath(m.points(), m.triangles(), main, w);
    }
}
