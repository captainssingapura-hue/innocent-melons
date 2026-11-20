package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Korean: from the Three Kingdoms to now; timber halls on stone platforms, heated floors, walled towns.
 */
public interface Korean extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Korean;
    }

    record It() implements Korean {
        public String name() {
            return "Korean";
        }

        public Year from() {
            return Years.bce(57);
        }

        public Year to() {
            return Years.ONGOING;
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
