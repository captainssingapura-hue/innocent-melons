package com.example.fxsuite.texture.stone;

import com.example.fxsuite.texture.brick.BrickWall;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;

/**
 * A wall standing on a footing, as a house's does: the footing's stone from the ground up to {@code height}
 * centimetres, and the wall's bricks from there, their first course laid on the footing's top. One surface: each
 * part keeps its own terms, and each comes down to its own average far off, the stone's below and the brick's above.
 */
public record Footed(BrickWall wall, Surface footing, double height) {

    /** A pixel is never taken to cover less than this, in centimetres, so the line between the two can be drawn. */
    private static final double SHARPEST = 0.01;

    public Footed {
        if (!(height > 0)) {
            throw new IllegalArgumentException("a footing has some height: " + height);
        }
    }

    /** Its face: the footing below, the wall above, the line between softened over what a pixel covers. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Real above = seen.at().up().less(new Real(height)).over(seen.covers().most(new Real(SHARPEST)))
                .plus(new Real(0.5)).held(0, 1);
        return wall.surface(seen.at().moved(0, -height), seen.covers()).above(footing, above);
    }
}
