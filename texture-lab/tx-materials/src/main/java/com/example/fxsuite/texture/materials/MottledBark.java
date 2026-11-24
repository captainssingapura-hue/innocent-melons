package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Patches;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The bark of a plane tree: smooth, and shed in flakes, so that the trunk is a patchwork of the pale cream of new
 * bark, the olive of bark some seasons old and the grey-brown of the oldest not yet fallen, each patch of no shape
 * in particular and no two alike. It is dull, with no shine.
 *
 * <p>It is laid out over where on its bough's bark a point is (across: how far round; up: how far along), in two
 * layers of irregular patches of different sizes, some of each layer coloured: olive ones under larger brown ones,
 * so the three colours meet every way. Far off the patches run into the one colour they come to between them. The
 * bark has no depth to catch the light and its flakes do not lift: those wait for relief. Its colours and sizes are
 * their author's, from no measured tree.
 */
public record MottledBark() {

    private static final int OLIVE = 81;
    private static final int OLIVES = 82;
    private static final int BROWN = 83;
    private static final int BROWNS = 84;
    private static final int STAINS = 85;
    /** How far across a patch of each layer is, in centimetres, and what share of each layer's are coloured. */
    private static final double OLIVE_PATCH = 9;
    private static final double BROWN_PATCH = 15;
    private static final double OLIVE_SHARE = 0.42;
    private static final double BROWN_SHARE = 0.3;

    /** Its surface, worked out from where on the tree a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real covers = seen.covers();
        return new Surface(new Colour("#ddd6b8"), List.of(
                new Term("weather-stains", new Colour("#c2bb9d"),
                        new Mottle(20, STAINS).soft(at, covers).times(0.5), 10),
                new Term("olive patches", new Colour("#8a8f5e"),
                        new Patches(at.across(), at.up(), OLIVE_PATCH, OLIVE).scatter(OLIVES)
                                .some(OLIVE_SHARE, covers), OLIVE_PATCH),
                new Term("old brown patches", new Colour("#7a6650"),
                        new Patches(at.across(), at.up(), BROWN_PATCH, BROWN).scatter(BROWNS)
                                .some(BROWN_SHARE, covers), BROWN_PATCH)),
                new Colour(0, 0, 0), 8);
    }
}
