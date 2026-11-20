package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A shophouse, as anyone builds one: a shop open to the street along its front, so many bays wide and so deep, under a
 * roof. Whose and when are left open: each civilization's implementation builds it its own way from the same figures.
 *
 * @param bays    how many bays wide it is along the street
 * @param bay     how wide a bay is, column to column, cm
 * @param depth   how deep it is, front to back, cm
 * @param roofing what its roof is covered in
 * @param <C>     whose
 * @param <E>     when
 */
public record ShopHouseSpec<C extends Civilization, E extends Era<? super C>>(int bays, double bay, double depth,
                                                                              Roofing roofing)
        implements Spec<Buildings.ShopHouse, C, E> {

    public ShopHouseSpec {
        if (bays < 1 || !(bay >= 200) || !(depth >= 300) || roofing == null) {
            throw new IllegalArgumentException("a shophouse of a bay or more, each 2 m or more, 3 m deep or more,"
                    + " roofed");
        }
    }

    /** How wide it is along the street, column to column. */
    public double width() {
        return bays * bay;
    }
}
