package com.example.fxsuite.building.spec;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.Era;

/**
 * A stall, as anyone puts one up: a seller's stand so wide and so deep, a counter under a shade. Whose and when are
 * left open.
 *
 * @param wide  how wide it is along its front, cm
 * @param deep  how deep it is, cm
 * @param shade what it stands under
 * @param <C>   whose
 * @param <E>   when
 */
public record StallSpec<C extends Civilization, E extends Era<? super C>>(double wide, double deep, Shade shade)
        implements Spec<Buildings.Stall, C, E> {

    /** What a stall stands under. */
    public enum Shade {
        /** A cloth or a mat stretched on four posts. */
        AWNING,
        /** A great umbrella on one pole. */
        UMBRELLA
    }

    public StallSpec {
        if (!(wide >= 100) || !(deep >= 80) || shade == null) {
            throw new IllegalArgumentException("a stall a metre wide or more, 80 cm deep or more, under a shade");
        }
    }
}
