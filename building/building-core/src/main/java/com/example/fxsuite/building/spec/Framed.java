package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * What anything built as a frame of bays under a roof is built from: so many bays along its front, each so wide, so
 * deep, roofed. A shophouse, a tea house, a warehouse and a wine house are each one; an implementation of a frame takes
 * any of them.
 *
 * @param <T> the thing
 * @param <C> whose
 * @param <E> when
 */
public interface Framed<T extends Leaf<?>, C extends Civilization, E extends Era<? super C>> extends Spec<T, C, E> {

    /** How many bays wide it is along its front. */
    int bays();

    /** How wide a bay is, column to column, cm. */
    double bay();

    /** How deep it is, front to back, cm. */
    double depth();

    /** What its roof is covered in. */
    Roofing roofing();

    /** How wide it is along its front, column to column. */
    default double width() {
        return bays() * bay();
    }
}
