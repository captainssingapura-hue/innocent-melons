package com.example.fxsuite.building.spec;

import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A wine house, as anyone builds one: a hall on the street and rooms over it, reached by a stair; so many bays wide
 * and so deep, under a roof. Whose and when are left open.
 *
 * @param bays    how many bays wide it is along the street
 * @param bay     how wide a bay is, column to column, cm
 * @param depth   how deep it is, front to back, cm
 * @param roofing what its roof is covered in
 * @param <C>     whose
 * @param <E>     when
 */
public record WineHouseSpec<C extends Civilization, E extends Era<? super C>>(int bays, double bay, double depth,
                                                                              Roofing roofing)
        implements Framed<Buildings.WineHouse, C, E> {

    public WineHouseSpec {
        Frames.INSTANCE.check(bays, bay, depth, roofing);
    }
}
