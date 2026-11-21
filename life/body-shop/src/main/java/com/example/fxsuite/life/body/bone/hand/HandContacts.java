package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the hand's bones meet, as on the right, in the hand's frame (x toward the thumb, y up the arm, z out of the
 * palm): the wrist, turning about the capitate's head, on the radius under the scaphoid and the lunate; the carpals
 * each where it meets its neighbour, midway between their middles; each metacarpal on its carpal; their bases side by
 * side; each finger's joints, flexing toward the palm.
 */
public record HandContacts() implements StatelessFunctionalObject {

    public static final HandContacts INSTANCE = new HandContacts();

    /** Where the hand turns on the forearm: the capitate's head. */
    public static final V3 WRIST = new V3(-0.05, -1.85, -0.05);
    /** The carpals' middles. */
    public static final V3 SCAPHOID = new V3(1.65, -1.15, 0.25);
    public static final V3 LUNATE = new V3(0.15, -0.95, 0.1);
    public static final V3 TRIQUETRUM = new V3(-1.25, -1.35, -0.1);
    public static final V3 PISIFORM = new V3(-1.55, -1.55, 0.85);
    public static final V3 TRAPEZIUM = new V3(2.2, -2.65, 0.6);
    public static final V3 TRAPEZOID = new V3(1.0, -2.85, 0.05);
    public static final V3 CAPITATE = new V3(-0.1, -2.75, -0.05);
    public static final V3 HAMATE = new V3(-1.45, -2.65, 0.0);

    /** The wrist: down the hand, flexing toward the palm. */
    public Place wrist() {
        return new Place(WRIST, Axes.HANGING);
    }

    /** Where two bones meet, midway between their middles {@code a} and {@code b}. */
    public Place between(final V3 a, final V3 b) {
        return new Place(a.plus(b).times(0.5), Axes.UPRIGHT);
    }

    /** {@code d}'s metacarpal's base on its carpal: along it, flexing toward the palm. */
    public Place carpometacarpal(final Digit d) {
        final V3 carpal = switch (d) {
            case THUMB -> TRAPEZIUM;
            case INDEX -> TRAPEZOID;
            case MIDDLE -> CAPITATE;
            case RING, LITTLE -> HAMATE;
        };
        return new Place(carpal.plus(d.base()).times(0.5), Axes.INSTANCE.flexing(d.head().minus(d.base()), V3.Z));
    }

    /** {@code d}'s metacarpal's base beside the next one's, toward the little finger. */
    public Place intermetacarpal(final Digit d) {
        return between(d.base(), Digit.values()[d.ordinal() + 1].base());
    }

    /** {@code d}'s first phalanx on its metacarpal: the middle of its head. */
    public Place metacarpophalangeal(final Digit d) {
        return new Place(d.head(), Axes.INSTANCE.flexing(Fingers.INSTANCE.way(d, 0)[1], V3.Z));
    }

    /** The joint before {@code d}'s phalanx {@code q}: midway between it and the one before. */
    public Place interphalangeal(final Digit d, final int q) {
        return new Place(Fingers.INSTANCE.interphalangeal(d, q),
                Axes.INSTANCE.flexing(Fingers.INSTANCE.way(d, q)[1], V3.Z));
    }
}
