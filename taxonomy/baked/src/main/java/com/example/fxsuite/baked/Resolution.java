package com.example.fxsuite.baked;

import java.util.Locale;

/**
 * How finely a thing is made: coarse, to be seen far off, or by many at once; middling; fine, to be seen near. A leaf
 * may be made at any of them its recipe offers ({@link Recipe}), each kept apart from the others.
 */
public enum Resolution {
    COARSE, MIDDLE, FINE;

    /** What it is called where it is kept: {@code fine}. */
    public String named() {
        return name().toLowerCase(Locale.ROOT);
    }
}
