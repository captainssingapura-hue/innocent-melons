package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Mesoamerican: from the Olmec to the Spanish conquest; stepped pyramids, ball courts, cities of plazas.
 */
public interface Mesoamerican extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Mesoamerican;
    }

    record It() implements Mesoamerican {
        public String name() {
            return "Mesoamerican";
        }

        public Year from() {
            return Years.bce(1200);
        }

        public Year to() {
            return Years.ce(1521);
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
