package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Japanese: from the Kofun's great tombs to now; timber halls and shrines, building learned from China and made its
 * own.
 */
public interface Japanese extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Japanese;
    }

    record It() implements Japanese {
        public String name() {
            return "Japanese";
        }

        public Year from() {
            return Years.INSTANCE.ce(250);
        }

        public Year to() {
            return Years.INSTANCE.ongoing();
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
