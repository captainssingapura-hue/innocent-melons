package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The head's bones and the throat's: the skull's twenty-two, the teeth, the middle ears' six, and the hyoid. */
public record SkullBones() implements StatelessFunctionalObject {

    public static final SkullBones INSTANCE = new SkullBones();

    public List<Bone> bones() {
        return List.of(Frontal.INSTANCE, Parietal.INSTANCE, Occipital.INSTANCE, Temporal.INSTANCE,
                Sphenoid.INSTANCE, Ethmoid.INSTANCE, Nasal.INSTANCE, Lacrimal.INSTANCE, Palatine.INSTANCE,
                Zygomatic.INSTANCE, Maxilla.INSTANCE, InferiorNasalConcha.INSTANCE, Vomer.INSTANCE,
                Mandible.INSTANCE, UpperTeeth.INSTANCE, LowerTeeth.INSTANCE, Malleus.INSTANCE, Incus.INSTANCE,
                Stapes.INSTANCE, Hyoid.INSTANCE);
    }
}
