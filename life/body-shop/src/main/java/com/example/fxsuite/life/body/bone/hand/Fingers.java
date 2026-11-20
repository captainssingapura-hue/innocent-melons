package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import static com.example.fxsuite.life.body.Sketch.v;

/** How a finger's bones are drawn, any finger's, in the hand's frame: its metacarpal, and its phalanges on from it. */
public record Fingers() implements StatelessFunctionalObject {

    public static final Fingers INSTANCE = new Fingers();

    /** {@code d}'s metacarpal: its base in the wrist, its slender shaft bowed to the back of the hand, its head. */
    public void metacarpal(final Sketch s, final Digit d, final double light) {
        final double t = d.thick() * light;
        final V3 base = d.base();
        final V3 head = d.head();
        final V3 mid = base.plus(head).times(0.5).plus(v(0, 0, -0.18));
        s.blend(0.45);
        s.egg(base, base.minus(head), v(0, 0, 1), 0.55 * t, 0.55 * t, 0.5 * t);
        s.rod(base, mid, 0.48 * t, 0.36 * t);
        s.rod(mid, head, 0.36 * t, 0.42 * t);
        s.egg(head, head.minus(base), v(0, 0, 1), 0.5 * t, 0.55 * t, 0.48 * t);
    }

    /** {@code d}'s phalanx {@code q} (from its root): its base flared, its shaft, its head; the last a tuft. */
    public void phalanx(final Sketch s, final Digit d, final int q, final double light) {
        final V3[] w = way(d, q);
        final V3 a = w[0];
        final V3 dir = w[1];
        final V3 b = a.plus(dir.times(d.length(q)));
        final double r = 0.46 * d.thick() * light * (1 - 0.17 * q);
        s.blend(0.35);
        s.egg(a.plus(dir.times(r * 0.55)), dir, v(1, 0, 0), r * 0.55, r * 1.12, r * 0.85);
        s.rod(a.plus(dir.times(r * 0.5)), b.minus(dir.times(r * 0.5)), r * 0.8, r * 0.68);
        if (q == d.phalanges() - 1) {
            s.egg(b.minus(dir.times(r * 0.4)), dir, v(1, 0, 0), r * 0.45, r * 0.9, r * 0.5);
        } else {
            s.egg(b.minus(dir.times(r * 0.5)), dir, v(1, 0, 0), r * 0.5, r * 0.98, r * 0.72);
        }
    }

    /** The joint between {@code d}'s phalanges {@code q - 1} and {@code q}: midway from the one's end to the other. */
    public V3 interphalangeal(final Digit d, final int q) {
        final V3[] before = way(d, q - 1);
        final V3 end = before[0].plus(before[1].times(d.length(q - 1)));
        return end.plus(way(d, q)[0]).times(0.5);
    }

    /** Where {@code d}'s phalanx {@code q} starts, and the way it goes: on from its metacarpal, each joint bent on. */
    public V3[] way(final Digit d, final int q) {
        V3 dir = d.head().minus(d.base()).unit();
        V3 at = d.head().plus(dir.times(0.5 * d.thick() + 0.08));
        for (int k = 0; k <= q; k++) {
            dir = bent(dir, d.curl());
            if (k < q) {
                at = at.plus(dir.times(d.length(k) + 0.1));
            }
        }
        return new V3[] {at, dir};
    }

    /** {@code dir} bent {@code degrees} toward the palm. */
    private V3 bent(final V3 dir, final double degrees) {
        final V3 palm = V3.Z.minus(dir.times(dir.z()));
        if (palm.length() < 1e-9) {
            return dir;
        }
        final double a = Math.toRadians(degrees);
        return dir.times(Math.cos(a)).plus(palm.unit().times(Math.sin(a))).unit();
    }
}
