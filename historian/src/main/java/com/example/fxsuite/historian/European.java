package com.example.fxsuite.historian;

import java.time.Year;

/**
 * European: from the Greeks on; temples, churches and cathedrals, palaces and towns of stone and brick.
 */
public interface European extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof European;
    }

    record It() implements European {
        public String name() {
            return "European";
        }

        public Year from() {
            return Years.bce(800);
        }

        public Year to() {
            return Years.ONGOING;
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
