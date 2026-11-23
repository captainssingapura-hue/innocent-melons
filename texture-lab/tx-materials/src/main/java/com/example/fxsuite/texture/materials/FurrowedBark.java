package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.List;

/**
 * The bark of an old walnut: grey ridges standing between dark furrows that run along the wood, wandering this way
 * and that so that two furrows meet and part again, and the ridges between them are long narrow plates crossing
 * in diamonds. Each plate is a little paler or darker than the next, and there are weather-stains over the whole.
 * It is dull, with no shine.
 *
 * <p>It is laid out over where on its bough's bark a point is (across: how far round; up: how far along, each
 * bough set on by a stretch of its own), so the furrows run along the wood. A furrow is a stripe along the wood,
 * filtered as every stripe is ({@link Turns}), bent round the bough by a soft noise that changes slowly along it:
 * where the bending crowds stripes together they meet. The bark has no depth to catch the light: that waits for relief. Its
 * colours and sizes are their author's, from no measured tree.
 */
public record FurrowedBark() {

    private static final int BENDING = 61;
    private static final int SHADES = 63;
    private static final int STAINS = 64;
    /** How far apart the furrows are round the bough, in centimetres; and how wide a furrow is, of that. */
    private static final double APART = 3.6;
    private static final double FURROW = 0.32;
    /**
     * How far a furrow is bent round, at most, in furrows; and over how far along the wood the bending turns, the
     * bending turning far more quickly round the bough, so that furrows side by side bend differently, and meet.
     */
    private static final double BENT = 0.9;
    private static final double BENDS = 11;
    /** How long a plate is along the wood, in centimetres, for its shade. */
    private static final double PLATE = 16;
    /** How far the plates of one ridge are moved along from the next, of a plate. */
    private static final double STAGGER = 0.37;

    /** Its surface, worked out from where on the tree a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real covers = seen.covers();
        final Real counted = counted(at, covers);
        return new Surface(new Colour("#7c776f"), List.of(
                new Term("weather-stains", new Colour("#918b80"),
                        new Mottle(18, STAINS).soft(at, covers).times(0.6), 9),
                new Term("plates", new Colour("#5d5851"),
                        new Scatter(counted.floored(),
                                at.up().over(PLATE).plus(counted.floored().times(STAGGER)).floored(), SHADES, APART)
                                .settling(covers).times(0.7), APART),
                new Term("furrows", new Colour("#2a2622"),
                        new Turns(counted, new Real(1 / APART)).pulse(FURROW, covers), APART)),
                new Colour(0, 0, 0), 8);
    }

    /**
     * How many furrows round the bough a point is, counted from where the way round begins, each furrow bent round
     * by a noise that turns slowly along the wood; far off, where the bending can no longer be told, bent no more.
     */
    private Real counted(final Point at, final Real covers) {
        final Real bend = new Mottle(BENDS, BENDING).soft(at.scaled(BENDS / (APART * 1.5), 1, 1), covers)
                .less(new Real(0.5)).times(2 * BENT);
        return at.across().over(APART).plus(bend);
    }
}
