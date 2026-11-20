package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.joint.Complex;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/** The spine's joints, from the atlas on the axis down the discs to the sacrum, and the coccyx on the sacrum. */
public record SpineJoints() implements StatelessFunctionalObject {

    public static final SpineJoints INSTANCE = new SpineJoints();

    public List<Complex> joints() {
        return List.of(AtlantoAxial.INSTANCE, IntervertebralC2C3.INSTANCE, IntervertebralC3C4.INSTANCE,
                IntervertebralC4C5.INSTANCE, IntervertebralC5C6.INSTANCE, IntervertebralC6C7.INSTANCE,
                IntervertebralC7T1.INSTANCE, IntervertebralT1T2.INSTANCE, IntervertebralT2T3.INSTANCE,
                IntervertebralT3T4.INSTANCE, IntervertebralT4T5.INSTANCE, IntervertebralT5T6.INSTANCE,
                IntervertebralT6T7.INSTANCE, IntervertebralT7T8.INSTANCE, IntervertebralT8T9.INSTANCE,
                IntervertebralT9T10.INSTANCE, IntervertebralT10T11.INSTANCE, IntervertebralT11T12.INSTANCE,
                IntervertebralT12L1.INSTANCE, IntervertebralL1L2.INSTANCE, IntervertebralL2L3.INSTANCE,
                IntervertebralL3L4.INSTANCE, IntervertebralL4L5.INSTANCE, IntervertebralL5S1.INSTANCE,
                Sacrococcygeal.INSTANCE);
    }
}
