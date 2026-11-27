package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * A hand closed round a grip, a bar across its palm, bent from how it hangs: each finger's joints bent further toward
 * the palm, the knuckles most and the tips least, the thumb a little, its tip over the bar. Whatever is drawn of a hand
 * as it hangs, its bones, its skin or a glove over it, closed so: each point following the finger it lies along, turned
 * about each of that finger's joints it lies past, partly where it lies across a joint so that nothing tears there; the
 * palm and the wrist as they were. In the hand's frame as on the right side (x toward the thumb, y up the arm, z out
 * of the palm), the papers' centimetres. And the bar it closes round: its line through the hand.
 */
public record Grasp() implements StatelessFunctionalObject {

    public static final Grasp INSTANCE = new Grasp();

    /** How much further a finger's joints bend, knuckle first, and the thumb's, degrees. */
    private static final double KNUCKLE = 55;
    private static final double MIDDLE = 70;
    private static final double END = 35;
    private static final double THUMB_KNUCKLE = 15;
    private static final double THUMB_END = 30;
    /** How far either side of a joint a point is turned partly with it, cm. */
    private static final double ACROSS = 0.7;

    /** {@code points}, x, y and z each, in the hand's frame as it hangs, closed round the grip. */
    public float[] held(final float[] points) {
        final float[] held = points.clone();
        for (int i = 0; i < points.length; i += 3) {
            final V3 p = new V3(points[i], points[i + 1], points[i + 2]);
            final V3 q = held(p, nearest(p));
            held[i] = (float) q.x();
            held[i + 1] = (float) q.y();
            held[i + 2] = (float) q.z();
        }
        return held;
    }

    /**
     * The bar's line through the hand, closed: a point on it on the index finger's side and one on the little
     * finger's, each the middle of the ring that finger's knuckle and its next two joints lie on, closed.
     */
    public V3[] axis() {
        return new V3[] {ringed(Digit.INDEX), ringed(Digit.LITTLE)};
    }

    /** The middle of the circle through {@code d}'s knuckle and its next two joints, closed. */
    private V3 ringed(final Digit d) {
        final V3 a = d.head();
        final V3 b = held(Fingers.INSTANCE.interphalangeal(d, 1), d);
        final V3 c = held(Fingers.INSTANCE.interphalangeal(d, 2), d);
        final V3 ab = b.minus(a);
        final V3 ac = c.minus(a);
        final V3 n = ab.cross(ac);
        final V3 to = n.cross(ab).times(ac.dot(ac)).plus(ac.cross(n).times(ab.dot(ab))).times(1 / (2 * n.dot(n)));
        return a.plus(to);
    }

    /** {@code p}, lying along {@code d}, its joints bent further, the furthest from the palm first. */
    private V3 held(final V3 p, final Digit d) {
        V3 q = p;
        for (int k = d.phalanges() - 1; k >= 0; k--) {
            final V3[] way = Fingers.INSTANCE.way(d, k);
            final V3 joint = k == 0 ? d.head() : Fingers.INSTANCE.interphalangeal(d, k);
            final double past = p.minus(joint).dot(way[1]);
            final double share = Math.clamp(0.5 + past / (2 * ACROSS), 0, 1);
            if (share > 0) {
                final V3 axis = way[1].cross(V3.Z);
                q = joint.plus(Rot.about(axis, bend(d, k) * share).apply(q.minus(joint)));
            }
        }
        return q;
    }

    /** How much further {@code d}'s joint {@code k} bends, the knuckle 0. */
    private double bend(final Digit d, final int k) {
        if (d == Digit.THUMB) {
            return k == 0 ? THUMB_KNUCKLE : THUMB_END;
        }
        return k == 0 ? KNUCKLE : k == 1 ? MIDDLE : END;
    }

    /** The finger {@code p} lies nearest along, its metacarpal and its phalanges as it hangs. */
    private Digit nearest(final V3 p) {
        Digit best = Digit.MIDDLE;
        double near = Double.MAX_VALUE;
        for (final Digit d : Digit.values()) {
            double far = apart(p, d.base(), d.head());
            for (int k = 0; k < d.phalanges(); k++) {
                final V3[] way = Fingers.INSTANCE.way(d, k);
                far = Math.min(far, apart(p, way[0], way[0].plus(way[1].times(d.length(k)))));
            }
            if (far < near) {
                near = far;
                best = d;
            }
        }
        return best;
    }

    /** How far {@code p} is from the segment from {@code a} to {@code b}. */
    private double apart(final V3 p, final V3 a, final V3 b) {
        final V3 ab = b.minus(a);
        final double t = Math.clamp(p.minus(a).dot(ab) / ab.dot(ab), 0, 1);
        return p.minus(a.plus(ab.times(t))).length();
    }
}
