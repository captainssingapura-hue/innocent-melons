package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The bark of an old broadleaved tree, an oak's: grey-brown and rough, darker in its furrows and paler on its
 * ridges, with weather-stains a hand or two across and a few spots of pale lichen. It is dull, with no shine.
 *
 * <p>It is laid out over where on its bough's bark a point is (across: how far round; up: how far along; and how
 * thick the wood is), so its furrows run along the wood, long and narrow, as an oak's do. The bark has no
 * depth to catch the light: that waits for relief.
 */
public record RoughBark() {

    private static final int FURROWS = 51;
    private static final int STAINS = 52;
    private static final int LICHEN = 53;
    /** How many times as long along the wood as across it a furrow is. */
    private static final double ALONG = 0.22;

    /** Its surface, worked out from where on the tree a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        return new Surface(new Colour("#6b5d4f"), List.of(
                new Term("weather-stains", new Colour("#857868"),
                        new Mottle(22, STAINS).soft(at, seen.covers()).times(0.7), 11),
                new Term("furrows", new Colour("#3f352c"),
                        new Mottle(2.4, FURROWS).soft(at.scaled(1, ALONG, 1), seen.covers()).times(0.85), 1.2),
                new Term("lichen", new Colour("#a9ad94"),
                        new Mottle(1.5, LICHEN).cell(at, 0, 0, 0).some(0.05, seen.covers()), 1.5)),
                new Colour(0, 0, 0), 8);
    }
}
