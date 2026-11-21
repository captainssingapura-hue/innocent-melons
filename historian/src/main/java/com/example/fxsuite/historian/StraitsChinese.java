package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Straits Chinese (Peranakan): the Chinese settled in the Malay ports from Malacca's time on, their own ways grown
 * among the Malays' and the British: the shophouse with its covered way along the street.
 */
public interface StraitsChinese extends Chinese {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof StraitsChinese;
    }

    record It() implements StraitsChinese {
        public String name() {
            return "Straits Chinese (Peranakan)";
        }

        public Year from() {
            return Years.INSTANCE.ce(1400);
        }

        public Year to() {
            return Years.INSTANCE.ongoing();
        }

        public Civilization parent() {
            return Chinese.IT;
        }
    }
}
