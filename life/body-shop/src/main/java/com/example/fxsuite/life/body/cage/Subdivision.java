package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A cage smoothed, a step at a time (Catmull and Clark's): each face given a point at its middle, each edge one near its
 * middle, each point moved toward its neighbours, and each face cut into as many four-sided faces as it had sides; so
 * the surface is smooth in the end, and each step four times as many faces. An edge creased (DeRose, Kass and Truong's
 * creases) kept sharp, its point at its middle and the points either side moved only along it, for as many steps as
 * its sharpness, between smooth and sharp for what is left over; three creases or more meeting, a corner, kept. An edge
 * with a face on one side only, where the surface ends, kept sharp. A point keeps its number from step to step, the
 * edges' points numbered after the points, the faces' after those.
 */
public record Subdivision() implements StatelessFunctionalObject {

    public static final Subdivision INSTANCE = new Subdivision();

    /**
     * A surface at a step: its points, three numbers each; its faces, each its points round the way it faces out; how
     * sharp each creased edge is, by its key ({@link #key}).
     */
    public record Level(double[] points, List<int[]> faces, Map<Long, Double> sharp) {

        public int size() {
            return points.length / 3;
        }

        public V3 point(final int i) {
            return new V3(points[3 * i], points[3 * i + 1], points[3 * i + 2]);
        }
    }

    /** A level's edges: each one's two points and two faces ({@code -1} none), how sharp, each point's edges. */
    private record Edges(Map<Long, Integer> index, int[] ends, int[] faces, double[] sharp, int[][] around) {

        int count() {
            return sharp.length;
        }

        boolean open(final int e) {
            return faces[2 * e + 1] < 0;
        }

        int other(final int e, final int p) {
            return ends[2 * e] == p ? ends[2 * e + 1] : ends[2 * e];
        }
    }

    /** The edge from {@code a} to {@code b}'s key, the same either way round. */
    public long key(final int a, final int b) {
        return ((long) Math.min(a, b) << 32) | Math.max(a, b);
    }

    /** {@code cage} as it stands, its creases as sharp as a body defined {@code definition} has them. */
    public Level of(final Cage cage, final double definition) {
        final double[] p = new double[3 * cage.size()];
        for (int i = 0; i < cage.size(); i++) {
            final V3 v = cage.points().get(i);
            p[3 * i] = v.x();
            p[3 * i + 1] = v.y();
            p[3 * i + 2] = v.z();
        }
        final Map<Long, Double> sharp = new HashMap<>();
        for (final Crease c : cage.creases()) {
            final double s = c.sharpness(definition);
            for (int i = 0; s > 0 && i + 1 < c.path().size(); i++) {
                sharp.merge(key(c.path().get(i), c.path().get(i + 1)), s, Math::max);
            }
        }
        return new Level(p, cage.faces(), sharp);
    }

    /** {@code l} smoothed {@code steps} times. */
    public Level times(final Level l, final int steps) {
        Level at = l;
        for (int i = 0; i < steps; i++) {
            at = once(at);
        }
        return at;
    }

    /** {@code l} smoothed once. */
    public Level once(final Level l) {
        final Edges e = edges(l);
        final int v = l.size();
        final int ne = e.count();
        final double[] out = new double[3 * (v + ne + l.faces().size())];
        for (int f = 0; f < l.faces().size(); f++) {
            final int[] face = l.faces().get(f);
            for (final int c : face) {
                for (int k = 0; k < 3; k++) {
                    out[3 * (v + ne + f) + k] += l.points()[3 * c + k] / face.length;
                }
            }
        }
        for (int i = 0; i < ne; i++) {
            edgePoint(l, e, i, out, v + ne);
        }
        final double[] q = new double[3 * v];
        final int[] faces = new int[v];
        for (int f = 0; f < l.faces().size(); f++) {
            for (final int c : l.faces().get(f)) {
                faces[c]++;
                for (int k = 0; k < 3; k++) {
                    q[3 * c + k] += out[3 * (v + ne + f) + k];
                }
            }
        }
        for (int i = 0; i < v; i++) {
            vertexPoint(l, e, i, q, faces[i], out);
        }
        final List<int[]> next = new ArrayList<>();
        for (int f = 0; f < l.faces().size(); f++) {
            final int[] c = l.faces().get(f);
            for (int i = 0; i < c.length; i++) {
                final int before = e.index().get(key(c[(i + c.length - 1) % c.length], c[i]));
                final int after = e.index().get(key(c[i], c[(i + 1) % c.length]));
                next.add(new int[] {c[i], v + after, v + ne + f, v + before});
            }
        }
        final Map<Long, Double> sharp = new HashMap<>();
        for (int i = 0; i < ne; i++) {
            if (e.sharp()[i] > 1) {
                sharp.put(key(e.ends()[2 * i], v + i), e.sharp()[i] - 1);
                sharp.put(key(v + i, e.ends()[2 * i + 1]), e.sharp()[i] - 1);
            }
        }
        return new Level(out, next, sharp);
    }

    /** Which of {@code l}'s faces each of its next step's comes of, by number, in the order {@link #once} makes them. */
    public int[] parents(final Level l) {
        return from(l, 0);
    }

    /** Which of {@code l}'s faces each of its triangles comes of, by number, in the order {@link #mesh} makes them. */
    public int[] triangleFaces(final Level l) {
        return from(l, 2);
    }

    /** Each face's number, once for each of its corners but {@code fewer}, face by face. */
    private int[] from(final Level l, final int fewer) {
        int n = 0;
        for (final int[] f : l.faces()) {
            n += f.length - fewer;
        }
        final int[] from = new int[n];
        int k = 0;
        for (int f = 0; f < l.faces().size(); f++) {
            for (int i = fewer; i < l.faces().get(f).length; i++) {
                from[k++] = f;
            }
        }
        return from;
    }

    /** {@code l} as triangles: each four-sided face cut across its shorter way, any other fanned. */
    public Mesher.Mesh mesh(final Level l) {
        final float[] p = new float[l.points().length];
        for (int i = 0; i < p.length; i++) {
            p[i] = (float) l.points()[i];
        }
        final List<Integer> t = new ArrayList<>();
        for (final int[] f : l.faces()) {
            final int cut = f.length == 4 && l.point(f[1]).minus(l.point(f[3])).length()
                    < l.point(f[0]).minus(l.point(f[2])).length() ? 1 : 0;
            for (int i = 1; i + 1 < f.length; i++) {
                t.add(f[cut]);
                t.add(f[(cut + i) % f.length]);
                t.add(f[(cut + i + 1) % f.length]);
            }
        }
        return new Mesher.Mesh(p, t.stream().mapToInt(Integer::intValue).toArray());
    }

    /** Edge {@code i}'s new point: at its middle if sharp or open, else with its faces' too, or between. */
    private void edgePoint(final Level l, final Edges e, final int i, final double[] out, final int facesFrom) {
        final int a = e.ends()[2 * i];
        final int b = e.ends()[2 * i + 1];
        final double s = e.open(i) ? 1 : Math.min(e.sharp()[i], 1);
        for (int k = 0; k < 3; k++) {
            final double mid = (l.points()[3 * a + k] + l.points()[3 * b + k]) / 2;
            final double smooth = e.open(i) ? mid : (l.points()[3 * a + k] + l.points()[3 * b + k]
                    + out[3 * (facesFrom + e.faces()[2 * i]) + k] + out[3 * (facesFrom + e.faces()[2 * i + 1]) + k])
                    / 4;
            out[3 * (l.size() + i) + k] = smooth + (mid - smooth) * s;
        }
    }

    /**
     * Point {@code i} moved: toward its faces' and its edges' middles, if no more than one of its edges is creased;
     * along its two creases, if two are; kept, if more; between, as sharp as its creases are on the whole.
     */
    private void vertexPoint(final Level l, final Edges e, final int i, final double[] q, final int nf,
                             final double[] out) {
        final int[] around = e.around()[i];
        final int n = around.length;
        if (n == 0) {
            System.arraycopy(l.points(), 3 * i, out, 3 * i, 3);
            return;
        }
        int creased = 0;
        double sharp = 0;
        final int[] along = new int[2];
        final double[] r = new double[3];
        boolean open = false;
        for (final int edge : around) {
            final int o = e.other(edge, i);
            for (int k = 0; k < 3; k++) {
                r[k] += (l.points()[3 * i + k] + l.points()[3 * o + k]) / 2;
            }
            final double s = e.open(edge) ? 1 : Math.min(e.sharp()[edge], 1);
            open |= e.open(edge);
            if (s > 0) {
                if (creased < 2) {
                    along[creased] = o;
                }
                creased++;
                sharp += s;
            }
        }
        final double weight = open ? 1 : creased == 0 ? 0 : sharp / creased;
        for (int k = 0; k < 3; k++) {
            final double v = l.points()[3 * i + k];
            final double smooth = open ? v : (q[3 * i + k] / nf + 2 * r[k] / n + (n - 3) * v) / n;
            final double sharpened = creased == 2 ? (l.points()[3 * along[0] + k] + 6 * v
                    + l.points()[3 * along[1] + k]) / 8 : v;
            out[3 * i + k] = creased < 2 ? smooth : smooth + (sharpened - smooth) * weight;
        }
    }

    /** {@code l}'s edges, found from its faces. */
    private Edges edges(final Level l) {
        final Map<Long, Integer> index = new HashMap<>();
        final List<int[]> found = new ArrayList<>();
        for (int f = 0; f < l.faces().size(); f++) {
            final int[] c = l.faces().get(f);
            for (int i = 0; i < c.length; i++) {
                final long k = key(c[i], c[(i + 1) % c.length]);
                final Integer at = index.get(k);
                if (at == null) {
                    index.put(k, found.size());
                    found.add(new int[] {c[i], c[(i + 1) % c.length], f, -1});
                } else {
                    found.get(at)[3] = f;
                }
            }
        }
        final int n = found.size();
        final int[] ends = new int[2 * n];
        final int[] faces = new int[2 * n];
        final double[] sharp = new double[n];
        final int[] count = new int[l.size()];
        for (int i = 0; i < n; i++) {
            final int[] x = found.get(i);
            ends[2 * i] = x[0];
            ends[2 * i + 1] = x[1];
            faces[2 * i] = x[2];
            faces[2 * i + 1] = x[3];
            sharp[i] = l.sharp().getOrDefault(key(x[0], x[1]), 0.0);
            count[x[0]]++;
            count[x[1]]++;
        }
        final int[][] around = new int[l.size()][];
        for (int p = 0; p < around.length; p++) {
            around[p] = new int[count[p]];
            count[p] = 0;
        }
        for (int i = 0; i < n; i++) {
            around[ends[2 * i]][count[ends[2 * i]]++] = i;
            around[ends[2 * i + 1]][count[ends[2 * i + 1]]++] = i;
        }
        return new Edges(index, ends, faces, sharp, around);
    }
}
