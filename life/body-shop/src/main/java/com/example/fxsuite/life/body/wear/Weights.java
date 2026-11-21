package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.flesh.Flesh;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * How much a point follows each part as the body moves, adding to one, by the parts' order: what a point of a garment
 * takes from the points under it, between two of them as far as it lies between them, and gives the other side
 * mirrored.
 */
public record Weights(float[] by) implements Scalar {

    /** As far from these as toward {@code o} as {@code t}. */
    public Weights toward(final Weights o, final double t) {
        final float[] w = new float[by.length];
        for (int i = 0; i < w.length; i++) {
            w[i] = (float) (by[i] * (1 - t) + o.by[i] * t);
        }
        return new Weights(w);
    }

    /** These, the other side's: each part's share given to its mirror. */
    public Weights mirrored() {
        final float[] w = new float[by.length];
        for (final Part p : Part.values()) {
            w[p.other().ordinal()] += by[p.ordinal()];
        }
        return new Weights(w);
    }

    /** The part followed most. */
    public Part most() {
        int best = 0;
        for (int i = 1; i < by.length; i++) {
            best = by[i] > by[best] ? i : best;
        }
        return Part.values()[best];
    }

    /**
     * The {@link Flesh#PARTS} parts followed most, into {@code which} (by number in {@code parts}, {@code -1} none) and
     * {@code shares} from {@code at}, made to add to one.
     */
    public void strongest(final List<Part> parts, final int[] which, final float[] shares, final int at) {
        final Part[] all = Part.values();
        final boolean[] taken = new boolean[by.length];
        float sum = 0;
        for (int k = 0; k < Flesh.PARTS; k++) {
            int best = -1;
            for (int i = 0; i < by.length; i++) {
                if (!taken[i] && by[i] > 1e-4f && (best < 0 || by[i] > by[best])) {
                    best = i;
                }
            }
            which[at + k] = best < 0 ? -1 : parts.indexOf(all[best]);
            shares[at + k] = best < 0 ? 0 : by[best];
            sum += best < 0 ? 0 : by[best];
            if (best >= 0) {
                taken[best] = true;
            }
        }
        for (int k = 0; k < Flesh.PARTS; k++) {
            shares[at + k] /= sum;
        }
    }
}
