package com.example.fxsuite.life.body.bone.leg;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the leg's bones meet, as on the right, each in the frame of the part the bone is drawn in: the hip, the middle
 * of the femur's head; the knee, on the axis through the femur's condyles, where the tibia's plateau turns under them;
 * the kneecap, riding on the same axis; the fibula bound to the tibia below the knee and above the ankle; the ankle, on
 * the axis through the malleoli, the talus turning in the mortise of the tibia and the fibula.
 */
public record LegContacts() implements StatelessFunctionalObject {

    public static final LegContacts INSTANCE = new LegContacts();

    /** How far down the thigh the knee is, and down the shin the ankle. */
    public static final double KNEE = 42;
    public static final double ANKLE = 42;
    /** How far out from the ankle's middle the lateral malleolus meets the talus. */
    public static final double MALLEOLUS = 2.6;

    /** The hip, on the femur: the middle of its head. */
    public Place hip() {
        return new Place(V3.ZERO, Axes.THIGH);
    }

    /** The knee, on the femur. */
    public Place knee() {
        return new Place(new V3(0, -KNEE, 0), Axes.SHIN);
    }

    /** The knee, on the tibia. */
    public Place kneeOnTibia() {
        return new Place(V3.ZERO, Axes.SHIN);
    }

    /** The kneecap's axis, on the femur and on the kneecap, both drawn in the thigh's frame. */
    public Place patellar() {
        return new Place(new V3(0, -KNEE, 0), Axes.UPRIGHT);
    }

    /** Where the fibula's head is bound to the tibia, below the knee. */
    public Place tibiofibularUpper() {
        return new Place(new V3(2.7, -3.9, -1.2), Axes.UPRIGHT);
    }

    /** Where the fibula is bound to the tibia, above the ankle. */
    public Place tibiofibularLower() {
        return new Place(new V3(2.3, -39.0, -0.3), Axes.UPRIGHT);
    }

    /** The ankle, on the tibia. */
    public Place ankle() {
        return new Place(new V3(0, -ANKLE, 0), Axes.FORWARD);
    }

    /** The ankle, on the talus. */
    public Place ankleOnTalus() {
        return new Place(V3.ZERO, Axes.FORWARD);
    }

    /** Where the lateral malleolus meets the talus, on the ankle's axis, on the fibula. */
    public Place malleolus() {
        return new Place(new V3(MALLEOLUS, -ANKLE, 0), Axes.FORWARD);
    }

    /** The same, on the talus. */
    public Place malleolusOnTalus() {
        return new Place(new V3(MALLEOLUS, 0, 0), Axes.FORWARD);
    }
}
