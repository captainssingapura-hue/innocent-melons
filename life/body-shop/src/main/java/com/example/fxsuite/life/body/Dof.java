package com.example.fxsuite.life.body;

/**
 * One way a joint turns, degrees, and how far it may: the joint ranges of the body model handover (Papers/Body), as
 * the interactive model's sliders have them; the spine's and the neck's shared between their two joints. A setting per
 * figure in time; for now the same for every one.
 */
public enum Dof {

    TILT("Pelvis tilt", -10, 45, 0, false),
    SPINE_BEND("Bend forward", -12.5, 30, 0, false),
    SPINE_SIDE("Bend sideways", -15, 15, 0, false),
    SPINE_TWIST("Twist", -22.5, 22.5, 0, true),
    NECK_BEND("Nod forward", -30, 25, 0, false),
    NECK_SIDE("Tilt sideways", -22.5, 22.5, 0, false),
    NECK_TURN("Turn", -40, 40, 0, true),
    SHRUG("Shrug", -10, 30, 0, false),
    PROTRACT("Shoulder forward", -20, 20, 0, false),
    RAISE_FORWARD("Raise forward", -40, 180, 0, false),
    RAISE_SIDEWAYS("Raise sideways", -10, 180, 8, false),
    ARM_ROTATION("Arm rotation", -70, 90, 0, true),
    ELBOW("Elbow bend", 0, 145, 10, false),
    FOREARM_TWIST("Forearm twist", 0, 170, 85, true),
    WRIST_BEND("Wrist toward the palm", -70, 80, 0, false),
    WRIST_SIDE("Wrist sideways", -20, 30, 0, false),
    HIP_FORWARD("Hip forward", -30, 125, 0, false),
    HIP_SIDEWAYS("Hip sideways", -20, 45, 0, false),
    HIP_ROTATION("Hip rotation", -45, 45, 0, true),
    KNEE("Knee bend", 0, 140, 0, false),
    ANKLE("Ankle (toes down)", -45, 50, 0, false),
    ANKLE_ROLL("Ankle roll", -15, 30, 0, true),
    TOES("Toes up", -30, 60, 0, false);

    private final String label;
    private final double min;
    private final double max;
    private final double rest;
    private final boolean twist;

    Dof(final String label, final double min, final double max, final double rest, final boolean twist) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.rest = rest;
        this.twist = twist;
    }

    public String label() {
        return label;
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    /** Where it is standing at rest. */
    public double rest() {
        return rest;
    }

    /** Whether it turns a part about its own length, rather than swinging it: dragged with the control key held. */
    public boolean twist() {
        return twist;
    }

    /** {@code v}, no further than it may go. */
    public double clamp(final double v) {
        return Math.clamp(v, min, max);
    }
}
