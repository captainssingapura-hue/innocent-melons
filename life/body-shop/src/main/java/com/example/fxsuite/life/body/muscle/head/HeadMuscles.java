package com.example.fxsuite.life.body.muscle.head;

import com.example.fxsuite.life.body.muscle.Muscle;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The neck's and the head's muscles: the sternocleidomastoid, the scalenes, the splenius, the straps under the jaw; the
 * jaw's, the masseter and the temporalis; the face's, the frontalis and the zygomaticus.
 */
public record HeadMuscles() implements StatelessFunctionalObject {

    public static final HeadMuscles INSTANCE = new HeadMuscles();

    public List<Muscle> muscles() {
        return List.of(Sternocleidomastoid.INSTANCE, Scalenes.INSTANCE, SpleniusCapitis.INSTANCE, Sternohyoid.INSTANCE,
                Mylohyoid.INSTANCE, Masseter.INSTANCE, Temporalis.INSTANCE, Frontalis.INSTANCE,
                ZygomaticusMajor.INSTANCE);
    }
}
