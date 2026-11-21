package com.example.fxsuite.building.spec;

import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * A hall of one storey, for whatever it is for: a tea house, a warehouse. The thing, whose and when are all left open:
 * each implementation says in its type which it builds from these figures.
 *
 * @param bays    how many bays wide it is along its front
 * @param bay     how wide a bay is, column to column, cm
 * @param depth   how deep it is, front to back, cm
 * @param roofing what its roof is covered in
 * @param <T>     the thing
 * @param <C>     whose
 * @param <E>     when
 */
public record HallSpec<T extends Leaf<?>, C extends Civilization, E extends Era<? super C>>(int bays, double bay,
                                                                                         double depth, Roofing roofing)
        implements Framed<T, C, E> {

    public HallSpec {
        Frames.INSTANCE.check(bays, bay, depth, roofing);
    }
}
