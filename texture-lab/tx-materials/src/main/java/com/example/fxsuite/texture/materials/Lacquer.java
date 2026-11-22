package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;

import java.util.List;

/**
 * Gloss paint on sheet, as a car's body wears it: a coat of one colour under a clear, hard lacquer. The colour is
 * the thing's own and not the paint's: the same paint is on a red car and a blue one, so it is told with each
 * thing drawn. The lacquer is what is seen of the light: a small, hard glint where the sun is mirrored toward the
 * eye, and the surroundings mirrored faintly seen square on and nearly wholly seen at a glancing angle, as the
 * sky runs along the edge of a bonnet.
 *
 * <p>What it mirrors is the sky's light above and the ground's below, each as one colour: nothing that stands
 * beside the thing is seen in it. Flakes of metal in the paint, and a colour that shifts as it turns, are other
 * paints and not this one.
 */
public record Lacquer() {

    /** Its surface: the thing's colour, under the lacquer's glint and its mirroring. */
    public Surface surface() {
        return new Surface(new Tint().colour(), List.of(), new Colour("#ffffff"), 260)
                .mirroring(new Colour(0.05, 0.05, 0.05)).glancing(new Colour(0.72, 0.72, 0.72));
    }
}
