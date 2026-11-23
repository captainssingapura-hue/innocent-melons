package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;

import java.util.List;

/**
 * A paper lantern with a light inside it: it glows in its own colour, the thing's, told with each lantern drawn (red,
 * on a river boat), as bright by night as by day, and is lit a little more by the sun besides. The paper's own colour
 * under the light is the same colour, darker. Its numbers are the builder's.
 */
public record PaperLantern() {

    /** How much of its colour it gives of its own, and how much of it shows as paint under the light. */
    private static final double GLOWS = 0.85;
    private static final double PAPER = 0.35;

    /** Its surface. */
    public Surface surface() {
        final Colour told = new Tint().colour();
        return new Surface(told.scaled(new Real(PAPER)), List.of(), new Colour("#000000"), 1)
                .glowing(told.scaled(new Real(GLOWS)));
    }
}
