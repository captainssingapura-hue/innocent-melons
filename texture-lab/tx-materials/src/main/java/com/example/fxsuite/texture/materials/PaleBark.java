package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * The bark of a poplar: pale grey with a cast of green, and smooth over most of the tree, marked with small dark
 * flecks where its pores are and with faint weather-stains; low on an old trunk it has broken into dark rough
 * ridges, which thin out upward until the smooth pale bark is all there is. It is dull, with no shine.
 *
 * <p>It is laid out over where on its bough's bark a point is (across: how far round; up: how far along, each
 * bough set on by a stretch of its own, the trunk's nothing), so the flecks stand in staggered rows and only the
 * trunk is dark at its foot. The bark has no depth to catch the light: that waits for relief. Its colours and
 * sizes are their author's, from no measured tree.
 */
public record PaleBark() {

    private static final int DIAMONDS = 91;
    private static final int STAINS = 92;
    private static final int RIDGES = 93;
    /** A fleck, in centimetres: how wide round the bough, how tall along it; and what share of cells have one. */
    private static final double WIDE = 2.2;
    private static final double TALL = 1.6;
    private static final double MARKED = 0.07;
    private static final double STAGGER = 0.5;
    /** How far up the trunk its rough dark foot reaches, in centimetres. */
    private static final double FOOT = 320;

    /** Its surface, worked out from where on the tree a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real covers = seen.covers();
        final Real row = at.up().over(TALL).floored();
        final Real low = new Real(1).less(at.up().over(FOOT)).held(0, 1);
        return new Surface(new Colour("#b3b6a2"), List.of(
                new Term("weather-stains", new Colour("#9ea390"),
                        new Mottle(16, STAINS).soft(at, covers).times(0.6), 8),
                new Term("pores", new Colour("#3b3a33"),
                        new Scatter(at.across().over(WIDE).plus(row.times(STAGGER)).floored(), row, DIAMONDS, TALL)
                                .some(MARKED, covers), TALL),
                new Term("rough foot", new Colour("#4a463e"),
                        new Mottle(6, RIDGES).soft(at.scaled(1, 0.25, 1), covers).times(2.4)
                                .less(new Real(1.1).less(low)).held(0, 1).times(low), 3)),
                new Colour(0, 0, 0), 8);
    }
}
