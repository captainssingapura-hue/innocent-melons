package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A point block, as asked for: so many storeys, four flats round a lift lobby they share, the lobby square, so wide
 * each way. In centimetres. Two storeys to forty; its lobby 8.3 m wide at least, so each flat's porch lies along its
 * side, and 12 m at most.
 *
 * @param storeys how many storeys it has
 * @param core    how wide its lobby is each way
 * @param <C>     whose way it is built
 * @param <E>     when
 */
public record PointBlockSpec<C extends Civilization, E extends Era<? super C>>(int storeys, double core)
        implements Spec<Buildings.PointBlock, C, E> {

    public PointBlockSpec {
        if (storeys < 2 || storeys > 40 || !(core >= 830 && core <= 1_200)) {
            throw new IllegalArgumentException("two storeys to forty, its lobby 8.3 m to 12 m wide, not " + storeys
                    + ", " + core);
        }
    }
}
