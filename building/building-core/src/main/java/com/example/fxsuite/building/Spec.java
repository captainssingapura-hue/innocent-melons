package com.example.fxsuite.building;

import hue.captains.singapura.tao.ontology.Scalar;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * What a thing is to be built from: its parameters, typed to the thing it is, whose it is and when. Pure data, a
 * record. It may be fixed to one thing, civilization and era (a woven arch bridge of the Northern Song); or leave any
 * of them open, to be built from by more than one implementation (a shophouse is so many bays, whoever builds it); or
 * extend another, an implementation of the wider taking the narrower.
 *
 * @param <T> the thing: a leaf of what exists, the only nodes that are built
 * @param <C> whose
 * @param <E> when: an era of its civilization's lineage, or everyone's
 */
public interface Spec<T extends Leaf<?>, C extends Civilization, E extends Era<? super C>> extends Scalar {
}
