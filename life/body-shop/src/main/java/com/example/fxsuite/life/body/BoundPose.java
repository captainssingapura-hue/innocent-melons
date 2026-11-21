package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How a body is held to be bound, what it is made in and moves from: every part unturned, but the arms out 35 degrees
 * from the sides and the legs 5, so that neither runs into the trunk.
 */
public record BoundPose() implements StatelessFunctionalObject {

    public static final BoundPose INSTANCE = new BoundPose();

    public Pose pose() {
        return Pose.REST.unturned().with(Part.RIGHT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 35)
                .with(Part.LEFT_UPPER_ARM, Dof.RAISE_SIDEWAYS, 35).with(Part.RIGHT_THIGH, Dof.HIP_SIDEWAYS, 5)
                .with(Part.LEFT_THIGH, Dof.HIP_SIDEWAYS, 5);
    }
}
