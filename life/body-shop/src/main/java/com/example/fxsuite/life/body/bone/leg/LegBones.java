package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.bone.Bone;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The leg's bones: the femur, the patella, the tibia and the fibula. */
public record LegBones() implements StatelessFunctionalObject {

    public static final LegBones INSTANCE = new LegBones();

    public List<Bone> bones() {
        return List.of(Femur.INSTANCE, Patella.INSTANCE, Tibia.INSTANCE, Fibula.INSTANCE);
    }
}
