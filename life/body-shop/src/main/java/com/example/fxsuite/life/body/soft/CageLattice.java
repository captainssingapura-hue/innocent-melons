package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A muscle's elements made from its own cage ({@link Cage}), its rings of points from its origin to its insertion,
 * so that they follow it however thin it is, a sheet as well as a strap, and are one piece. Between two rings, more
 * rings laid evenly, none further apart than asked, and each ring's edges cut as finely as asked, alike in every
 * ring; each ring cut alike into triangles about its middle, by a ring halfway out; each triangle and the same of
 * the next ring a prism, cut into three tetrahedra each going round from the corner numbered least, so that two
 * elements meeting meet face to face. Of them, those with a node in the region
 * kept; a node on the region's faces or beyond them fixed. The muscle's surface carried in them, each of its points in
 * the region held in the element it lies in, or, should it lie just outside all, in the nearest ({@link Lattice}).
 */
public record CageLattice() implements StatelessFunctionalObject {

    public static final CageLattice INSTANCE = new CageLattice();

    /** How many points round each of a muscle's rings in its cage. */
    private static final int ROUND = 8;
    /** The most pieces a ring's edge is cut into. */
    private static final int MOST = 8;

    /**
     * The elements of the muscle whose cage is {@code cage}, a ring if {@code ring}, its surface {@code surface}, in
     * {@code region}, its rings no further apart than {@code step}, cm.
     */
    public Lattice of(final SoftRegion region, final Cage cage, final boolean ring, final Mesher.Mesh surface,
                      final double step) {
        final int[] pieces = pieces(cage, step);
        final List<V3[]> slices = new ArrayList<>();
        for (final V3[] r : slices(cage, ring, step)) {
            slices.add(round(r, pieces));
        }
        final int around = slices.getFirst().length;
        final int cut = 1 + 2 * around;
        final float[] nodes = new float[3 * cut * slices.size()];
        for (int s = 0; s < slices.size(); s++) {
            final V3[] r = slices.get(s);
            V3 middle = V3.ZERO;
            for (final V3 p : r) {
                middle = middle.plus(p.times(1.0 / around));
            }
            put(nodes, s * cut, middle);
            for (int q = 0; q < around; q++) {
                put(nodes, s * cut + 1 + q, middle.plus(r[q].minus(middle).times(0.5)));
                put(nodes, s * cut + 1 + around + q, r[q]);
            }
        }
        final boolean[] fixed = new boolean[nodes.length / 3];
        for (int i = 0; i < fixed.length; i++) {
            fixed[i] = !within(region, nodes[3 * i], nodes[3 * i + 1], nodes[3 * i + 2]);
        }
        final List<Integer> tets = new ArrayList<>();
        final int pairs = ring ? slices.size() : slices.size() - 1;
        for (int s = 0; s < pairs; s++) {
            final int low = s * cut;
            final int high = (s + 1) % slices.size() * cut;
            for (final int[] t : triangles(around)) {
                prism(t, low, high, fixed, tets);
            }
        }
        final int[] all = tets.stream().mapToInt(Integer::intValue).toArray();
        final double typical = typical(nodes, all);
        return new Embedding(nodes, all).held(region, surface, typical, fixed);
    }

