package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The legs' joints: the hip, the knee, the fibula's two to the tibia, the ankle. */
public record LegJoints() implements StatelessFunctionalObject {

    public static final LegJoints INSTANCE = new LegJoints();

    public List<Complex> joints() {
        return List.of(Hip.INSTANCE, Knee.INSTANCE, ProximalTibiofibular.INSTANCE, DistalTibiofibular.INSTANCE,
                Ankle.INSTANCE);
    }
}
