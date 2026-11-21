package com.example.fxsuite.road;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * One attribute of a road's profile ({@link RoadProfile}): the width of a strip across the road, its crossfall, its
 * grade. Each attribute is a type of its own and comes in a natural order, so that two values of it are always one
 * lower and one higher, or equal; it compares with its own kind and no other. Values are said along the road's own
 * stations, from its start to its end, whichever way it is walked or driven.
 *
 * @param <A> the attribute itself
 */
public interface RoadProfileAttribute<A extends RoadProfileAttribute<A>> extends Scalar, Comparable<A> {
}
