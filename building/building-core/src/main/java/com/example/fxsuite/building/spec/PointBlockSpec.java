package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A point block, as asked for: so many storeys, square in plan, so wide each way, its core of stairs and lifts square
 * at its middle, so wide, four flats round it, one to each corner. In centimetres. Two storeys to forty; 12 m to 30 m
 * each way; its core 5 m wide at least, and no more than half as wide as the block.
 *
 * @param storeys how many storeys it has
 * @param side    how wide it is each way
 * @param core    how wide its core is each way
 * @param <C>     whose way it is built
 * @param <E>     when
 */
public record PointBlockSpec<C extends Civilization, E extends Era<? super C>>(int storeys, double side, double core)
        implements Spec<Buildings.PointBlock, C, E> {

    public PointBlockSpec {
        if (storeys < 2 || storeys > 40 || !(side >= 1_200 && side <= 3_000) || !(core >= 500 && core <= side / 2)) {
            throw new IllegalArgumentException("two storeys to forty, 12 m to 30 m each way, its core 5 m at least and"
                    + " half the block at most, not " + storeys + ", " + side + ", " + core);
        }
    }
}
