package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * Knit round the right foot, as it goes on from the leg ({@link Legwear}): at the ankle, its rings turning through a
 * line across the front of the ankle, clear of it, from level to upright, so that each takes in a slice of the heel,
 * none crossing another; then upright rings along the foot, its top where the shin's front was, to the toes.
 */
public record Foot() implements StatelessFunctionalObject {

    public static final Foot INSTANCE = new Foot();

    /** How far the heel lies from the line the rings turn through, cm, for how many rings it takes. */
    private static final double HEEL = 12;

    /** The rings round the right foot after the leg's, read round as {@code from} goes, put after {@code rings}. */
    public void rings(final Beneath b, final Legwear.Marks m, final double[] from, final List<Legwear.Ring> rings) {
        final V3 hinge = m.hinge();
        final int[] feet = Legwear.INSTANCE.among(b, p -> p.x() > -1 && p.y() < hinge.y() + 6);
        final double[] angles = Legwear.INSTANCE.angles(from, 1);
        final int turning = (int) Math.round(Math.PI / 2 * HEEL / Legwear.APART);
        for (int f = 1; f <= turning; f++) {
            final double a = Math.PI / 2 * f / turning;
            final V3 away = new V3(0, -Math.sin(a), -Math.cos(a));
            rings.add(new Legwear.Ring(new Hoop.Plane(hinge, V3.X, away.times(-1)),
                    p -> p.x() > 0 && p.minus(hinge).dot(away) > 0, angles, feet));
        }
        final double end = m.toes() - 0.5;
        final int along = Math.max(1, (int) Math.round((end - hinge.z()) / Legwear.APART));
        for (int g = 1; g <= along; g++) {
            final double z = hinge.z() + (end - hinge.z()) * g / along;
            rings.add(new Legwear.Ring(new Hoop.Plane(new V3(0, hinge.y(), z), V3.X, V3.Y),
                    p -> p.x() > 0 && p.y() < hinge.y(), angles, feet));
        }
    }
}
