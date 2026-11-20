package com.example.fxsuite.life.body.joint;

/**
 * A way a joint turns, in the joint's frame (x to the right, y along the bone it carries, z ahead): bending forward
 * (back, less than nothing), about x; bending to the side, about z; twisting, about y.
 */
public enum Movement {
    FLEXION("bend forward"),
    SIDE_BEND("bend sideways"),
    TWIST("twist");

    private final String said;

    Movement(final String said) {
        this.said = said;
    }

    public String said() {
        return said;
    }
}