    /** The muscle's rings, with more laid evenly between any two further apart than {@code step}. */
    private List<V3[]> slices(final Cage cage, final boolean ring, final double step) {
        final int n = cage.size() / ROUND;
        final List<V3[]> slices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            final V3[] a = ringOf(cage, i);
            slices.add(a);
            if (i + 1 == n && !ring) {
                break;
            }
            final V3[] b = ringOf(cage, (i + 1) % n);
            final int between = Math.max(1, (int) Math.ceil(middle(a).minus(middle(b)).length() / step));
            for (int k = 1; k < between && (i + 1 < n || ring); k++) {
                final double t = (double) k / between;
                final V3[] mid = new V3[ROUND];
                for (int q = 0; q < ROUND; q++) {
                    mid[q] = a[q].plus(b[q].minus(a[q]).times(t));
                }
                slices.add(mid);
            }
        }
        return slices;
    }

    /**
     * How many pieces each of a ring's edges is cut into, the same in every ring: as many as keeps the longest of
     * that edge in any ring no longer than {@code step}, no more than {@link #MOST}.
     */
    private int[] pieces(final Cage cage, final double step) {
        final int[] m = new int[ROUND];
        java.util.Arrays.fill(m, 1);
        for (int i = 0; i < cage.size() / ROUND; i++) {
            final V3[] r = ringOf(cage, i);
            for (int q = 0; q < ROUND; q++) {
                final double length = r[(q + 1) % ROUND].minus(r[q]).length();
                m[q] = Math.max(m[q], Math.min(MOST, (int) Math.ceil(length / step)));
            }
        }
        return m;
    }

    /** {@code r}, each of its edges cut into as many pieces as {@code pieces} says, a point at each cut. */
    private V3[] round(final V3[] r, final int[] pieces) {
        final List<V3> out = new ArrayList<>();
        for (int q = 0; q < ROUND; q++) {
            final V3 a = r[q];
            final V3 b = r[(q + 1) % ROUND];
            for (int k = 0; k < pieces[q]; k++) {
                out.add(a.plus(b.minus(a).times((double) k / pieces[q])));
            }
        }
        return out.toArray(new V3[0]);
    }

    private V3[] ringOf(final Cage cage, final int i) {
        final V3[] r = new V3[ROUND];
        for (int q = 0; q < ROUND; q++) {
            r[q] = cage.points().get(i * ROUND + q);
        }
        return r;
    }

    private V3 middle(final V3[] r) {
        V3 m = V3.ZERO;
        for (final V3 p : r) {
            m = m.plus(p.times(1.0 / ROUND));
        }
        return m;
    }

    /** A ring's triangles, by their points among its cut: about its middle, and between its two rings. */
    private List<int[]> triangles(final int around) {
        final List<int[]> t = new ArrayList<>();
        for (int q = 0; q < around; q++) {
            final int next = (q + 1) % around;
            t.add(new int[] {0, 1 + q, 1 + next});
            t.add(new int[] {1 + q, 1 + around + q, 1 + around + next});
            t.add(new int[] {1 + q, 1 + around + next, 1 + next});
        }
        return t;
    }

    /**
     * The prism of triangle {@code t} in the ring from {@code low} and the same in that from {@code high}, cut into
     * three: its corners taken least first, each side of it cut from its lower corner below to its higher above, so a
     * side two prisms share is cut alike in both. Kept unless every node of it is fixed.
     */
    private void prism(final int[] t, final int low, final int high, final boolean[] fixed, final List<Integer> tets) {
        final int[] s = t.clone();
        java.util.Arrays.sort(s);
        final int a = low + s[0];
        final int b = low + s[1];
        final int c = low + s[2];
        final int a2 = high + s[0];
        final int b2 = high + s[1];
        final int c2 = high + s[2];
        for (final int[] tet : new int[][] {{a, b, c, c2}, {a, b, b2, c2}, {a, a2, b2, c2}}) {
            if (!(fixed[tet[0]] && fixed[tet[1]] && fixed[tet[2]] && fixed[tet[3]])) {
                for (final int n : tet) {
                    tets.add(n);
                }
            }
        }
    }

    /** How long an element's edge is, near enough: the middle of all its first edges. */
    private double typical(final float[] nodes, final int[] tets) {
        if (tets.length == 0) {
            return 1;
        }
        final double[] lengths = new double[tets.length / 4];
        for (int t = 0; t < lengths.length; t++) {
            final int a = 3 * tets[4 * t];
            final int b = 3 * tets[4 * t + 3];
            final double dx = nodes[b] - nodes[a];
            final double dy = nodes[b + 1] - nodes[a + 1];
            final double dz = nodes[b + 2] - nodes[a + 2];
            lengths[t] = Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
        java.util.Arrays.sort(lengths);
        return lengths[lengths.length / 2];
    }

    private void put(final float[] nodes, final int i, final V3 v) {
        nodes[3 * i] = (float) v.x();
        nodes[3 * i + 1] = (float) v.y();
        nodes[3 * i + 2] = (float) v.z();
    }

    /** Whether {@code x}, {@code y}, {@code z} is in {@code region} and not on its faces. */
    private boolean within(final SoftRegion region, final double x, final double y, final double z) {
        final double e = 1e-6;
        return x > region.lo().x() + e && y > region.lo().y() + e && z > region.lo().z() + e
                && x < region.hi().x() - e && y < region.hi().y() - e && z < region.hi().z() - e;
    }
}
