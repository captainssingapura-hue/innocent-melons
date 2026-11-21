package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the skull's bones meet, as on the right, in the head's frame (x to its right, y up from the joint it nods on, z
 * ahead): along each suture, midway between the middles of the two bones it joins, or down the middle between two of a
 * pair; where it nods on the atlas; the jaw turning in its hollows by the ears, both on one axis; the teeth in their
 * sockets; the middle ear's bones one on the next, the stirrup in the inner ear's window.
 */
public record SkullContacts() implements StatelessFunctionalObject {

    public static final SkullContacts INSTANCE = new SkullContacts();

    /** The bones' middles, the right one of a pair. */
    public static final V3 FRONTAL = new V3(0, 11.5, 6.5);
    public static final V3 PARIETAL = new V3(4.5, 12.5, -2.0);
    public static final V3 OCCIPITAL = new V3(0, 5.5, -6.5);
    public static final V3 TEMPORAL = new V3(5.8, 3.5, -0.5);
    public static final V3 SPHENOID = new V3(0, 3.5, 2.5);
    public static final V3 ETHMOID = new V3(0, 4.5, 6.5);
    public static final V3 NASAL = new V3(0.4, 5.4, 9.85);
    public static final V3 LACRIMAL = new V3(1.6, 4.5, 8.4);
    public static final V3 ZYGOMATIC = new V3(4.8, 3.2, 6.9);
    public static final V3 MAXILLA = new V3(2.2, 1.5, 7.5);
    public static final V3 PALATINE = new V3(1.0, 0.3, 4.0);
    public static final V3 CONCHA = new V3(0.8, 1.8, 7.0);
    public static final V3 VOMER = new V3(0, 2.0, 5.5);
    public static final V3 MANDIBLE = new V3(3.0, -4.35, 5.6);
    /** The middles of the right half of each row of teeth. */
    public static final V3 UPPER_TEETH = new V3(2.6, 0.15, 6.3);
    public static final V3 LOWER_TEETH = new V3(2.45, -2.75, 6.1);
    /** The middle ear's bones, and the inner ear's window the stirrup's footplate sits in. */
    public static final V3 MALLEUS = new V3(5.0, 1.6, -0.25);
    public static final V3 INCUS = new V3(4.85, 1.9, -0.62);
    public static final V3 STAPES = new V3(4.6, 1.36, -0.52);
    public static final V3 OVAL_WINDOW = new V3(4.5, 1.36, -0.52);
    /** The jaw's condyle in its hollow. */
    public static final V3 CONDYLE = new V3(5.15, 1.05, -0.15);

    /** Where two bones meet, midway between their middles {@code a} and {@code b}. */
    public Place between(final V3 a, final V3 b) {
        return new Place(a.plus(b).times(0.5), Axes.UPRIGHT);
    }

    /** Where a bone of a pair meets its fellow, its middle {@code a}: down the middle, beside it. */
    public Place middle(final V3 a) {
        return new Place(new V3(0, a.y(), a.z()), Axes.UPRIGHT);
    }

    /** Where the head nods on the atlas: the head's own frame. */
    public Place atlas() {
        return Place.HERE;
    }

    /** The jaw in its hollow: upright, so that its turn about x, both sides alike, opens it. */
    public Place jaw() {
        return new Place(CONDYLE, Axes.UPRIGHT);
    }
}
