package com.example.fxsuite.life.body.joint.pelvis;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The pelvis's joints: the sacroiliac joints, one a side, and the pubic symphysis. */
public record PelvisJoints() implements StatelessFunctionalObject {

    public static final PelvisJoints INSTANCE = new PelvisJoints();

    public List<Complex> joints() {
        return List.of(Sacroiliac.INSTANCE, PubicSymphysis.INSTANCE);
    }
}
