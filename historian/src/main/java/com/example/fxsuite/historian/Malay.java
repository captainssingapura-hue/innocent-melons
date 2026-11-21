package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Malay: the peoples of the Malay peninsula and archipelago, from their early ports to now; houses of timber and thatch
 * on stilts, mosques and palaces.
 */
public interface Malay extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Malay;
    }

    record It() implements Malay {
        public String name() {
            return "Malay";
        }

        public Year from() {
            return Years.INSTANCE.ce(200);
        }

        public Year to() {
            return Years.INSTANCE.ongoing();
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
