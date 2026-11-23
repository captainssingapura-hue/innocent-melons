package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A body's skin with a clay head joined to it by a band ({@link NeckBand}), made one surface, bound as the skin was:
 * the skin's points following what they followed, those made where it was cut ({@link NeckCut}) as the point kept
 * beside them, where they are between it and the other; each of the band's the parts the point of the body's edge
 * nearest below it follows, less as far up the band as it is, and the head the rest; the clay head's the head alone.
 * Its ways out found afresh.
 */
public record NeckShares() implements StatelessFunctionalObject {

    public static final NeckShares INSTANCE = new NeckShares();

    /**
     * The surface of {@code all}, in the head's frame {@code f}: the skin's kept, cut, {@code body}, first; the band's
     * from {@code at[0]}, its {@code at[3]} less one rings of {@code at[2]}; the clay head's from {@code at[1]}; its
     * triangles {@code faces}; the body's edge {@code foot}.
     */
    public Flesh flesh(final Flesh skin, final Rig.Frame f, final V3[] all, final List<int[]> faces,
                       final NeckEdge.Edge foot, final NeckCut.Cut body, final int[] at) {
        final int n = all.length;
        final int k = Flesh.PARTS;
        final int band = at[0];
        final int clay = at[1];
        final int[] which = new int[k * n];
        final float[] shares = new float[k * n];
        final int head = skin.parts().indexOf(Part.HEAD);
        final V3[] bound = new V3[n];
        for (int i = 0; i < n; i++) {
            if (i < band) {
                final int from = source(skin, body, i);
                System.arraycopy(skin.which(), k * from, which, k * i, k);
                System.arraycopy(skin.shares(), k * from, shares, k * i, k);
                bound[i] = i < skin.size() ? skin.point(i) : made(skin, body, i - skin.size());
            } else {
                final double t = i >= clay ? 1 : (double) ((i - band) / at[2] + 1) / at[3];
                final int below = i >= clay ? -1
                        : source(skin, body, foot.at()[foot.nearest(NeckBand.INSTANCE.round((i - band) % at[2]))]);
                share(skin, below, head, t, which, shares, i);
                bound[i] = f.place(all[i]);
            }
        }
        final int[] t = new int[3 * faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            System.arraycopy(faces.get(i), 0, t, 3 * i, 3);
        }
        return new Flesh(new float[3 * n], new float[3 * n], t, skin.parts(), skin.bound(), which, shares)
                .reshaped(bound);
    }

    /**
     * The skin's point whose shares point {@code i} of the cut body takes: its own; a made one's, the one kept by it.
     */
    private int source(final Flesh skin, final NeckCut.Cut body, final int i) {
        return i < skin.size() ? i : body.made()[i - skin.size()][0];
    }

    /** Where the {@code m}th point made on the body's cut was, as the body was bound. */
    private V3 made(final Flesh skin, final NeckCut.Cut body, final int m) {
        final double t = body.along()[m];
        return skin.point(body.made()[m][0]).times(1 - t).plus(skin.point(body.made()[m][1]).times(t));
    }

    /**
     * Into point {@code i}'s shares: the head's, {@code t}; and those of {@code below}, a point of the skin, if any,
     * each {@code 1 - t} times; the four largest kept, made to come to one.
     */
    private void share(final Flesh skin, final int below, final int head, final double t, final int[] which,
                       final float[] shares, final int i) {
        final int k = Flesh.PARTS;
        final double[] by = new double[skin.parts().size()];
        by[head] += t;
        if (below >= 0) {
            for (int s = 0; s < k; s++) {
                final int q = skin.which()[k * below + s];
                if (q >= 0) {
                    by[q] += (1 - t) * skin.shares()[k * below + s];
                }
            }
        }
        double sum = 0;
        for (int s = 0; s < k; s++) {
            int best = -1;
            for (int q = 0; q < by.length; q++) {
                if (by[q] > 0 && (best < 0 || by[q] > by[best])) {
                    best = q;
                }
            }
            which[k * i + s] = best;
            shares[k * i + s] = best < 0 ? 0 : (float) by[best];
            sum += best < 0 ? 0 : by[best];
            if (best >= 0) {
                by[best] = 0;
            }
        }
        for (int s = 0; s < k; s++) {
            shares[k * i + s] = (float) (shares[k * i + s] / sum);
        }
    }
}
