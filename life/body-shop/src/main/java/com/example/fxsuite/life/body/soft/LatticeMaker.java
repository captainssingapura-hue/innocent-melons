package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.head.Crossings;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A muscle's elements in a region ({@link Lattice}): the region cut into cubes {@code cell} on a side, from its lowest
 * corner; of them, each whose middle is inside the muscle, and each that one of the muscle's surface's points in the
 * region lies in, so that every such point is held in an element. Each cube cut into six tetrahedra about its long
 * diagonal, one for each order the three ways can be taken in, so a point lies in the one whose order is the order
 * of how far along it is each way, and its four weights fall out of those three numbers. Nodes are shared by the
 * cubes that meet at them; a node on the region's faces or beyond them is fixed.
 */
public record LatticeMaker() implements StatelessFunctionalObject {

    public static final LatticeMaker INSTANCE = new LatticeMaker();

    /** The orders the three ways can be taken in: one tetrahedron of a cube for each. */
    private static final int[][] ORDERS = {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};

    /** The elements of the muscle whose surface is {@code surface}, in {@code region}, cubes {@code cell} a side. */
    public Lattice of(final SoftRegion region, final Mesher.Mesh surface, final double cell) {
        final double[] lo = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] hi = {region.hi().x(), region.hi().y(), region.hi().z()};
        final float[] p = surface.points();
        final int[] from = new int[3];
        final int[] to = new int[3];
        final double[] least = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] most = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        for (int i = 0; i < p.length; i += 3) {
            for (int c = 0; c < 3; c++) {
                least[c] = Math.min(least[c], p[i + c]);
                most[c] = Math.max(most[c], p[i + c]);
            }
        }
        for (int c = 0; c < 3; c++) {
            final int cells = (int) Math.ceil((hi[c] - lo[c]) / cell);
            from[c] = Math.clamp((int) Math.floor((least[c] - lo[c]) / cell), 0, cells - 1);
            to[c] = Math.clamp((int) Math.floor((most[c] - lo[c]) / cell), 0, cells - 1);
        }
        final int[] n = {to[0] - from[0] + 1, to[1] - from[1] + 1, to[2] - from[2] + 1};
        final double[] middle = {lo[0] + (from[0] + 0.5) * cell, lo[1] + (from[1] + 0.5) * cell,
            lo[2] + (from[2] + 0.5) * cell};
        final boolean[] kept = Crossings.INSTANCE.inside(surface, middle, cell, n);
        for (int i = 0; i < p.length; i += 3) {
            final V3 v = new V3(p[i], p[i + 1], p[i + 2]);
            if (region.has(v)) {
                kept[index(cellOf(v, lo, cell, from, n), n)] = true;
            }
        }
        return built(region, surface, cell, lo, from, n, kept);
    }

    /** The lattice of the cubes {@code kept}, numbered across, then up, then ahead, from {@code from}. */
    private Lattice built(final SoftRegion region, final Mesher.Mesh surface, final double cell, final double[] lo,
                          final int[] from, final int[] n, final boolean[] kept) {
        final Map<Long, Integer> numbered = new HashMap<>();
        final List<Float> nodes = new ArrayList<>();
        final int[] firstTet = new int[kept.length];
        final List<Integer> tets = new ArrayList<>();
        for (int c = 0; c < kept.length; c++) {
            firstTet[c] = -1;
            if (!kept[c]) {
                continue;
            }
            final int i = c % n[0] + from[0];
            final int j = c / n[0] % n[1] + from[1];
            final int k = c / (n[0] * n[1]) + from[2];
            final int[] corner = new int[8];
            for (int b = 0; b < 8; b++) {
                final int ci = i + (b & 1);
                final int cj = j + (b >> 1 & 1);
                final int ck = k + (b >> 2 & 1);
                corner[b] = numbered.computeIfAbsent(((long) ci << 42) | ((long) cj << 21) | ck, key -> {
                    nodes.add((float) (lo[0] + ci * cell));
                    nodes.add((float) (lo[1] + cj * cell));
                    nodes.add((float) (lo[2] + ck * cell));
                    return nodes.size() / 3 - 1;
                });
            }
            firstTet[c] = tets.size() / 4;
            for (final int[] order : ORDERS) {
                final int a = 1 << order[0];
                final int b = a | 1 << order[1];
                tets.add(corner[0]);
                tets.add(corner[a]);
                tets.add(corner[b]);
                tets.add(corner[7]);
            }
        }
        final float[] at = new float[nodes.size()];
        for (int i = 0; i < at.length; i++) {
            at[i] = nodes.get(i);
        }
        final boolean[] fixed = new boolean[at.length / 3];
        for (int i = 0; i < fixed.length; i++) {
            fixed[i] = !within(region, at[3 * i], at[3 * i + 1], at[3 * i + 2]);
        }
        return held(region, surface, cell, lo, from, n, at, tets.stream().mapToInt(Integer::intValue).toArray(),
                fixed, firstTet);
    }

    /** The lattice made, each of the surface's points in the region held in the tetrahedron it lies in. */
    private Lattice held(final SoftRegion region, final Mesher.Mesh surface, final double cell, final double[] lo,
                         final int[] from, final int[] n, final float[] nodes, final int[] tets,
                         final boolean[] fixed, final int[] firstTet) {
        final float[] p = surface.points();
        final int[] holder = new int[p.length / 3];
        final float[] weights = new float[4 * holder.length];
        for (int s = 0; s < holder.length; s++) {
            final V3 v = new V3(p[3 * s], p[3 * s + 1], p[3 * s + 2]);
            holder[s] = -1;
            if (!region.has(v)) {
                continue;
            }
            final int[] c = cellOf(v, lo, cell, from, n);
            final double[] u = {(v.x() - lo[0]) / cell - (c[0] + from[0]), (v.y() - lo[1]) / cell - (c[1] + from[1]),
                (v.z() - lo[2]) / cell - (c[2] + from[2])};
            for (int o = 0; o < ORDERS.length; o++) {
                final int[] w = ORDERS[o];
                if (u[w[0]] >= u[w[1]] && u[w[1]] >= u[w[2]]) {
                    holder[s] = firstTet[index(c, n)] + o;
                    weights[4 * s] = (float) (1 - u[w[0]]);
                    weights[4 * s + 1] = (float) (u[w[0]] - u[w[1]]);
                    weights[4 * s + 2] = (float) (u[w[1]] - u[w[2]]);
                    weights[4 * s + 3] = (float) u[w[2]];
                    break;
                }
            }
        }
        return new Lattice(cell, nodes, tets, fixed, holder, weights);
    }

    /** The cube {@code v} lies in, counted from {@code from}, kept within the {@code n} there are. */
    private int[] cellOf(final V3 v, final double[] lo, final double cell, final int[] from, final int[] n) {
        final double[] at = {v.x(), v.y(), v.z()};
        final int[] c = new int[3];
        for (int a = 0; a < 3; a++) {
            c[a] = Math.clamp((int) Math.floor((at[a] - lo[a]) / cell) - from[a], 0, n[a] - 1);
        }
        return c;
    }

    /** Whether {@code x}, {@code y}, {@code z} is in {@code region} and not on its faces. */
    private boolean within(final SoftRegion region, final double x, final double y, final double z) {
        final double e = 1e-6;
        return x > region.lo().x() + e && y > region.lo().y() + e && z > region.lo().z() + e
                && x < region.hi().x() - e && y < region.hi().y() - e && z < region.hi().z() - e;
    }

    private int index(final int[] c, final int[] n) {
        return (c[2] * n[1] + c[1]) * n[0] + c[0];
    }
}
