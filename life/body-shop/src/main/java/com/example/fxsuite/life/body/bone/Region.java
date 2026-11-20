package com.example.fxsuite.life.body.bone;

/** Where in the body a bone is, in the order a skeleton is gone through. */
public enum Region {
    SKULL("Skull"),
    EAR("Middle ear"),
    THROAT("Throat"),
    SPINE("Spine"),
    THORAX("Chest"),
    SHOULDER("Shoulder girdle"),
    ARM("Arm"),
    HAND("Hand"),
    HIP("Pelvis"),
    LEG("Leg"),
    FOOT("Foot");

    private final String label;

    Region(final String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
