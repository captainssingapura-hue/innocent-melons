package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A stretch of quay along a river's bank, as anyone builds one: so long; its face so far out in front of the line it is
 * set along, standing on the river's bed so far under its top; so many flights of steps down its face to landings so
 * far under its top, a little over the water. What it is built of is its builders' own. Whose and when are left open.
 *
 * @param length  how long the stretch is, cm
 * @param out     how far out in front of its line its face stands, cm
 * @param bed     how far under its top the river's bed is, cm
 * @param landing how far under its top its steps' landings are, cm
 * @param flights how many flights of steps it has down its face
 * @param <C>     whose
 * @param <E>     when
 */
public record QuaySpec<C extends Civilization, E extends Era<? super C>>(double length, double out, double bed,
                                                                        double landing, int flights)
        implements Spec<Buildings.Quay, C, E> {

    public QuaySpec {
        if (!(length >= 500) || !(out >= 100) || !(landing > 0) || !(bed > landing) || flights < 0) {
            throw new IllegalArgumentException("a quay 5 m long or more, its face 1 m out or more, its landings over"
                    + " the river's bed");
        }
    }
}
