package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The head's bones and the throat's: the skull, its twenty-one but the jaw knit into one; the jaw; the teeth; the
 * middle ears' six; and the hyoid.
 */
public record SkullBones() implements StatelessFunctionalObject {

    public static final SkullBones INSTANCE = new SkullBones();

    public List<Bone> bones() {
        return List.of(Skull.INSTANCE, Mandible.INSTANCE, UpperTeeth.INSTANCE, LowerTeeth.INSTANCE, Malleus.INSTANCE,
                Incus.INSTANCE, Stapes.INSTANCE, Hyoid.INSTANCE);
    }
}
