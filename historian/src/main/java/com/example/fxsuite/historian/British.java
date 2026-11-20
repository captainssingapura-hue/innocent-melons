package com.example.fxsuite.historian;

import java.time.Year;

/**
 * British: the peoples of Britain after Rome left them, and the empire they made; its styles carried to its colonies,
 * the Straits among them.
 */
public interface British extends European {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof British;
    }

    record It() implements British {
        public String name() {
            return "British";
        }

        public Year from() {
            return Years.ce(410);
        }

        public Year to() {
            return Years.ONGOING;
        }

        public Civilization parent() {
            return European.IT;
        }
    }
}
