package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Spread;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A body's fat as a layer of its own, every body's the same points and faces: wrapped round its fat's outside, its
 * cage smoothed {@link #STEPS} times, each point set on that outside ({@link Wrap}), so that the muscles' shapes show
 * through it as far as the fat is thin; its points eased toward evenly apart along the surface and set on it again,
 * twice; what stands out set back, narrow grooves filled, what cuts under a muscle's edge pushed out. Each point
 * following the body as what is under it does, spread along the surface ({@link Spread}), bound to the body where it
 * was found, to move with it.
 */
public record FatLayer() implements StatelessFunctionalObject {

    public static final FatLayer INSTANCE = new FatLayer();

    /** How many times its cage is smoothed; how far out a point looks back in from, and how far in at most, cm. */
    private static final int STEPS = 3;
    private static final double BACK = 3;
    private static final int EASING = 2;
    /** How many times narrow grooves are filled; how many times what still lies too near beneath is pushed out. */
    private static final int FILL = 4;
    private static final int CLEARING = 3;
    /** How far, at most, a triangle under a muscle's edge is pushed out, cm. */
    private static final double SLIVER = 0.4;
    /** How far a point may stand out from its neighbours' middle, or sink, before it is set back among them, cm. */
    private static final double SPIKE = 0.6;

    /** How far a point may be set from where its smoothed cage put it, cm: past that, it stays. */
    private static final double FAR = 4;
    /** How many times how each point follows the body is spread along the surface. */
    private static final int SPREADING = 30;

    /**
     * The fat as found: its cage, its surface bound to the body, where its points found no fat outside, and what of
     * the body each point is on.
     */
    public record Fat(Cage cage, Flesh flesh, List<V3> missed, FatTarget.Region[] regions) {
    }

    /** The fat on {@code target}. */
    public Fat of(final FatTarget target) {
        final Wrap.Shape shape = Wrap.INSTANCE.start(target, STEPS);
        final V3[] at = shape.at();
        final int[] t = shape.triangles();
        final FatTarget.Region[] region = shape.regions();
        final V3[] smooth = at.clone();
        final Weights[] w = new Weights[at.length];
        boolean[] missed = Wrap.INSTANCE.onto(target, shape, smooth, w, BACK, FAR);
        final int[][] next = Around.INSTANCE.neighbours(at.length, t);
        for (int round = 0; round < EASING; round++) {
            ease(at, t, next);
            missed = Wrap.INSTANCE.onto(target, shape, smooth, w, BACK / 2, FAR);
        }
        for (int round = 0; round < EASING; round++) {
            despike(at, t, next);
        }
        fill(at, t, next);
        for (int round = 0; round < CLEARING; round++) {
            clear(target, at, t, region);
        }
        final Weights[] spread = Spread.INSTANCE.over(w, next, SPREADING);
        final Sheet sheet = new Sheet();
        final List<V3> misses = new ArrayList<>();
        for (int i = 0; i < at.length; i++) {
            sheet.add(at[i], spread[i]);
            if (missed[i]) {
                misses.add(at[i]);
            }
        }
        for (int i = 0; i < t.length; i += 3) {
            sheet.triangle(t[i], t[i + 1], t[i + 2]);
        }
        return new Fat(shape.cage(), sheet.flesh(target.bind()), misses, region);
    }

    /**
     * Narrow grooves filled, as fat fills the valleys between muscles: {@link #FILL} times, every point that lies below
     * its neighbours' middle moved half way out to it, along its way out; none moved in, so that nothing beneath shows.
     */
    private void fill(final V3[] at, final int[] t, final int[][] next) {
        for (int round = 0; round < FILL; round++) {
            final V3[] out = Around.INSTANCE.normals(at, t);
            final V3[] moved = new V3[at.length];
            for (int i = 0; i < at.length; i++) {
                V3 m = V3.ZERO;
                for (final int j : next[i]) {
                    m = m.plus(at[j].times(1.0 / next[i].length));
                }
                final double up = m.minus(at[i]).dot(out[i]);
                moved[i] = up > 0 ? at[i].plus(out[i].times(up / 2)) : at[i];
            }
            System.arraycopy(moved, 0, at, 0, at.length);
        }
    }

    /**
     * What lies too near beneath pushed out: each triangle's middle that is nearer the muscles or the bones than the
     * fat is thick there pushing its corners out along their ways out by as much as it falls short, the most any of its
     * triangles asks; so that a triangle between two points on the fat's outside does not cut under a muscle's edge.
     * Only so far as such a sliver asks, {@link #SLIVER}: a triangle further under than that is not under a muscle's
     * edge but near what is not its own, the other thigh by the crotch, and is left.
     */
    private void clear(final FatTarget target, final V3[] at, final int[] t, final FatTarget.Region[] region) {
        final V3[] out = Around.INSTANCE.normals(at, t);
        final double[] push = new double[at.length];
        final double[] wanting = IntStream.range(0, t.length / 3).parallel().mapToDouble(k -> {
            final V3 c = at[t[3 * k]].plus(at[t[3 * k + 1]]).plus(at[t[3 * k + 2]]).times(1.0 / 3);
            return -target.clearance(c, region[t[3 * k]], c.x() < 0 ? -1 : 1);
        }).toArray();
        for (int k = 0; k < wanting.length; k++) {
            for (int c = 0; c < 3; c++) {
                push[t[3 * k + c]] = wanting[k] < SLIVER ? Math.max(push[t[3 * k + c]], wanting[k])
                        : push[t[3 * k + c]];
            }
        }
        for (int i = 0; i < at.length; i++) {
            at[i] = push[i] > 0.01 ? at[i].plus(out[i].times(push[i])) : at[i];
        }
    }

    /**
     * A point standing out from its neighbours, or sunk among them, further than {@link #SPIKE}, set at their middle:
     * found on something its neighbours were not, as a line in slips past an edge; what the body is changes more
     * gently than that between points so near.
     */
    private void despike(final V3[] at, final int[] t, final int[][] next) {
        final V3[] out = Around.INSTANCE.normals(at, t);
        final V3[] moved = at.clone();
        for (int i = 0; i < at.length; i++) {
            V3 m = V3.ZERO;
            for (final int j : next[i]) {
                m = m.plus(at[j].times(1.0 / next[i].length));
            }
            if (Math.abs(at[i].minus(m).dot(out[i])) > SPIKE) {
                moved[i] = m;
            }
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }

    /** Every point moved half way toward its neighbours' middle, along the surface only. */
    private void ease(final V3[] at, final int[] t, final int[][] next) {
        final V3[] out = Around.INSTANCE.normals(at, t);
        final V3[] moved = new V3[at.length];
        for (int i = 0; i < at.length; i++) {
            V3 m = V3.ZERO;
            for (final int j : next[i]) {
                m = m.plus(at[j].times(1.0 / next[i].length));
            }
            final V3 d = m.minus(at[i]);
            moved[i] = at[i].plus(d.minus(out[i].times(d.dot(out[i]))).times(0.5));
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }





}
