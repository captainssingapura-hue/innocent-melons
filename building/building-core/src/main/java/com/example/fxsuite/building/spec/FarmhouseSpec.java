package com.example.fxsuite.building.spec;

import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A farmhouse, as anyone builds one: a frame of so many bays, so deep, roofed, and its yard before it so deep. What it
 * is walled with and how its yard is closed are its builders' own. Whose and when are left open.
 *
 * @param bays    how many bays wide it is along its front
 * @param bay     how wide a bay is, column to column, cm
 * @param depth   how deep it is, front to back, cm
 * @param roofing what its roof is covered in
 * @param yard    how deep its yard is before its front, cm
 * @param <C>     whose
 * @param <E>     when
 */
public record FarmhouseSpec<C extends Civilization, E extends Era<? super C>>(int bays, double bay, double depth,
                                                                              Roofing roofing, double yard)
        implements Framed<Buildings.Farmhouse, C, E> {

    public FarmhouseSpec {
        Frames.INSTANCE.check(bays, bay, depth, roofing);
        if (!(yard >= 300)) {
            throw new IllegalArgumentException("a yard 3 m deep or more");
        }
    }
}
