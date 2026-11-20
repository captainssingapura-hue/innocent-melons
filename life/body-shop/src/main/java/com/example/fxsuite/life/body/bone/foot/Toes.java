package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * How a toe's bones are drawn, any toe's: its metatarsal in the foot's frame, its phalanges in the toes' frame, from
 * the joint the toes turn at.
 */
public record Toes() implements StatelessFunctionalObject {

    public static final Toes INSTANCE = new Toes();

    /** The toes' joint, in the foot's frame: where the toes' frame is. */
    public static final V3 JOINT = v(0, -5.4, 13.1);

    /** {@code toe}'s metatarsal: its base, its shaft bowed up a little, its head; the fifth with a knob at its base. */
    public void metatarsal(final Sketch s, final Toe toe, final double light) {
        final V3 base = toe.base();
        final V3 head = toe.head();
        final V3 mid = base.plus(head).times(0.5).plus(v(0, 0.25, 0));
        s.blend(0.6);
        s.egg(base, toe.baseR() * light, toe.baseR() * 1.25, toe.baseR() * 0.8);
        s.rod(base, mid, toe.baseR() * 0.8 * light, toe.shaftR() * light);
        s.rod(mid, head, toe.shaftR() * light, toe.headR() * 0.75 * light);
        s.egg(head, toe.headR() * light, toe.headR() * 1.1, toe.headR());
        if (toe == Toe.LITTLE) {
            s.ball(v(3.0, -4.4, 6.05), 0.55);
        }
    }

    /** {@code toe}'s phalanx {@code q} (from its root), in the toes' frame: on from its metatarsal's head. */
    public void phalanx(final Sketch s, final Toe toe, final int q) {
        final V3 head = toe.head().minus(JOINT);
        double z = head.z() + toe.headR() + 0.12;
        for (int k = 0; k < q; k++) {
            z += toe.length(k) + 0.12;
        }
        final double len = toe.length(q);
        // the toes droop a little toward their tips
        final double y0 = head.y() + 0.05 - 0.12 * (z - head.z());
        final V3 a = v(head.x() + 0.03 * z, y0, z);
        final V3 b = v(head.x() + 0.03 * (z + len), y0 - 0.12 * len, z + len);
        final double r = toe.phalanxR() * (1 - 0.15 * q);
        s.blend(0.4);
        s.egg(a.plus(v(0, 0, r * 0.5)), r * 1.1, r * 0.95, r * 0.6);
        s.rod(a.plus(v(0, 0, r * 0.4)), b.minus(v(0, 0, r * 0.4)), r * 0.72, r * 0.62);
        if (q == toe.phalanges() - 1) {
            s.egg(b.minus(v(0, 0.05, r * 0.35)), r * 0.85, r * 0.5, r * 0.45);
        } else {
            s.egg(b.minus(v(0, 0, r * 0.4)), r * 0.9, r * 0.75, r * 0.5);
        }
    }
}
