package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;

import java.util.List;

/**
 * Asphalt as a road is paved with it: dark grey, nearly black where it is new and paler where traffic and weather
 * have worn the tar off its stones, in soft patches a metre or so across, and speckled all over with the pale
 * stones it is made of. It is dull, with the least shine.
 *
 * <p>Its patches and its stones are in cells that fill space, so they lie the same on a road that climbs or leans
 * as on a level one, and come to their one grey far off. Its colours and sizes are their author's, from no measured
 * road.
 */
public record Tarmac() {

    private static final int WORN = 111;
    private static final int STONES = 112;
    /** How far across a worn patch is, and a stone, in centimetres; and what share of its grains are pale stones. */
    private static final double PATCH = 110;
    private static final double STONE = 1.4;
    private static final double PALE = 0.22;

    /** Its surface, worked out from where on the thing a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real covers = seen.covers();
        return new Surface(new Colour("#34363a"), List.of(
                new Term("worn patches", new Colour("#4d4f52"), new Mottle(PATCH, WORN).soft(at, covers).times(0.7),
                        PATCH / 2),
                new Term("stones", new Colour("#8c8d8b"),
                        new Mottle(STONE, STONES).cell(at, 0, 0, 0).some(PALE, covers), STONE)),
                new Colour(0.04, 0.04, 0.04), 20);
    }
}
