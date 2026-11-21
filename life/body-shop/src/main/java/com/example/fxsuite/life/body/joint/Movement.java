package com.example.fxsuite.life.body.joint;

/**
 * A way a joint turns, in the joint's frame, whose y runs along the bone it carries and whose z is the way that bone
 * flexes: flexing, about x, y toward z (a spine forward, a thigh forward, a shin back, a foot's toes down, a toe curled
 * under; less than nothing the other way); bending to the side, about z (for a limb, out from the body); twisting,
 * about y.
 */
public enum Movement {
    FLEXION("flex"),
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
