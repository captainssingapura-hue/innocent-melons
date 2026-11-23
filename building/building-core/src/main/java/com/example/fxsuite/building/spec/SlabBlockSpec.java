package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A slab block, as asked for: so many flats side by side along its corridor, each so wide and so deep, the corridor so
 * wide along its front, a core of stairs and lifts between every so many flats and at its ends, and so many storeys. In
 * centimetres. Two flats to forty; each 5 m to 12 m wide and 8 m to 16 m deep; its corridor 1.2 m to 3 m; two flats a
 * core at least; two storeys to thirty.
 *
 * @param flats    how many flats there are on each storey
 * @param flatWide how wide each flat is, along the corridor
 * @param flatDeep how deep each flat is, back from the corridor
 * @param corridor how wide the corridor is
 * @param perCore  how many flats there are between one core and the next
 * @param storeys  how many storeys it has
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record SlabBlockSpec<C extends Civilization, E extends Era<? super C>>(int flats, double flatWide,
                                                                              double flatDeep, double corridor,
                                                                              int perCore, int storeys)
        implements Spec<Buildings.SlabBlock, C, E> {

    public SlabBlockSpec {
        if (flats < 2 || flats > 40 || perCore < 2 || perCore > flats || storeys < 2 || storeys > 30) {
            throw new IllegalArgumentException("two flats to forty, two a core at least, two storeys to thirty, not "
                    + flats + ", " + perCore + ", " + storeys);
        }
        if (!(flatWide >= 500 && flatWide <= 1_200) || !(flatDeep >= 800 && flatDeep <= 1_600)
                || !(corridor >= 120 && corridor <= 300)) {
            throw new IllegalArgumentException("flats 5 m to 12 m wide and 8 m to 16 m deep, a corridor 1.2 m to 3 m,"
                    + " not " + flatWide + ", " + flatDeep + ", " + corridor);
        }
    }
}
