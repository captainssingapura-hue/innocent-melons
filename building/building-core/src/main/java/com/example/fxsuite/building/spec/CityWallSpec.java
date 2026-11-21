package com.example.fxsuite.building.spec;

import com.example.fxsuite.existence.Earthworks;
import com.example.fxsuite.building.Spec;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A stretch of a city's wall, as anyone builds one: so long and so high, walked along its top. How thick it is and
 * what it is made of are its builders' own. Whose and when are left open.
 *
 * @param length how long the stretch is, cm
 * @param high   how high its top is, cm
 * @param <C>    whose
 * @param <E>    when
 */
public record CityWallSpec<C extends Civilization, E extends Era<? super C>>(double length, double high)
        implements Spec<Earthworks.CityWall, C, E> {

    public CityWallSpec {
        if (!(length > 0) || !(high >= 200)) {
            throw new IllegalArgumentException("a wall some way long, 2 m high or more");
        }
    }
}
