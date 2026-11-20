package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The shoulder girdle's and the arm's bones: the clavicle and the scapula; the humerus, the ulna and the radius. */
public record ArmBones() implements StatelessFunctionalObject {

    public static final ArmBones INSTANCE = new ArmBones();

    public List<Bone> bones() {
        return List.of(Clavicle.INSTANCE, Scapula.INSTANCE, Humerus.INSTANCE, Ulna.INSTANCE, Radius.INSTANCE);
    }
}
