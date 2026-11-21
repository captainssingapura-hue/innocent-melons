package com.example.fxsuite.road;

import hue.captains.singapura.tao.ontology.Vector;

/**
 * A transition of one attribute of a road's profile: a type of its own to each attribute and each way of changing it,
 * from a lower value to a higher, never between two equal values, which would change nothing. Its two values are an
 * ordered set of one attribute, in the attribute's natural order: a vector. Which end of a seam between two sections
 * holds the lower value is the seam's to say; a narrowing is a widening laid the other way.
 *
 * <p>The transition is a function along its length: its value at each fraction of the way from the lower value to the
 * higher, its shape its type's. Its length is worked out from the road's design, how fast it is driven and how wide.
 *
 * @param <A> the attribute it changes
 */
public interface RoadTransition<A extends RoadProfileAttribute<A>> extends Vector<A> {

    /** The lower value, where the transition starts. */
    A lower();

    /** The higher value, where it ends. */
    A higher();

    /** Its value at {@code along}, from 0 at its lower value to 1 at its higher. */
    A at(double along);

    /** How long it is, in metres, on a road of {@code design}. */
    double length(Design design);
}
