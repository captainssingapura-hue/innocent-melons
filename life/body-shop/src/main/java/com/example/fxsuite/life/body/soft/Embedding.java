package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A muscle's surface laid in elements made for it (as {@link CageLattice} makes them): each of its points in a region
 * found in the element it lies in, by a grid over the elements' boxes, and held there by four weights; one lying just
 * outside every element held in the nearest, its weights reaching beyond it, as the element carries it; one far
 * outside every element not carried at all, and left where it is. Nodes no element uses let go.
 */
final class Embedding {

    /** How far outside its element, by its least weight, a point may lie and still be carried by it. */
    private static final double FAR = 1.0;

    private final float[] nodes;
    private final int[] tets;

    /** Elements {@code tets}, four nodes each, of nodes at {@code nodes}, three numbers each. */
    Embedding(final float[] nodes, final int[] tets) {
        this.nodes = nodes;
        this.tets = tets;
    }

    /**
     * The lattice of these elements, nodes no element uses let go, fixed as {@code fixed} says, each of
     * {@code surface}'s points in {@code region} held in its element, {@code typical} a typical edge's length.
     */
    Lattice held(final SoftRegion region, final Mesher.Mesh surface, final double typical, final boolean[] fixed) {
        final int[] renumbered = new int[fixed.length];
        java.util.Arrays.fill(renumbered, -1);
        int kept = 0;
        for (final int n : tets) {
            if (renumbered[n] < 0) {
                renumbered[n] = kept++;
            }
        }
        final float[] at = new float[3 * kept];
        final boolean[] fix = new boolean[kept];
        for (int i = 0; i < renumbered.length; i++) {
            if (renumbered[i] >= 0) {
                System.arraycopy(nodes, 3 * i, at, 3 * renumbered[i], 3);
                fix[renumbered[i]] = fixed[i];
            }
        }
        final int[] t = new int[tets.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = renumbered[tets[i]];
        }
        final float[] p = surface.points();
        final int[] holder = new int[p.length / 3];
        final float[] weights = new float[4 * holder.length];
        final Map<Long, List<Integer>> grid = grid(at, t, typical);
        for (int s = 0; s < holder.length; s++) {
            final V3 v = new V3(p[3 * s], p[3 * s + 1], p[3 * s + 2]);
            holder[s] = region.has(v) ? found(at, t, grid, typical, v, weights, s) : -1;
            if (holder[s] >= 0 && outside(weights, s) > FAR) {
                holder[s] = -1;
            }
        }
        return new Lattice(typical, at, t, fix, holder, weights);
    }

    /** How far below nothing the {@code s}th point's least weight is: how far outside its element it lies. */
    private double outside(final float[] weights, final int s) {
        double least = 0;
        for (int k = 0; k < 4; k++) {
            least = Math.min(least, weights[4 * s + k]);
        }
        return -least;
    }

    /** Each element by the cells of a grid {@code cell} apart its box touches. */
    private Map<Long, List<Integer>> grid(final float[] at, final int[] t, final double cell) {
        final Map<Long, List<Integer>> grid = new HashMap<>();
        for (int e = 0; e < t.length / 4; e++) {
            final int[] lo = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
            final int[] hi = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
            for (int k = 0; k < 4; k++) {
                for (int c = 0; c < 3; c++) {
                    final int i = (int) Math.floor(at[3 * t[4 * e + k] + c] / cell);
                    lo[c] = Math.min(lo[c], i);
                    hi[c] = Math.max(hi[c], i);
                }
            }
            for (int i = lo[0]; i <= hi[0]; i++) {
                for (int j = lo[1]; j <= hi[1]; j++) {
                    for (int k = lo[2]; k <= hi[2]; k++) {
                        grid.computeIfAbsent(key(i, j, k), x -> new ArrayList<>()).add(e);
                    }
                }
            }
        }
        return grid;
    }

    /**
     * The element {@code v} lies in, or, lying in none near, the nearest of those near, its weights put in
     * {@code weights} as the {@code s}th point's; {@code -1} if no element is near.
     */
    private int found(final float[] at, final int[] t, final Map<Long, List<Integer>> grid, final double cell,
                      final V3 v, final float[] weights, final int s) {
        int best = -1;
        double bestLeast = -Double.MAX_VALUE;
        final double[] w = new double[4];
        final double[] bestW = new double[4];
        final int ci = (int) Math.floor(v.x() / cell);
        final int cj = (int) Math.floor(v.y() / cell);
        final int ck = (int) Math.floor(v.z() / cell);
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                for (int dk = -1; dk <= 1; dk++) {
                    for (final int e : grid.getOrDefault(key(ci + di, cj + dj, ck + dk), List.of())) {
                        final double least = weighed(at, t, e, v, w);
                        if (least > bestLeast) {
                            bestLeast = least;
                            best = e;
                            System.arraycopy(w, 0, bestW, 0, 4);
                        }
                    }
                }
            }
        }
        for (int k = 0; k < 4 && best >= 0; k++) {
            weights[4 * s + k] = (float) bestW[k];
        }
        return best;
    }

    /** {@code v}'s weights in element {@code e}, put in {@code w}; the least of them, below nothing if outside it. */
    private double weighed(final float[] at, final int[] t, final int e, final V3 v, final double[] w) {
        final V3 a = node(at, t[4 * e]);
        final V3 b = node(at, t[4 * e + 1]).minus(a);
        final V3 c = node(at, t[4 * e + 2]).minus(a);
        final V3 d = node(at, t[4 * e + 3]).minus(a);
        final double det = b.dot(c.cross(d));
        if (Math.abs(det) < 1e-15) {
            return -Double.MAX_VALUE;
        }
        final V3 p = v.minus(a);
        w[1] = p.dot(c.cross(d)) / det;
        w[2] = b.dot(p.cross(d)) / det;
        w[3] = b.dot(c.cross(p)) / det;
        w[0] = 1 - w[1] - w[2] - w[3];
        return Math.min(Math.min(w[0], w[1]), Math.min(w[2], w[3]));
    }

    private V3 node(final float[] at, final int i) {
        return new V3(at[3 * i], at[3 * i + 1], at[3 * i + 2]);
    }

    private long key(final int i, final int j, final int k) {
        return ((long) (i & 0x1FFFFF) << 42) | ((long) (j & 0x1FFFFF) << 21) | (k & 0x1FFFFF);
    }
}
