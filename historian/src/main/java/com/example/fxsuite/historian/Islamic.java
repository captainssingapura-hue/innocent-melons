package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Islamic: from the Prophet's time on, across the lands of Islam; mosques, domes and minarets, courtyards and gardens.
 */
public interface Islamic extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Islamic;
    }

    record It() implements Islamic {
        public String name() {
            return "Islamic";
        }

        public Year from() {
            return Years.ce(610);
        }

        public Year to() {
            return Years.ONGOING;
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
