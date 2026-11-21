package com.example.fxsuite.texture.stone;

import com.example.fxsuite.vulkan.form.Panel;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Blurred;
import com.example.fxsuite.vulkan.term.Patches;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;

/**
 * The mortar between stones of no two shapes: stones about {@code stone} centimetres across, fitted together, with
 * about {@code joint} centimetres of mortar between them, a little more in some places and less in others.
 */
record Jointing(double stone, double joint) {

    /** What tells the stones' middles from any other scatter: this and the number after it. */
    static final int MIDDLES = 1;
    /** A pixel is never taken to cover less than this, in centimetres: an edge is never sharper than can be drawn. */
    private static final double SHARPEST = 0.01;
    /** The stretch of wall the mortar's share is worked out over, in stones each way, and how finely, to a stone. */
    private static final double STRETCH = 8;
    private static final double SAMPLES = 5;

    /** The stones a point of the wall's face is among. */
    Patches stones(final Point at) {
        return new Patches(at.across(), at.up(), stone, MIDDLES);
    }

    /**
     * How much of what a pixel covers at the point is mortar, looked at closely: all of it within half a joint of
     * the line between two stones and none of it further in, the edge between softened over what the pixel covers.
     */
    Real edged(final Point at, final Real covers) {
        return new Real(joint / 2).less(stones(at).inside()).over(covers.most(new Real(SHARPEST)))
                .plus(new Real(0.5)).held(0, 1);
    }

    /**
     * The same from any distance: as a pixel comes to cover several stones, settling to {@code average}, the
     * mortar's share of the whole wall.
     */
    Real share(final Point at, final Real covers, final double average) {
        return edged(at, covers).mixed(new Real(average), new Blurred(covers, stone).share());
    }

    /** The mortar's share of the whole wall, worked out by Java over a stretch of it. */
    double worked() {
        final Seen seen = new Seen();
        return new Averaged(new Panel(STRETCH * stone, STRETCH * stone, stone / SAMPLES).formed())
                .of(edged(seen.at(), seen.covers()));
    }
}
