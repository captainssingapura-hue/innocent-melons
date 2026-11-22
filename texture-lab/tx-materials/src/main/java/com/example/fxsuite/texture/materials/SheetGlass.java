package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;

import java.util.List;

/**
 * Glass in sheets, as a car's windows are: seen through. Looked at square on, most of what is behind it shows,
 * a little darkened; looked at more and more nearly edge on it mirrors more of what is about it and shows less
 * of what is behind, until at a glancing angle it is nearly all sky. A small hard glint of the sun lies on it
 * wherever the sun is mirrored toward the eye.
 *
 * <p>What it mirrors is the sky's light above and the ground's below, each as one colour: nothing that stands
 * beside it is seen in it. What shows through is only darkened, not coloured or bent: glass has no thickness here.
 * And it casts no shadow, since whatever is seen through is left out of the sun's picture.
 */
public record SheetGlass() {

    /** How much of what is behind it shows, seen square on. */
    private static final double THROUGH = 0.78;

    /** Its surface: nearly black of itself, seen through, under a glint and a mirror that grows toward its edge. */
    public Surface surface() {
        return new Surface(new Colour("#0c1517"), List.of(), new Colour("#ffffff"), 400)
                .mirroring(new Colour(0.08, 0.09, 0.10)).glancing(new Colour(0.92, 0.94, 0.95)).clear(THROUGH);
    }
}
