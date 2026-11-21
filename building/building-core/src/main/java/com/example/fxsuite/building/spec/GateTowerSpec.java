package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A gate through a city's wall and the tower over it, as anyone builds one: a base so wide along the wall, so deep
 * through it and so high, a passage through it so wide, a tower of so many bays on it, a way up to its top inside the
 * wall on one side of it. Whose and when are left open.
 *
 * @param wide    how wide its base is along the wall, cm
 * @param deep    how deep its base is at its foot, through the wall, cm: its passage's length
 * @param high    how high its base is, the tower's floor, cm
 * @param passage how wide its passage is, cm
 * @param bays    how many bays wide its tower is
 * @param ascent  which side of it its way up is
 * @param <C>     whose
 * @param <E>     when
 */
public record GateTowerSpec<C extends Civilization, E extends Era<? super C>>(double wide, double deep, double high,
                                                                              double passage, int bays,
                                                                              Ascent ascent)
        implements Spec<Buildings.GateTower, C, E> {

    /** Which side of a gate its way up is, inside the wall: to the left or the right of one going out through it. */
    public enum Ascent { LEFT, RIGHT }

    public GateTowerSpec {
        if (ascent == null || !(passage >= 200) || !(wide >= passage + 400) || !(deep >= 300) || !(high >= 400)
                || bays < 1) {
            throw new IllegalArgumentException("a gate's base wider than its passage by 4 m or more, 3 m deep or more,"
                    + " 4 m high or more, a tower of a bay or more on it, a way up on one side");
        }
    }
}
