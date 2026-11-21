package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Underneath;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * What the fat lies on ({@link Beneath}): the body's muscles, the deep ones too, so that none of the grooves between
 * the superficial ones goes deeper than it should; its bones; and the bulk of the belly behind its wall
 * ({@link Viscera}).
 */
public record FatBeneath() implements StatelessFunctionalObject {

    public static final FatBeneath INSTANCE = new FatBeneath();

    /** What lies under the fat of a body whose muscles, the deep ones too, are {@code body}, held as {@code bind}. */
    public Beneath of(final MuscleBody.Bound body, final Rig bind) {
        final Beneath bones = Underneath.INSTANCE.bones(bind, new Detail(0.6));
        final Underneath u = Underneath.INSTANCE;
        return u.with(u.with(u.muscles(body.flesh()), bones), Viscera.INSTANCE.of(bones, bind));
    }
}
