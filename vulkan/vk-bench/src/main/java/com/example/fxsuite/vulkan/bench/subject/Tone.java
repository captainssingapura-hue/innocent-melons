package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;

import java.util.List;

/**
 * What the courier's parts are made of. As the engine's own figure has them: a pale shell, a teal trim, dark
 * joints, and a visor and a beacon lit from within, which show their colour whatever light falls on them. And, for
 * a courier finished in metal ({@link Finish#GILT}): polished gold and a darker bronze, titanium and a darker
 * gunmetal, each mirroring the sky above it and the ground below, tinted its own colour, with a tight bright
 * highlight where the sun is mirrored to the eye. The metals' colours are not the engine's, which has none.
 */
public enum Tone {

    SHELL("#e9e4da", "#3a3a3a", 32, "#000000", false),
    TRIM("#2f8f9d", "#3a3a3a", 32, "#000000", false),
    JOINT("#2a2e35", "#3a3a3a", 32, "#000000", false),
    VISOR("#7df9ff", "#000000", 32, "#000000", true),
    BEACON("#ff6b6b", "#000000", 32, "#000000", true),
    /** Polished gold: little colour of its own, and most of what it shows mirrored, in gold. */
    GOLD("#4a3008", "#fff0c0", 28, "#ffc457", false),
    /** A darker gold, for what joins the gold. */
    BRONZE("#2e1e06", "#e0c080", 24, "#b8863a", false),
    /** Titanium: a warm grey that mirrors less, and less sharply. */
    TITANIUM("#2c2d30", "#e4e4ea", 22, "#9a9893", false),
    /** A darker grey metal, for what joins the titanium. */
    GUNMETAL("#18191c", "#a0a0a8", 20, "#5c5d62", false),
    /** Steel, bare: a cool grey that mirrors a good deal. */
    STEEL("#34373b", "#e8eaee", 26, "#a2a7ad", false),
    /** A jet's flame: its own light, whatever falls on it. */
    FLAME("#ffb347", "#000000", 32, "#000000", true);

    private final String colour;
    private final String shine;
    private final double tight;
    private final String mirror;
    private final boolean lit;

    Tone(final String colour, final String shine, final double tight, final String mirror, final boolean lit) {
        this.colour = colour;
        this.shine = shine;
        this.tight = tight;
        this.mirror = mirror;
        this.lit = lit;
    }

    /** How a part of this tone is coloured, shines, mirrors and glows. */
    public Surface surface() {
        final Colour own = new Colour(colour);
        final Surface made = new Surface(own, List.of(), new Colour(shine), tight).mirroring(new Colour(mirror));
        return lit ? made.glowing(own) : made;
    }
}
