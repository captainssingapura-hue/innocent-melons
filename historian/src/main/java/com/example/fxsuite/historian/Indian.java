package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Indian: from the Indus valley's cities to now; temples of stone, stepped wells, palaces and tombs.
 */
public interface Indian extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Indian;
    }

    record It() implements Indian {
        public String name() {
            return "Indian";
        }

        public Year from() {
            return Years.INSTANCE.bce(3300);
        }

        public Year to() {
            return Years.INSTANCE.ongoing();
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
