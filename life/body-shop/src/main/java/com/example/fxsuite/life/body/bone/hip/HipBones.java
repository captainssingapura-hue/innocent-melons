package com.example.fxsuite.life.body.bone.hip;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The pelvis's own bones: the hip bones, one a side. The sacrum and the coccyx are the spine's. */
public record HipBones() implements StatelessFunctionalObject {

    public static final HipBones INSTANCE = new HipBones();

    public List<Bone> bones() {
        return List.of(HipBone.INSTANCE);
    }
}
