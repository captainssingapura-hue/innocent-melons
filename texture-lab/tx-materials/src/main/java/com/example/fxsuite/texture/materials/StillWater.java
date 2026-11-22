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
 * Still water, as it is seen lying level: dark of itself, and mirroring what is above it, the sky by day and by
 * night; a hard, small glint where the sun is mirrored toward the eye; and faint paler ripples across it in patches
 * some metres wide, smooth between them, each ripple wandering as it runs. They stand still: nothing here knows
 * the time.
 *
 * <p>It mirrors the sky's light as one colour, not what stands beside it, and nothing is seen through it: a house
 * by the water is not seen in the water, nor the bottom under it.
 */
public record StillWater() {

    /** How far it is from one ripple to the next, in centimetres, and how much of that a ripple's paler part is. */
    private static final double APART = 60;
    private static final double PALER = 0.22;

    /** Its surface, worked out from where on the water a point is: across it and along it. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real wander = at.along().over(90).sine().times(0.35).plus(at.across().over(230).sine().times(0.5))
                .plus(at.along().over(610).plus(at.across().over(1700)).sine().times(1.3))
                .plus(at.across().over(970).plus(at.along().over(-420)).sine().times(0.8));
        final Turns ripples = new Turns(at.across().over(APART).plus(wander), new Real(1 / APART));
        // ruffled in patches some metres across, and smooth between them, as a breath of wind leaves it
        final Real ruffled = at.across().over(310).plus(at.along().over(270).sine()).sine()
                .times(at.along().over(190).plus(at.across().over(530).sine().times(1.4)).sine())
                .times(1.6).plus(new Real(0.15)).held(0, 1);
        return new Surface(new Colour("#16292c"),
                List.of(new Term("ripples", new Colour("#27413f"),
                        ripples.pulse(PALER, seen.covers()).times(ruffled), APART * PALER)),
                new Colour("#ffffff"), 160).mirroring(new Colour("#8ea6ae"));
    }
}
