package com.example.fxsuite.historian;

import java.time.Year;

/**
 * Chinese: from the Shang, its first dynasty known by writing, to now; building in timber frames on earth platforms,
 * its rules written down in the Song as the Yingzao Fashi (营造法式).
 */
public interface Chinese extends Civilization {

    /** The one of it. */
    It IT = new It();

    @Override
    default boolean includes(final Civilization c) {
        return c instanceof Chinese;
    }

    record It() implements Chinese {
        public String name() {
            return "Chinese";
        }

        public Year from() {
            return Years.INSTANCE.bce(1600);
        }

        public Year to() {
            return Years.INSTANCE.ongoing();
        }

        public Civilization parent() {
            return Civilization.ROOT;
        }
    }
}
