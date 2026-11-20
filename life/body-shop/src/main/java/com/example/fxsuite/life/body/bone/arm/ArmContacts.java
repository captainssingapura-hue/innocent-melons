package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.HandContacts;
import com.example.fxsuite.life.body.bone.spine.Discs;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the shoulder's and the arm's bones meet, as on the right, each in the frame of the part the bone is drawn in:
 * the clavicle on the sternum and on the acromion, turning along its length; the humerus's head in the glenoid; at the
 * elbow, the trochlea in the ulna's notch and the capitulum on the radius's head, both on the one axis the elbow bends
 * about; the radius turning across the ulna about the line from its head to the ulna's, the forearm's twist, at its
 * head in the ulna's notch and at the ulna's head in its own; the radius at the wrist.
 *
 * <p>The elbow's axis is made square to the twist's, so that a forearm bent and twisted is one turn of each, about the
 * capitulum's middle, which lies on both.</p>
 */
public record ArmContacts() implements StatelessFunctionalObject {

    public static final ArmContacts INSTANCE = new ArmContacts();

    /** In the body at rest: where the clavicle meets the sternum, and where it meets the acromion. */
    public static final V3 STERNAL = new V3(2.6, 45.3, 6.8);
    public static final V3 ACROMIAL = new V3(16.3, 47.5, 1.3);
    /** In the upper arm's frame: the middles of the capitulum and of the trochlea; where the forearm hangs. */
    public static final V3 CAPITULUM = new V3(1.35, -31.0, 0.7);
    public static final V3 TROCHLEA = new V3(-0.85, -31.0, 0.35);
    public static final V3 ELBOW = new V3(0, -31.0, 0);
    /** In the forearm's frame: the middle of the ulna's head; where the hand hangs. */
    public static final V3 ULNA_HEAD = new V3(-1.9, -24.2, -0.45);
    public static final V3 WRIST = new V3(0, -25.0, 0);
    /** How far down the twist's axis from the capitulum the radius's head turns in the ulna's notch. */
    private static final double NOTCH = 1.4;

    /** The clavicle on the sternum: along it, out, raised up. */
    public Place sternoclavicular(final Part on) {
        return Discs.INSTANCE.inPart(new Place(STERNAL, clavicle()), on);
    }

    /** The clavicle on the acromion, along it the same. */
    public Place acromioclavicular() {
        return Discs.INSTANCE.inPart(new Place(ACROMIAL, clavicle()), Part.RIGHT_GIRDLE);
    }

    /** The shoulder joint, on the scapula: down the arm, raised forward. */
    public Place glenoid() {
        return Discs.INSTANCE.inPart(new Place(Scapula.JOINT, Axes.HANGING), Part.RIGHT_GIRDLE);
    }

    /** The same, on the humerus: the middle of its head. */
    public Place humeralHead() {
        return new Place(V3.ZERO, Axes.HANGING);
    }

    /**
     * The trochlea in the ulna's notch, on the elbow's axis: down the forearm, bending forward; in the upper arm's
     * frame if {@code upper}, else the forearm's.
     */
    public Place trochlea(final boolean upper) {
        final V3 e = elbowAxis();
        final V3 at = CAPITULUM.plus(e.times(TROCHLEA.minus(CAPITULUM).dot(e)));
        return new Place(upper ? at : at.minus(ELBOW), Axes.INSTANCE.along(e.times(-1), new V3(0, -1, 0)));
    }

    /** The capitulum on the radius's head, on both axes: in the upper arm's frame or the forearm's. */
    public Place capitulum(final boolean upper) {
        return new Place(upper ? CAPITULUM : CAPITULUM.minus(ELBOW), forearm());
    }

    /** The radius's head in the ulna's notch, on the twist's axis, in the forearm's frame. */
    public Place radialNotch() {
        return new Place(CAPITULUM.minus(ELBOW).minus(twistAxis().times(NOTCH)), forearm());
    }

    /** The ulna's head in the radius's notch, on the twist's axis, in the forearm's frame. */
    public Place ulnarNotch() {
        return new Place(ULNA_HEAD, forearm());
    }

    /** The radius at the wrist, where the hand turns on it, in the forearm's frame. */
    public Place wrist() {
        final Place w = HandContacts.INSTANCE.wrist();
        return new Place(w.at().plus(WRIST), w.turn());
    }

    /** The clavicle's frame: along it, out from the sternum, raised up. */
    private Rot clavicle() {
        return Axes.INSTANCE.flexing(ACROMIAL.minus(STERNAL), V3.Y);
    }

    /**
     * The forearm's frame at the elbow: x along the elbow's axis, in, as a hanging arm's; y up the twist's axis, a turn
     * about it pronating the forearm; z behind.
     */
    private Rot forearm() {
        return Axes.INSTANCE.along(elbowAxis().times(-1), twistAxis());
    }

    /** The twist's axis, up from the ulna's head to the capitulum's middle, in the forearm's frame. */
    private V3 twistAxis() {
        return CAPITULUM.minus(ELBOW).minus(ULNA_HEAD).unit();
    }

    /** The elbow's axis, out from the trochlea to the capitulum, made square to the twist's. */
    private V3 elbowAxis() {
        final V3 p = twistAxis();
        final V3 e = CAPITULUM.minus(TROCHLEA);
        return e.minus(p.times(e.dot(p))).unit();
    }
}
