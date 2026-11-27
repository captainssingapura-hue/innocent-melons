package com.example.fxsuite.life.body.hand;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;
import com.example.fxsuite.life.body.bone.hand.Fingers;
import com.example.fxsuite.life.body.bone.hand.HandContacts;

import java.util.List;

/**
 * A piece of a hand that turns on its own, each on the one before it, the first of each finger on the palm: the thumb's
 * metacarpal, on its base by the wrist, and its two phalanges; each finger's three phalanges, the first on its
 * knuckle. Where each turns and how, as the hand hangs at rest, in the hand's frame as on the right side (x toward the
 * thumb, y up the arm, z out of the palm), the papers' centimetres, as its bones are drawn ({@link Fingers}).
 */
public enum Segment {
    THUMB_METACARPAL(Digit.THUMB, -1, null, List.of(FingerDof.THUMB_BEND, FingerDof.THUMB_SWING)),
    THUMB_PROXIMAL(Digit.THUMB, 0, THUMB_METACARPAL, List.of(FingerDof.THUMB_KNUCKLE)),
    THUMB_DISTAL(Digit.THUMB, 1, THUMB_PROXIMAL, List.of(FingerDof.THUMB_END)),
    INDEX_PROXIMAL(Digit.INDEX, 0, null, List.of(FingerDof.KNUCKLE_BEND, FingerDof.KNUCKLE_SPREAD)),
    INDEX_MIDDLE(Digit.INDEX, 1, INDEX_PROXIMAL, List.of(FingerDof.MIDDLE_BEND)),
    INDEX_DISTAL(Digit.INDEX, 2, INDEX_MIDDLE, List.of(FingerDof.END_BEND)),
    MIDDLE_PROXIMAL(Digit.MIDDLE, 0, null, List.of(FingerDof.KNUCKLE_BEND, FingerDof.KNUCKLE_SPREAD)),
    MIDDLE_MIDDLE(Digit.MIDDLE, 1, MIDDLE_PROXIMAL, List.of(FingerDof.MIDDLE_BEND)),
    MIDDLE_DISTAL(Digit.MIDDLE, 2, MIDDLE_MIDDLE, List.of(FingerDof.END_BEND)),
    RING_PROXIMAL(Digit.RING, 0, null, List.of(FingerDof.KNUCKLE_BEND, FingerDof.KNUCKLE_SPREAD)),
    RING_MIDDLE(Digit.RING, 1, RING_PROXIMAL, List.of(FingerDof.MIDDLE_BEND)),
    RING_DISTAL(Digit.RING, 2, RING_MIDDLE, List.of(FingerDof.END_BEND)),
    LITTLE_PROXIMAL(Digit.LITTLE, 0, null, List.of(FingerDof.KNUCKLE_BEND, FingerDof.KNUCKLE_SPREAD)),
    LITTLE_MIDDLE(Digit.LITTLE, 1, LITTLE_PROXIMAL, List.of(FingerDof.MIDDLE_BEND)),
    LITTLE_DISTAL(Digit.LITTLE, 2, LITTLE_MIDDLE, List.of(FingerDof.END_BEND));

    private final Digit digit;
    private final int phalanx;
    private final Segment parent;
    private final List<FingerDof> dofs;

    Segment(final Digit digit, final int phalanx, final Segment parent, final List<FingerDof> dofs) {
        this.digit = digit;
        this.phalanx = phalanx;
        this.parent = parent;
        this.dofs = dofs;
    }

    /** The finger it is of. */
    public Digit digit() {
        return digit;
    }

    /** Which of its finger's phalanges it is, from the knuckle; the thumb's metacarpal before them. */
    public int phalanx() {
        return phalanx;
    }

    /** What it turns on: the one before it, or none, the palm. */
    public Segment parent() {
        return parent;
    }

    /** The ways it turns on what it turns on. */
    public List<FingerDof> dofs() {
        return dofs;
    }

    /** Where it turns on what it turns on, as the hand hangs at rest. */
    public V3 joint() {
        if (phalanx < 0) {
            return HandContacts.TRAPEZIUM.plus(digit.base()).times(0.5);
        }
        return phalanx == 0 ? digit.head() : Fingers.INSTANCE.interphalangeal(digit, phalanx);
    }

    /** Where its far end is, as the hand hangs at rest: its finger's next joint, or its tip. */
    public V3 end() {
        if (phalanx < 0) {
            return digit.head();
        }
        final V3[] way = Fingers.INSTANCE.way(digit, phalanx);
        return way[0].plus(way[1].times(digit.length(phalanx)));
    }

    /** The way it runs from its joint, as the hand hangs at rest. */
    public V3 way() {
        return phalanx < 0 ? digit.head().minus(digit.base()).unit() : Fingers.INSTANCE.way(digit, phalanx)[1];
    }

    /** How far it is bent {@code dof}'s way as the hand hangs at rest: its finger's curl if it bends, else none. */
    public double rest(final FingerDof dof) {
        return dof.bends() && phalanx >= 0 ? digit.curl() : 0;
    }
}
