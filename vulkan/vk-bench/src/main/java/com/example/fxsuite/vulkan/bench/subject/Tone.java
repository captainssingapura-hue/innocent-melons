package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;

import java.util.List;

/**
 * What the courier's parts are made of, as the engine's own figure has them: a pale shell, a teal trim, dark joints,
 * and a visor and a beacon lit from within, which show their colour whatever light falls on them.
 */
public enum Tone {

    SHELL("#e9e4da", false),
    TRIM("#2f8f9d", false),
    JOINT("#2a2e35", false),
    VISOR("#7df9ff", true),
    BEACON("#ff6b6b", true);

    /** What a part that is not lit from within shines with, and how tightly: a dull grey highlight. */
    private static final String SHINE = "#3a3a3a";
    private static final double TIGHT = 32;

    private final String colour;
    private final boolean lit;

    Tone(final String colour, final boolean lit) {
        this.colour = colour;
        this.lit = lit;
    }

    /** How a part of this tone is coloured, shines and glows. */
    public Surface surface() {
        final Colour own = new Colour(colour);
        return lit ? new Surface(own, List.of(), new Colour(0, 0, 0), TIGHT).glowing(own)
                : new Surface(own, List.of(), new Colour(SHINE), TIGHT);
    }
}
