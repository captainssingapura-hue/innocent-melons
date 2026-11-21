package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Weights;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 * Where the fat's outside is: as far out from the muscles and the bones as the fat is thick there ({@link FatMap}),
 * wherever they are, across a groove between two muscles narrower than twice that, rounded over a ridge. Found along a
 * line from outside in, stepping each time as far as it is from being that far out, so never past it; among what of the
 * body the point is on counts, the trunk's or a limb's on its side, so that the arm does not hide the chest, nor the
 * other leg the thigh.
 */
public final class FatTarget {

    /** What of the body a point is on: the trunk, an arm, a leg. */
    public enum Region { TRUNK, ARM, LEG }

    /** A point on the fat's outside: where, and how it follows the body. */
    public record Hit(V3 at, Weights weights) {
    }

    /** How near being as far out as the fat is thick is near enough, cm. */
    private static final double NEAR_ENOUGH = 0.004;

    private final Nearness near;
    private final FatMap map;
    private final Rig bind;
    private final Part[] mainOf;
    private final boolean[] onArm;

    public FatTarget(final Nearness near, final FatMap map, final Rig bind) {
        this.near = near;
        this.map = map;
        this.bind = bind;
        final int[] t = near.beneath().triangles();
        mainOf = new Part[t.length / 3];
        onArm = new boolean[t.length / 3];
        final Set<Part> arms = arms();
        for (int i = 0; i < mainOf.length; i++) {
            mainOf[i] = near.beneath().main()[t[3 * i]];
            onArm[i] = arms.contains(mainOf[i]) && alongArm(near.beneath().point(t[3 * i]), mainOf[i].side());
        }
    }

    /**
     * Whether {@code p} lies along the arm on side {@code side}, out past the shoulder's cap: near the line through
     * its joints, from a fifth of the way down the upper arm to the hand's end. What of the arm's muscles lies over the
     * shoulder, the chest or the back, and not along it, the trunk's as much as the arm's.
     */
    private boolean alongArm(final V3 p, final int side) {
        final Part upper = side > 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
        final V3 shoulder = bind.frame(upper).at();
        final V3 elbow = bind.frame(side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM).at();
        final V3 wrist = bind.frame(side > 0 ? Part.RIGHT_HAND : Part.LEFT_HAND).at();
        final V3 cap = shoulder.plus(elbow.minus(shoulder).times(0.2));
        final V3 end = wrist.plus(wrist.minus(elbow).unit().times(20));
        return Math.min(apart(p, cap, elbow), Math.min(apart(p, elbow, wrist), apart(p, wrist, end))) < 7;
    }

    /** How far {@code p} is from the line from {@code a} to {@code b}. */
    private double apart(final V3 p, final V3 a, final V3 b) {
        final V3 ab = b.minus(a);
        final double s = Math.clamp(p.minus(a).dot(ab) / ab.dot(ab), 0, 1);
        return p.minus(a.plus(ab.times(s))).length();
    }

    private Set<Part> arms() {
        return EnumSet.of(Part.RIGHT_UPPER_ARM, Part.RIGHT_FOREARM, Part.RIGHT_HAND, Part.LEFT_UPPER_ARM,
                Part.LEFT_FOREARM, Part.LEFT_HAND);
    }

    public Rig bind() {
        return bind;
    }

    /**
     * The fat's outside on the line from {@code from} along {@code in}, no further than {@code reach}, among what
     * counts for {@code r} on side {@code side} (1 right, -1 left); none if the line does not meet it.
     */
    public Optional<Hit> onto(final V3 from, final V3 in, final Region r, final int side, final double reach) {
        return onto(from, in, counts(r, side), reach);
    }

    /** As {@link #onto(V3, V3, Region, int, double)}, among the triangles {@code counts} says. */
    public Optional<Hit> onto(final V3 from, final V3 in, final IntPredicate counts, final double reach) {
        final V3 way = in.unit();
        double gone = 0;
        while (gone <= reach) {
            final V3 x = from.plus(way.times(gone));
            final Nearness.Near n = near.nearest(x, 8, counts);
            if (n.triangle() < 0) {
                gone += 6;
                continue;
            }
            final Weights w = near.weights(n);
            final double off = n.distance() - map.thickness(w, x, bind);
            if (off < NEAR_ENOUGH) {
                return Optional.of(new Hit(x, w));
            }
            gone += Math.max(off * 0.9, NEAR_ENOUGH);
        }
        return Optional.empty();
    }

    /**
     * How far {@code x} is out past the fat's outside, among what counts for {@code r} on side {@code side}: less than
     * nothing if it is under it, nearer what is beneath than the fat is thick there.
     */
    public double clearance(final V3 x, final Region r, final int side) {
        final Nearness.Near n = near.nearest(x, 4, counts(r, side));
        return n.triangle() < 0 ? 4 : n.distance() - map.thickness(near.weights(n), x, bind);
    }


    /** How high what goes with part {@code p} beneath the fat reaches: the crown, of the head's. */
    public double highest(final Part p) {
        double top = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < near.beneath().size(); i++) {
            top = near.beneath().main()[i] == p ? Math.max(top, near.beneath().points()[3 * i + 1]) : top;
        }
        return top;
    }

    /**
     * How far {@code x} is from the muscles and bones beneath, among what counts for {@code r} on side {@code side},
     * and how thick the fat should be there, cm: no further than {@code within}, at {@code within} if nothing is.
     */
    public double[] depth(final V3 x, final Region r, final int side, final double within) {
        final Nearness.Near n = near.nearest(x, within, counts(r, side));
        return n.triangle() < 0 ? new double[] {within, 0}
                : new double[] {n.distance(), map.thickness(near.weights(n), x, bind)};
    }

    /** How the body under {@code x} moves: the nearest point beneath's, whatever it is. */
    public Weights under(final V3 x) {
        return near.weights(near.nearest(x, 60, i -> true));
    }

    /**
     * The triangles of what goes with part {@code p} beneath whose first point lies where {@code where} says, once
     * found: to look among them alone, one finger's, say.
     */
    public IntPredicate among(final Part p, final java.util.function.Predicate<V3> where) {
        final int[] t = near.beneath().triangles();
        final boolean[] in = new boolean[mainOf.length];
        for (int i = 0; i < in.length; i++) {
            in[i] = mainOf[i] == p && where.test(near.beneath().point(t[3 * i]));
        }
        return i -> in[i];
    }

    /** Which triangles count for {@code r} on side {@code side}. */
    public IntPredicate counts(final Region r, final int side) {
        final Set<Part> arms = arms();
        final float[] p = near.beneath().points();
        final int[] t = near.beneath().triangles();
        return switch (r) {
            case TRUNK -> i -> !onArm[i];
            case ARM -> i -> mainOf[i].side() == side && (arms.contains(mainOf[i])
                    || mainOf[i] == Part.RIGHT_GIRDLE || mainOf[i] == Part.LEFT_GIRDLE);
            case LEG -> i -> (mainOf[i].side() == side && !arms.contains(mainOf[i]) && mainOf[i] != Part.RIGHT_GIRDLE
                    && mainOf[i] != Part.LEFT_GIRDLE || mainOf[i] == Part.PELVIS) && p[3 * t[3 * i]] * side > -0.5;
        };
    }
}
