package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;
import com.example.fxsuite.life.body.bone.hand.Fingers;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Underneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The soft of the fingers and the thumb round their bones, the pads and what lies between the skin and the bone all
 * round, as one rod along each of their bones, as thick as the finger is ({@link Digit}), a little less toward its tip;
 * so that a finger is as thick as a finger, not its bone in a skin. Both hands, as the body is held to be bound,
 * following the hand.
 */
public record Pads() implements StatelessFunctionalObject {

    public static final Pads INSTANCE = new Pads();

    /** How many points round each rod, and how many rings along it. */
    private static final int ROUND = 12;
    private static final int ALONG = 6;
    /** How thick a finger's soft is round its bone, against how thick the finger's bones are, cm. */
    private static final double SOFT = 0.78;

    /** The pads of both hands where {@code bind} holds them. */
    public Beneath of(final Rig bind) {
        Beneath all = null;
        for (final Part p : new Part[] {Part.RIGHT_HAND, Part.LEFT_HAND}) {
            final Rig.Frame hand = bind.frame(p);
            for (final Digit d : Digit.values()) {
                for (int q = 0; q < d.phalanges(); q++) {
                    final V3[] w = Fingers.INSTANCE.way(d, q);
                    final double r = SOFT * d.thick() * (1 - 0.12 * q);
                    final Beneath rod = rod(hand.place(w[0]), hand.place(w[0].plus(w[1].times(d.length(q)))), r, p);
                    all = all == null ? rod : Underneath.INSTANCE.with(all, rod);
                }
            }
        }
        return all;
    }

    /** A rod from {@code a} to {@code b}, {@code r} round, its ends rounded, closed, following part {@code p}. */
    private Beneath rod(final V3 a, final V3 b, final double r, final Part p) {
        final V3 way = b.minus(a).unit();
        final V3 u = way.cross(Math.abs(way.y()) < 0.9 ? V3.Y : V3.X).unit();
        final V3 w = way.cross(u).unit();
        final List<V3> at = new ArrayList<>();
        at.add(a.minus(way.times(r)));
        for (int i = 0; i < ALONG; i++) {
            final double s = (double) i / (ALONG - 1);
            final V3 c = a.plus(b.minus(a).times(s));
            final double rr = r * (i == 0 || i == ALONG - 1 ? 0.8 : 1);
            for (int k = 0; k < ROUND; k++) {
                final double t = 2 * Math.PI * k / ROUND;
                at.add(c.plus(u.times(rr * Math.cos(t))).plus(w.times(rr * Math.sin(t))));
            }
        }
        at.add(b.plus(way.times(r)));
        final List<Integer> t = new ArrayList<>();
        final int top = at.size() - 1;
        for (int k = 0; k < ROUND; k++) {
            final int n = (k + 1) % ROUND;
            t.addAll(List.of(0, 1 + n, 1 + k));
            final int last = 1 + (ALONG - 1) * ROUND;
            t.addAll(List.of(top, last + k, last + n));
            for (int i = 0; i + 1 < ALONG; i++) {
                final int c = 1 + i * ROUND;
                t.addAll(List.of(c + k, c + n, c + ROUND + n, c + k, c + ROUND + n, c + ROUND + k));
            }
        }
        final float[] pts = new float[3 * at.size()];
        for (int i = 0; i < at.size(); i++) {
            pts[3 * i] = (float) at.get(i).x();
            pts[3 * i + 1] = (float) at.get(i).y();
            pts[3 * i + 2] = (float) at.get(i).z();
        }
        final Part[] main = new Part[at.size()];
        Arrays.fill(main, p);
        final float[] alone = new float[Part.values().length];
        alone[p.ordinal()] = 1;
        final Weights[] weights = new Weights[at.size()];
        Arrays.fill(weights, new Weights(alone));
        return new Beneath(pts, t.stream().mapToInt(Integer::intValue).toArray(), main, weights);
    }
}
