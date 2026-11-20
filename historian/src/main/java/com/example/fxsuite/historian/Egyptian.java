package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Ancient Egyptian: from the first kings of a united Egypt to Rome taking it; pyramids, temples and tombs of stone.
 */
public interface Egyptian extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Egyptian;
    }

    record It() implements Egyptian {
        public String name() {
            return "Ancient Egyptian";
        }

        public Year from() {
            return Years.bce(3100);
        }

        public Year to() {
            return Years.bce(30);
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
