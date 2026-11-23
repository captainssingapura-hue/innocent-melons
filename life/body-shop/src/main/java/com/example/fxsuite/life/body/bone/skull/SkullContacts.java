package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the head's bones meet, as on the right, in the head's frame (x to its right, y up from the joint it nods on, z
 * ahead): where the skull nods on the atlas; the jaw turning in its hollows by the ears, both on one axis; the teeth in
 * their sockets, midway between the middles of the jaw and its teeth; the middle ear's bones one on the next, the
 * stirrup in the inner ear's window.
 */
public record SkullContacts() implements StatelessFunctionalObject {

    public static final SkullContacts INSTANCE = new SkullContacts();

    /** The upper jaw's middle on the right, and the lower jaw's. */
    public static final V3 MAXILLA = new V3(2.2, 1.5, 7.5);
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

    /** Where two bones meet, midway between their middles {@code a} and {@code b}, as the skull is bent. */
    public Place between(final V3 a, final V3 b) {
        return new Place(SkullForm.INSTANCE.apply(a.plus(b).times(0.5)), Axes.UPRIGHT);
    }

    /** Where the head nods on the atlas: the head's own frame. */
    public Place atlas() {
        return Place.HERE;
    }

    /** The jaw in its hollow: upright, so that its turn about x, both sides alike, opens it. */
    public Place jaw() {
        return new Place(SkullForm.INSTANCE.apply(CONDYLE), Axes.UPRIGHT);
    }
}
