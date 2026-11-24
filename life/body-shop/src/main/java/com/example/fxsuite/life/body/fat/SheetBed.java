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
 * surface, the nearest of it found within {@link #REACH}; the way out of it there, its nearest triangle's; whether it
 * is a bone's.
 */
public record SheetBed() implements StatelessFunctionalObject {

    public static final SheetBed INSTANCE = new SheetBed();

    /** How far a sheet's bed is looked for, cm. */
    private static final double REACH = 6;
    /** How finely its bones are found, cm. */
    private static final double BONES = 0.6;

    /**
     * The muscles of a body made as {@code p} says, the deep ones if {@code deep}, as drawn ({@link MuscleState}), its
     * sheets laid on the rest.
     */
    public MuscleBody.Bound body(final Proportions p, final boolean deep, final int steps) {
        return body(p, deep, steps, new MuscleState.Drawn());
    }

    /** The same, its muscles but its sheets in the state {@code state}. */
    public MuscleBody.Bound body(final Proportions p, final boolean deep, final int steps, final MuscleState state) {
        final Rig bind = new Rig(p, BoundPose.INSTANCE.pose(), false);
        final Beneath bones = Underneath.INSTANCE.bones(bind, new Detail(BONES));
        return MuscleBody.INSTANCE.make(p, deep, steps, muscles -> of(muscles, bones),
                firm -> state.shaped(firm, bones));
    }

    /** The bed of {@code muscles} and the bones of the body {@code bind} holds. */
    public Bed of(final Flesh muscles, final Rig bind) {
        return of(muscles, Underneath.INSTANCE.bones(bind, new Detail(BONES)));
    }

    /** The bed of {@code muscles} and {@code bones}. */
    private Bed of(final Flesh muscles, final Beneath bones) {
        final Underneath u = Underneath.INSTANCE;
        final Beneath flesh = u.muscles(muscles);
        final Nearness near = new Nearness(u.with(flesh, bones));
        // the muscles' triangles first, the bones' after them
        final int firstBone = flesh.triangles().length / 3;
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
            return Optional.of(new Bed.Laid(at, c1.minus(a).cross(c2.minus(a)).unit(),
                    n.triangle() >= firstBone));
        };
    }
}
