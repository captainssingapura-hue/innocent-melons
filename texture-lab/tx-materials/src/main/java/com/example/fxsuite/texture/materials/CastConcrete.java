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
 * Concrete cast in slabs, as a yard or a car park is paved: a pale warm grey, no two slabs quite the same grey,
 * with a narrow dark joint between each slab and the next. It is dull: it has no shine and mirrors nothing.
 *
 * <p>The slabs are four metres each way, measured in the thing's own frame across it, up it and along it, so a
 * floor and a wall are jointed alike. Further off the joints go first and then the slabs' differences, until it
 * is one grey.
 */
public record CastConcrete() {

    /** How far it is from one joint to the next, and how wide a joint is, in centimetres. */
    private static final double SLAB = 400;
    private static final double JOINT = 1.6;
    /** How much of the darker grey the darkest slab shows. */
    private static final double DARKEST = 0.4;
    private static final double HALF = 0.5;

    /** Its surface, worked out from where on the thing a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real across = at.across().over(SLAB);
        final Real along = at.along().over(SLAB);
        // up it, the joints lie half a slab from the thing's middle: a slab lying flat is not all joint on top
        final Real up = at.up().over(SLAB).plus(new Real(HALF));
        final Real own = new Scatter(across.floored(), along.floored().plus(up.floored().times(57)), 11, SLAB)
                .settling(seen.covers());
        final Real joints = joint(across, seen).most(joint(along, seen)).most(joint(up, seen));
        return new Surface(new Colour("#b9b6ad"), List.of(
                new Term("each slab's own grey", new Colour("#9c9a93"), own.times(DARKEST), SLAB),
                new Term("joints", new Colour("#5f5d58"), joints, JOINT)),
                new Colour(0, 0, 0), 8);
    }

    /** How much of a pixel is joint, the joints lying one to each turn of {@code turns}. */
    private Real joint(final Real turns, final Seen seen) {
        return new Turns(turns, new Real(1 / SLAB)).pulse(JOINT / SLAB, seen.covers());
    }
}
