package com.example.fxsuite.building.chinese;

import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.building.Spec;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

/**
 * A woven timber arch bridge of the Northern Song, as the Qingming scroll draws the Rainbow Bridge (虹桥): what one is
 * built from. Lengths in centimetres; heights from the street the bridge lands on.
 *
 * @param span      between its end walls' faces, the arch's feet on them
 * @param width     its deck, edge to edge
 * @param crown     its deck's top at its middle, over the street
 * @param landing   how far from its middle its deck meets the street, each way
 * @param springing how high its arch's feet are: under the street, over the water
 * @param bed       how far under the street the river's bed is, its end walls standing on it
 * @param groups    how many groups of ribs side by side, odd: those of four members and those of three in turn, the
 *                  outermost of four
 * @param pitch     how far apart the groups are, middle to middle
 * @param log       how thick its ribs are, squared
 * @param posts     how many railing posts a side
 * @param poles     how high the four poles at its ends stand; none, if 0
 */
public record WovenArch(double span, double width, double crown, double landing, double springing, double bed,
                        int groups, double pitch, double log, int posts, double poles)
        implements Spec<Bridges.Bridge, Chinese, ChineseEras.NorthernSong> {

    public WovenArch {
        if (!(span > 0) || !(width > 0) || !(crown > 0) || !(landing > span / 2) || !(bed > 0)
                || !(springing < crown) || !(springing > -bed)) {
            throw new IllegalArgumentException("a bridge spans something, lands past its span, its feet between the bed"
                    + " and its crown");
        }
        if (groups < 3 || groups % 2 == 0 || !(pitch > 0) || !(log > 0) || posts < 2 || poles < 0) {
            throw new IllegalArgumentException("an odd number of groups of ribs, three or more; railing posts; poles or"
                    + " none");
        }
    }
}
