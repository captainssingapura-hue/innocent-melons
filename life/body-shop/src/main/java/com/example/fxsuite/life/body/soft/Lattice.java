package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

import java.util.Optional;

/**
 * A muscle's elements in a region to be simulated soft ({@link SoftRegion}): a lattice of cubes, each cut into six
 * tetrahedra about its long diagonal, all cut alike so that neighbours meet face to face; and the muscle's surface
 * carried in it, each of its points in the region held in the tetrahedron it lies in, by four weights. What the
 * simulation moves is the lattice's nodes; the surface follows them. A node on the region's faces or beyond them is
 * fixed, where the muscle is cut. Made by {@link LatticeMaker}.
 *
 * <p>A tetrahedron's corners are, in order, its cube's lowest corner, then one step along one way, then along a second,
 * then its highest: half of them go round one way and half the other, so the simulation takes each one's room at rest
 * with its sign.</p>
 *
 * @param cell    how big its elements are, near enough, cm: a cube's side, or a typical edge
 * @param nodes   where each node is, three numbers each, cm, the body's frame
 * @param tets    each tetrahedron's four nodes, by number
 * @param fixed   whether each node is fixed
 * @param holder  the tetrahedron each of the surface's points is held in, by number; {@code -1} if it is out of the
 *                region, and stays where it is
 * @param weights how much each of its holder's four nodes counts for each of the surface's points, four each
 */
public record Lattice(double cell, float[] nodes, int[] tets, boolean[] fixed, int[] holder, float[] weights) {

    /** How many nodes it has. */
    public int nodeCount() {
        return nodes.length / 3;
    }

    /** How many tetrahedra it has. */
    public int tetCount() {
        return tets.length / 4;
    }

    /** How many of its nodes are fixed. */
    public int fixedCount() {
        int n = 0;
        for (final boolean f : fixed) {
            n += f ? 1 : 0;
        }
        return n;
    }

    /** How much room its elements take, cm3. */
    public double volume() {
        double sum = 0;
        for (int t = 0; t < tetCount(); t++) {
            final V3 a = node(tets[4 * t]);
            sum += Math.abs(node(tets[4 * t + 1]).minus(a).cross(node(tets[4 * t + 2]).minus(a))
                    .dot(node(tets[4 * t + 3]).minus(a))) / 6;
        }
        return sum;
    }

    /** Where node {@code i} is. */
    public V3 node(final int i) {
        return new V3(nodes[3 * i], nodes[3 * i + 1], nodes[3 * i + 2]);
    }

    /**
     * The surface whose points are {@code points}, three numbers each, as the lattice carries it now: each point held
     * in it where its holder's nodes have it, the rest where they were.
     */
    public float[] carrying(final float[] points) {
        final float[] now = points.clone();
        for (int i = 0; i < holder.length; i++) {
            final int s = i;
            carried(i).ifPresent(v -> {
                now[3 * s] = (float) v.x();
                now[3 * s + 1] = (float) v.y();
                now[3 * s + 2] = (float) v.z();
            });
        }
        return now;
    }

    /** Where the surface's point {@code i} is, as its holder's nodes have it; none, if it has no holder. */
    public Optional<V3> carried(final int i) {
        final int t = holder[i];
        if (t < 0) {
            return Optional.empty();
        }
        V3 at = V3.ZERO;
        for (int k = 0; k < 4; k++) {
            at = at.plus(node(tets[4 * t + k]).times(weights[4 * i + k]));
        }
        return Optional.of(at);
    }
}
