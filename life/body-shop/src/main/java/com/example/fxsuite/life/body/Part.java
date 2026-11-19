package com.example.fxsuite.life.body;

import java.util.List;

/**
 * A rigid part of the body, hung from its parent at its joint: the parts of the body model handover (Papers/Body), its
 * mannequin's and its separate abdomen and shoulder girdles, and the toes the interactive model gives a foot. In the
 * order the body is worked out in, each after its parent; the pelvis its root.
 *
 * <p>Each says where its joint is on its parent, standing at rest, in its parent's frame, in the papers' figure's
 * centimetres (166.5 cm tall); how it turns there, by its own ways ({@link Dof}); its share of the body's mass and where
 * that sits. A left part is its right's in a mirror ({@link #side}).
 */
public enum Part {

    PELVIS(null, 0, new V3(0, 0, 0), 14.2, new V3(0, 3, 0), List.of(Dof.TILT)),
    ABDOMEN(PELVIS, 0, new V3(0, 12, 0), 13.9, new V3(0, 7, 0), spine()),
    CHEST(ABDOMEN, 0, new V3(0, 14, 0), 21.6, new V3(0, 12, 0), spine()),
    NECK(CHEST, 0, new V3(0, 26, -0.5), 1.2, new V3(0, 4.5, 0), neck()),
    HEAD(NECK, 0, new V3(0, 9, 0.5), 6.9, new V3(0, 6, 1), neck()),
    RIGHT_GIRDLE(CHEST, 1, new V3(2, 19, 6.5), 0, V3.ZERO, girdle()),
    RIGHT_UPPER_ARM(RIGHT_GIRDLE, 1, new V3(16, -0.5, -6.5), 2.8, new V3(0, -13, 0), arm()),
    RIGHT_FOREARM(RIGHT_UPPER_ARM, 1, new V3(0, -31, 0), 1.6, new V3(0, -10.4, 0), forearm()),
    RIGHT_HAND(RIGHT_FOREARM, 1, new V3(0, -25, 0), 0.6, new V3(0, -7, 0), wrist()),
    LEFT_GIRDLE(CHEST, -1, new V3(2, 19, 6.5), 0, V3.ZERO, girdle()),
    LEFT_UPPER_ARM(LEFT_GIRDLE, -1, new V3(16, -0.5, -6.5), 2.8, new V3(0, -13, 0), arm()),
    LEFT_FOREARM(LEFT_UPPER_ARM, -1, new V3(0, -31, 0), 1.6, new V3(0, -10.4, 0), forearm()),
    LEFT_HAND(LEFT_FOREARM, -1, new V3(0, -25, 0), 0.6, new V3(0, -7, 0), wrist()),
    RIGHT_THIGH(PELVIS, 1, new V3(8.5, 0, 0), 10, new V3(1.2, -17.3, 0), hip()),
    RIGHT_SHIN(RIGHT_THIGH, 1, new V3(0, -42, 0), 4.65, new V3(0, -16.8, -0.4), List.of(Dof.KNEE)),
    RIGHT_FOOT(RIGHT_SHIN, 1, new V3(0, -42, 0), 1.45, new V3(0, -3.8, 4.5), ankle()),
    RIGHT_TOES(RIGHT_FOOT, 1, new V3(0, -5.4, 13.1), 0, V3.ZERO, List.of(Dof.TOES)),
    LEFT_THIGH(PELVIS, -1, new V3(8.5, 0, 0), 10, new V3(1.2, -17.3, 0), hip()),
    LEFT_SHIN(LEFT_THIGH, -1, new V3(0, -42, 0), 4.65, new V3(0, -16.8, -0.4), List.of(Dof.KNEE)),
    LEFT_FOOT(LEFT_SHIN, -1, new V3(0, -42, 0), 1.45, new V3(0, -3.8, 4.5), ankle()),
    LEFT_TOES(LEFT_FOOT, -1, new V3(0, -5.4, 13.1), 0, V3.ZERO, List.of(Dof.TOES));

    private static List<Dof> spine() {
        return List.of(Dof.SPINE_BEND, Dof.SPINE_SIDE, Dof.SPINE_TWIST);
    }

    private static List<Dof> neck() {
        return List.of(Dof.NECK_BEND, Dof.NECK_SIDE, Dof.NECK_TURN);
    }

    private static List<Dof> girdle() {
        return List.of(Dof.SHRUG, Dof.PROTRACT);
    }

    private static List<Dof> arm() {
        return List.of(Dof.RAISE_FORWARD, Dof.RAISE_SIDEWAYS, Dof.ARM_ROTATION);
    }

    private static List<Dof> forearm() {
        return List.of(Dof.ELBOW, Dof.FOREARM_TWIST);
    }

    private static List<Dof> wrist() {
        return List.of(Dof.WRIST_BEND, Dof.WRIST_SIDE);
    }

    private static List<Dof> hip() {
        return List.of(Dof.HIP_FORWARD, Dof.HIP_SIDEWAYS, Dof.HIP_ROTATION);
    }

    private static List<Dof> ankle() {
        return List.of(Dof.ANKLE, Dof.ANKLE_ROLL);
    }

    private final Part parent;
    private final int side;
    private final V3 joint;
    private final double mass;
    private final V3 centre;
    private final List<Dof> dofs;

    Part(final Part parent, final int side, final V3 joint, final double mass, final V3 centre, final List<Dof> dofs) {
        this.parent = parent;
        this.side = side;
        this.joint = joint;
        this.mass = mass;
        this.centre = centre;
        this.dofs = dofs;
    }

    /** What it hangs from; none for the pelvis. */
    public Part parent() {
        return parent;
    }

    /** Which side: 1 its right, -1 its left (its right's in a mirror), 0 down its middle. */
    public int side() {
        return side;
    }

    /** Where its joint is on its parent at rest, in its parent's frame, as on the right side; papers' cm. */
    V3 joint() {
        return joint;
    }

    /** Its share of the body's mass, per cent (the papers' Dempster figures). */
    public double mass() {
        return mass;
    }

    /** Where its mass sits, in its own frame, as on the right side. */
    V3 centre() {
        return centre;
    }

    /** The ways it turns at its joint, in order. */
    public List<Dof> dofs() {
        return dofs;
    }

    /** The same part on the other side; itself, if down the middle. */
    public Part other() {
        if (side == 0) {
            return this;
        }
        return valueOf(side > 0 ? name().replace("RIGHT_", "LEFT_") : name().replace("LEFT_", "RIGHT_"));
    }

    /** What it is called, in words. */
    public String said() {
        return name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
    }
}
