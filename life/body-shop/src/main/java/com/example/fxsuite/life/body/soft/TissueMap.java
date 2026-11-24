package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.head.Crossings;

import java.util.List;

/**
 * A region's soft tissue as one piece: the region cut into cells, each given to one tissue, so that no two muscles
 * share any of it and none of it is left between them unless it is meant to be. A cell in one muscle is that muscle's;
 * in two or more, where they are drawn one in another, it is the one's it lies deepest in, so each keeps its own core
 * and the edge they share is shared between them; in bone, nobody's; in none but in a narrow gap between muscles, no
 * wider than twice {@code close}, connective tissue, filling it; else empty. Each muscle's part of it measured
 * against how much of the muscle as drawn lay in the region.
 */
public final class TissueMap {

    /** What a cell outside every muscle holds: nothing, connective tissue between muscles, or bone. */
    public static final int EMPTY = -1;
    public static final int FILLER = -2;
    public static final int BONE = -3;

    private final double[] lo = new double[3];
    private final int[] n = new int[3];
    private final double cell;
    private final int[] label;
    private final int[] drawn;

    /**
     * {@code region} cut into cells {@code cell} a side, given to the muscles whose surfaces are {@code muscles}, by
     * their order, and the bones {@code bones} kept out; gaps no wider than twice {@code close} cm filled.
     */
    public TissueMap(final SoftRegion region, final double cell, final List<Mesher.Mesh> muscles, final Walls bones,
                     final double close) {
        this.cell = cell;
        final double[] l = {region.lo().x(), region.lo().y(), region.lo().z()};
        final double[] h = {region.hi().x(), region.hi().y(), region.hi().z()};
        for (int c = 0; c < 3; c++) {
            lo[c] = l[c];
            n[c] = (int) Math.ceil((h[c] - l[c]) / cell);
        }
        label = new int[n[0] * n[1] * n[2]];
        drawn = new int[muscles.size()];
        final float[] deepest = new float[label.length];
        java.util.Arrays.fill(label, EMPTY);
        for (int i = 0; i < label.length; i++) {
            final V3 c = middle(i);
            if (bones.distance(c.x(), c.y(), c.z()) < 0) {
                label[i] = BONE;
            }
        }
        for (int k = 0; k < muscles.size(); k++) {
            claimed(k, muscles.get(k), deepest);
        }
        filled(close);
    }

    /**
     * Muscle {@code k}'s cells, its surface {@code surface}, found within its box only: each in it counted as drawn,
     * and taken from any other it lies less deep in, as {@code deepest} says each cell's holder lies.
     */
    private void claimed(final int k, final Mesher.Mesh surface, final float[] deepest) {
        final int[] from = new int[3];
        final int[] m = new int[3];
        final float[] p = surface.points();
        for (int c = 0; c < 3; c++) {
            double least = Double.MAX_VALUE;
            double most = -Double.MAX_VALUE;
            for (int i = c; i < p.length; i += 3) {
                least = Math.min(least, p[i]);
                most = Math.max(most, p[i]);
            }
            from[c] = Math.clamp((int) Math.floor((least - lo[c]) / cell) - 1, 0, n[c] - 1);
            final int to = Math.clamp((int) Math.ceil((most - lo[c]) / cell) + 1, 0, n[c] - 1);
            m[c] = to - from[c] + 1;
        }
        final double[] middles = {lo[0] + (from[0] + 0.5) * cell, lo[1] + (from[1] + 0.5) * cell,
            lo[2] + (from[2] + 0.5) * cell};
        final boolean[] in = Crossings.INSTANCE.inside(surface, middles, cell, m);
        final boolean[] out = new boolean[in.length];
        for (int i = 0; i < in.length; i++) {
            out[i] = !in[i];
        }
        final float[] depth = Chamfer.INSTANCE.from(out, m);
        for (int i = 0; i < in.length; i++) {
            if (!in[i]) {
                continue;
            }
            final int g = ((i / (m[0] * m[1]) + from[2]) * n[1] + i / m[0] % m[1] + from[1]) * n[0] + i % m[0] + from[0];
            drawn[k]++;
            if (label[g] != BONE && depth[i] > deepest[g]) {
                deepest[g] = depth[i];
                label[g] = k;
            }
        }
    }

    /** Every empty cell in a gap between muscles no wider than twice {@code close}, cm, given to connective tissue. */
    private void filled(final double close) {
        final boolean[] muscle = new boolean[label.length];
        for (int i = 0; i < label.length; i++) {
            muscle[i] = label[i] >= 0;
        }
        final float[] toMuscle = Chamfer.INSTANCE.from(muscle, n);
        final double steps = close / cell;
        final boolean[] beyond = new boolean[label.length];
        for (int i = 0; i < label.length; i++) {
            beyond[i] = toMuscle[i] > steps;
        }
        final float[] toBeyond = Chamfer.INSTANCE.from(beyond, n);
        for (int i = 0; i < label.length; i++) {
            if (label[i] == EMPTY && !beyond[i] && toBeyond[i] > steps) {
                label[i] = FILLER;
            }
        }
    }

    /** The middle of cell {@code i}, numbered across, then up, then ahead. */
    public V3 middle(final int i) {
        return new V3(lo[0] + (i % n[0] + 0.5) * cell, lo[1] + (i / n[0] % n[1] + 0.5) * cell,
                lo[2] + (i / (n[0] * n[1]) + 0.5) * cell);
    }

    /** What cell {@code i} holds: a muscle, by its number, or {@link #EMPTY}, {@link #FILLER} or {@link #BONE}. */
    public int label(final int i) {
        return label[i];
    }

    /** How many cells there are each way. */
    public int[] counts() {
        return n.clone();
    }

    /** Where the cells begin, their lowest corner. */
    public double[] lowest() {
        return lo.clone();
    }

    public double cell() {
        return cell;
    }

    /** How much room muscle {@code k} has in it, cm3. */
    public double room(final int k) {
        return count(k) * cell * cell * cell;
    }

    /** How much of muscle {@code k} as drawn lay in the region, cm3. */
    public double drawn(final int k) {
        return drawn[k] * cell * cell * cell;
    }

    /** How much connective tissue fills the gaps, cm3. */
    public double filler() {
        return count(FILLER) * cell * cell * cell;
    }

    private int count(final int what) {
        int c = 0;
        for (final int l : label) {
            c += l == what ? 1 : 0;
        }
        return c;
    }
}
