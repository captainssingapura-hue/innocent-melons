package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The shoulder's and the arm's joints: the clavicle at either end, the shoulder, the elbow and the forearm. */
public record ArmJoints() implements StatelessFunctionalObject {

    public static final ArmJoints INSTANCE = new ArmJoints();

    public List<Complex> joints() {
        return List.of(Sternoclavicular.INSTANCE, Acromioclavicular.INSTANCE, Glenohumeral.INSTANCE, Elbow.INSTANCE);
    }
}
