package com.example.fxsuite.life.body.muscle;

/**
 * Where in the body a muscle is: the head, the neck; the chest, the belly, the back; the shoulder, the upper arm, the
 * forearm, the hand; the hip, the thigh, the lower leg, the foot.
 */
public enum MuscleRegion {
    HEAD("The head"),
    NECK("The neck"),
    CHEST("The chest"),
    ABDOMEN("The belly"),
    BACK("The back"),
    SHOULDER("The shoulder"),
    UPPER_ARM("The upper arm"),
    FOREARM("The forearm"),
    HAND("The hand"),
    HIP("The hip"),
    THIGH("The thigh"),
    LOWER_LEG("The lower leg"),
    FOOT("The foot");

    private final String label;

    MuscleRegion(final String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
