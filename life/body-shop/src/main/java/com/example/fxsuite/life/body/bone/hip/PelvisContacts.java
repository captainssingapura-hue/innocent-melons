package com.example.fxsuite.life.body.bone.hip;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the pelvis's bones meet, as on the right, in the pelvis's frame (from the middle between the hip joints): the
 * sacroiliac joint, where the sacrum's wing meets the ilium; the pubic symphysis in front, down the middle, where the
 * two hip bones meet; the hip joint, the middle of the acetabulum and of the femur's head in it.
 */
public record PelvisContacts() implements StatelessFunctionalObject {

    public static final PelvisContacts INSTANCE = new PelvisContacts();

    /** Where the sacrum meets the ilium, upright: it nods there, a little, about its x. */
    public Place sacroiliac() {
        return new Place(new V3(4.9, 7.0, -5.9), Axes.UPRIGHT);
    }

    /** Where the two hip bones meet in front, down the middle. */
    public Place symphysis() {
        return new Place(new V3(0, -4.4, 6.0), Axes.UPRIGHT);
    }

    /** The hip joint, down the thigh. */
    public Place hip() {
        return new Place(HipBone.JOINT, Axes.THIGH);
    }
}
