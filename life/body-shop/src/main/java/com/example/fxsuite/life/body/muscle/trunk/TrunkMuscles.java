package com.example.fxsuite.life.body.muscle.trunk;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The trunk's muscles: the chest's, the pectorals and the serratus; the belly's, the rectus and the obliques; the
 * back's, the trapezius's three parts, the latissimus, the rhomboids, the levator scapulae and the erector spinae.
 */
public record TrunkMuscles() implements StatelessFunctionalObject {

    public static final TrunkMuscles INSTANCE = new TrunkMuscles();

    public List<Muscle> muscles() {
        return List.of(PectoralisMajorClavicular.INSTANCE, PectoralisMajorSternal.INSTANCE, PectoralisMinor.INSTANCE,
                SerratusAnterior.INSTANCE, RectusAbdominis.INSTANCE, ExternalOblique.INSTANCE,
                InternalOblique.INSTANCE, TrapeziusUpper.INSTANCE, TrapeziusMiddle.INSTANCE, TrapeziusLower.INSTANCE,
                LatissimusDorsi.INSTANCE, Rhomboids.INSTANCE, LevatorScapulae.INSTANCE, ErectorSpinae.INSTANCE);
    }
}
