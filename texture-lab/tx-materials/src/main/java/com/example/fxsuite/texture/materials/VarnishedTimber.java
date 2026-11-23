package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.List;

/**
 * Worked timber under varnish, as a boat's posts, rails and benches are: a warm mid brown, its grain darker lines
 * running along the wood a few millimetres apart, wandering a little as grain does; and over it the varnish's small
 * hard glint and a little of the sky mirrored, more at a glancing angle. The grain runs along the thing's own first
 * way (across, as a point says where it is), which a part made of timber is laid out along. Its colours and sizes
 * are the builder's.
 */
public record VarnishedTimber() {

    /** How far apart the grain's lines are, in centimetres, and how much of that a darker line is. */
    private static final double APART = 0.6;
    private static final double DARKER = 0.3;

    /** Its surface. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real wander = at.across().over(37).sine().times(0.6).plus(at.across().over(11).sine().times(0.25));
        final Turns grain = new Turns(at.up().plus(at.along()).over(APART).plus(wander), new Real(1 / APART));
        return new Surface(new Colour("#7b4a26"),
                List.of(new Term("grain", new Colour("#4f2c14"), grain.pulse(DARKER, seen.covers()), APART * DARKER)),
                new Colour("#fff4e0"), 90).mirroring(new Colour(0.03, 0.03, 0.03))
                .glancing(new Colour(0.35, 0.35, 0.35));
    }
}
