package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * How a row of teeth is drawn, the upper or the lower, both sides, in the head's frame: each in its place in the arch,
 * meeting in a bite, the front lowest and rising back, the lower front teeth tucked behind the upper; incisors chisels,
 * canines points, premolars and molars rounded blocks hollowed among their cusps; each with its root into the jaw.
 */
public record Teeth() implements StatelessFunctionalObject {

    public static final Teeth INSTANCE = new Teeth();

    /** The upper row, or the lower. */
    public void draw(final Sketch s, final boolean upper) {
        s.blend(0.08);
        final Tooth[] all = Tooth.values();
        for (final int side : new int[] {1, -1}) {
            for (final Tooth t : all) {
                final int i = t.ordinal();
                final double bite = -1.45 + 0.06 * i;
                final double tall = t.tall(upper);
                final double top = upper ? bite + tall : bite + (i < 3 ? 0.35 : 0);
                final V3 at = v(side * t.out(upper), upper ? bite + tall / 2 : top - tall / 2, t.ahead(upper));
                final Tooth next = all[Math.min(i + 1, all.length - 1)];
                final Tooth last = all[Math.max(i - 1, 0)];
                final V3 along = v(side * (next.out(upper) - last.out(upper)), 0,
                        next.ahead(upper) - last.ahead(upper)).unit();
                crown(s, t, upper, at, along);
                s.rod(at, at.plus(v(0, upper ? 1.3 : -1.3, 0)), Math.min(t.broad(upper), t.deep(upper)) * 0.32, 0.12);
            }
        }
    }

    private void crown(final Sketch s, final Tooth t, final boolean upper, final V3 at, final V3 along) {
        final double tall = t.tall(upper);
        final double deep = t.deep(upper);
        if (t == Tooth.CENTRAL_INCISOR || t == Tooth.LATERAL_INCISOR) {
            s.egg(at, along, v(0, 1, 0), t.broad(upper) / 2, tall / 2, deep / 2 * 0.6);
        } else if (t == Tooth.CANINE) {
            s.rod(at.plus(v(0, upper ? 0.25 : -0.25, 0)), at.plus(v(0, upper ? -0.5 : 0.5, 0)), deep / 2, 0.12);
        } else {
            s.egg(at, along, v(0, 1, 0), t.broad(upper) / 2, tall / 2, deep / 2);
            s.carve().ball(at.plus(v(0, upper ? -tall / 2 - 0.12 : tall / 2 + 0.12, 0)), deep * 0.22);
        }
    }
}
