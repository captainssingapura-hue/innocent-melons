package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;
import com.example.fxsuite.life.body.bone.hand.Fingers;

import java.util.ArrayList;
import java.util.List;

/**
 * Where a right hand's lines are, in its own frame as on the right side (x toward the thumb, y up the arm, z out of
 * the palm), as its bones hang ({@link Digit}, {@link Fingers}): each finger's, from its metacarpal's base along its
 * bones to its tip; the palm's lanes, two in line with each finger at the knuckles, spread evenly across the wrist at
 * its top; where the rings round each finger are; which finger a point is nearest.
 */
final class HandLines {

    /** How many points across the back of the hand, and across its front. */
    static final int ACROSS = 8;
    /** How far across the wrist is, either side of its middle, cm. */
    private static final double WRIST = 2.6;

    /** How far across lane {@code j} is at {@code y} down the hand, at the knuckles if {@code knuckles}. */
    double across(final int j, final double y, final boolean knuckles) {
        final Digit d = Digit.values()[j / 2 + 1];
        final double t = Math.clamp((y - d.base().y()) / (d.head().y() - d.base().y()), 0, 1);
        final double x = d.base().x() + t * (d.head().x() - d.base().x());
        final boolean radial = j % 2 == 0;
        final double edge = knuckles ? 0.55 : j == 0 || j == ACROSS - 1 ? 0.95 : 0.45;
        final double lane = x + (radial ? edge : -edge);
        final double even = WRIST - 2 * WRIST * j / (ACROSS - 1);
        final double s = Math.clamp(-y / 6.6, 0, 1);
        return even * (1 - s) + lane * s;
    }

    /** How far down the hand lane {@code j}'s finger's knuckle is. */
    double knuckle(final int j) {
        return Digit.values()[j / 2 + 1].head().y();
    }

    /** Where {@code d}'s rings are along its bones, each where and which way the bone goes there. */
    List<V3[]> stations(final Digit d) {
        final List<V3[]> at = new ArrayList<>();
        if (d == Digit.THUMB) {
            final V3 way = d.head().minus(d.base()).unit();
            for (final double f : new double[] {0.55, 0.85}) {
                at.add(new V3[] {d.base().plus(d.head().minus(d.base()).times(f)), way});
            }
        }
        final double[][] shares = d == Digit.THUMB ? new double[][] {{0.3, 0.75}, {0.35, 0.7, 0.92}}
                : new double[][] {{0.15, 0.5, 0.85}, {0.3, 0.7}, {0.35, 0.7, 0.92}};
        for (int q = 0; q < d.phalanges(); q++) {
            final V3[] w = Fingers.INSTANCE.way(d, q);
            for (final double f : shares[q]) {
                at.add(new V3[] {w[0].plus(w[1].times(d.length(q) * f)), w[1]});
            }
        }
        return at;
    }

    /** The finger, or the thumb, whose bones {@code p} is nearest. */
    Digit nearest(final V3 p) {
        Digit best = Digit.THUMB;
        double near = Double.MAX_VALUE;
        for (final Digit d : Digit.values()) {
            final double far = apart(p, line(d, 0));
            if (far < near) {
                near = far;
                best = d;
            }
        }
        return best;
    }


    /** {@code d}'s line from its metacarpal's base to its tip, {@code shy} cm short of it. */
    private List<V3> line(final Digit d, final double shy) {
        final List<V3> line = new ArrayList<>(List.of(d.base(), d.head()));
        for (int q = 0; q < d.phalanges(); q++) {
            final V3[] w = Fingers.INSTANCE.way(d, q);
            line.add(w[0]);
            if (q == d.phalanges() - 1) {
                line.add(w[0].plus(w[1].times(d.length(q) - shy)));
            }
        }
        return line;
    }

    private double apart(final V3 p, final List<V3> line) {
        double near = Double.MAX_VALUE;
        for (int k = 0; k + 1 < line.size(); k++) {
            near = Math.min(near, p.minus(closest(p, line.get(k), line.get(k + 1))).length());
        }
        return near;
    }

    private V3 closest(final V3 p, final V3 a, final V3 b) {
        final V3 ab = b.minus(a);
        final double s = ab.dot(ab) < 1e-12 ? 0 : Math.clamp(p.minus(a).dot(ab) / ab.dot(ab), 0, 1);
        return a.plus(ab.times(s));
    }
}
