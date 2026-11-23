package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A house, as asked for: so wide across its front and so deep, so many storeys, each so high floor to floor, and so
 * many windows across its front on each storey. In centimetres. Room for a house: 4 m each way at least, one storey to
 * four, each 2.5 m to 4 m, and no more windows across its front than 1.5 m of it each.
 *
 * @param wide    how wide it is across its front
 * @param deep    how deep it is, front to back
 * @param storeys how many storeys it has
 * @param storey  how high each storey is, floor to floor
 * @param windows how many windows across its front on each storey
 * @param <C>     whose way it is built
 * @param <E>     when
 */
public record HouseSpec<C extends Civilization, E extends Era<? super C>>(double wide, double deep, int storeys,
                                                                          double storey, int windows)
        implements Spec<Buildings.House, C, E> {

    public HouseSpec {
        if (!(wide >= 400) || !(deep >= 400)) {
            throw new IllegalArgumentException("a house 4 m each way at least, not " + wide + " by " + deep);
        }
        if (storeys < 1 || storeys > 4) {
            throw new IllegalArgumentException("a house of one storey to four, not " + storeys);
        }
        if (!(storey >= 250) || !(storey <= 400)) {
            throw new IllegalArgumentException("a storey 2.5 m to 4 m high, not " + storey);
        }
        if (windows < 1 || windows * 150 > wide) {
            throw new IllegalArgumentException("one window across its front at least, and 1.5 m of it to each, not "
                    + windows + " across " + wide);
        }
    }
}
