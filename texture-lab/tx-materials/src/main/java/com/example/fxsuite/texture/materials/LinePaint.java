package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The paint a road's lines are painted in: white, matt, a little greyed and worn through in soft patches where
 * wheels have crossed it. Unlike a car's paint it has no clear coat and mirrors nothing.
 *
 * <p>Its wear is in cells that fill space and comes to its one off-white far off. Its colours and sizes are their
 * author's.
 */
public record LinePaint() {

    private static final int WEAR = 121;
    /** How far across a worn patch is, in centimetres. */
    private static final double PATCH = 35;

    /** Its surface, worked out from where on the thing a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        return new Surface(new Colour("#f0f0ea"), List.of(
                new Term("wear", new Colour("#b9b9b2"), new Mottle(PATCH, WEAR).soft(at, seen.covers()).times(0.45),
                        PATCH / 2)),
                new Colour(0, 0, 0), 8);
    }
}
