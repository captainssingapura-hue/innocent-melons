package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A slab block, as asked for: so long and so wide, its corridor so wide along its front, so many storeys, its first so
 * high and the rest so high. How it stands, where its pillars and its cores are, is its builder's to work out; how it
 * is parted into flats, another's. In centimetres. From 27 m long to 250 m; 9.2 m wide to 19 m, of it a corridor 1.2 m
 * to 3 m and the rest 8 m to 16 m deep behind; two storeys to forty, the first 3 m to 5 m high and the rest 2.6 m to
 * 3.6 m.
 *
 * @param length   how long it is, along its corridor
 * @param width    how wide it is, back to front, its corridor with it
 * @param corridor how wide its corridor is
 * @param storeys  how many storeys it has
 * @param ground   how high its first storey is, floor to floor
 * @param storey   how high each storey over it is
 * @param <C>      whose way it is built
 * @param <E>      when
 */
public record SlabBlockSpec<C extends Civilization, E extends Era<? super C>>(double length, double width,
                                                                              double corridor, int storeys,
                                                                              double ground, double storey)
        implements Spec<Buildings.SlabBlock, C, E> {

    public SlabBlockSpec {
        if (!(length >= 2_700 && length <= 25_000)) {
            throw new IllegalArgumentException("a slab block 27 m to 250 m long, not " + length);
        }
        if (!(corridor >= 120 && corridor <= 300) || !(width - corridor >= 800 && width - corridor <= 1_600)) {
            throw new IllegalArgumentException("a corridor 1.2 m to 3 m and 8 m to 16 m behind it, not " + corridor
                    + " of " + width);
        }
        if (storeys < 2 || storeys > 40) {
            throw new IllegalArgumentException("two storeys to forty, not " + storeys);
        }
        if (!(ground >= 300 && ground <= 500) || !(storey >= 260 && storey <= 360)) {
            throw new IllegalArgumentException("its first storey 3 m to 5 m high and the rest 2.6 m to 3.6 m, not "
                    + ground + " and " + storey);
        }
    }

    /** How deep it is behind its corridor. */
    public double deep() {
        return width - corridor;
    }
}
