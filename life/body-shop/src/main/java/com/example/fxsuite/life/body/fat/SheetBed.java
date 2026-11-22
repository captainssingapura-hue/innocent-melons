package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.Bed;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Underneath;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;

/**
 * What a body's sheets of muscle lie on ({@link Bed}): its other muscles, the deep ones too, and its bones, as one
 * surface, the nearest of it found within {@link #REACH}; the way out of it there, its nearest triangle's.
 */
public record SheetBed() implements StatelessFunctionalObject {

    public static final SheetBed INSTANCE = new SheetBed();

    /** How far a sheet's bed is looked for, cm. */
    private static final double REACH = 6;

    /** The muscles of a body made as {@code p} says, the deep ones if {@code deep}, its sheets laid on the rest. */
    public MuscleBody.Bound body(final Proportions p, final boolean deep, final int steps) {
        final Rig bind = new Rig(p, BoundPose.INSTANCE.pose(), false);
        return MuscleBody.INSTANCE.make(p, deep, steps, muscles -> of(muscles, bind));
    }

    /** The bed of {@code muscles} and the bones of the body {@code bind} holds. */
    public Bed of(final Flesh muscles, final Rig bind) {
        final Underneath u = Underneath.INSTANCE;
        final Nearness near = new Nearness(u.with(u.muscles(muscles), u.bones(bind, new Detail(0.6))));
        return p -> {
            final Nearness.Near n = near.nearest(p, REACH, t -> true);
            if (n.triangle() < 0) {
                return Optional.empty();
            }
            final Beneath b = near.beneath();
            final int[] t = b.triangles();
            final V3 a = b.point(t[3 * n.triangle()]);
            final V3 c1 = b.point(t[3 * n.triangle() + 1]);
            final V3 c2 = b.point(t[3 * n.triangle() + 2]);
            final V3 at = a.times(n.a()).plus(c1.times(n.b())).plus(c2.times(n.c()));
            return Optional.of(new Bed.Laid(at, c1.minus(a).cross(c2.minus(a)).unit()));
        };
    }
}
