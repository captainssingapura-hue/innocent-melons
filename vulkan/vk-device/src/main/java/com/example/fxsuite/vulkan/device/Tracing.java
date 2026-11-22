package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Shader;

import java.util.Optional;

/**
 * A look that colours nothing, on the card, built once: the one shader that places each point, with none run for
 * each pixel. What a picture of depths is drawn with ({@link Depths}), which keeps only how near each thing is. It
 * is a type of its own so that it can be handed to nothing that draws colours. Closed, it is gone from the card.
 */
public final class Tracing implements AutoCloseable {

    private final Drawing drawing;

    /** The look of {@code vertex} alone, laid out as {@code laid} says. */
    Tracing(final Reach reach, final Compiler compiler, final Laid laid, final Shader vertex) {
        this.drawing = new Drawing(reach, compiler, laid, vertex, Optional.empty(), false);
    }

    /** The layouts it was built from. */
    public Laid laid() {
        return drawing.laid();
    }

    /** The look itself, for what draws on depths alone to draw everything with. */
    Drawing drawing() {
        return drawing;
    }

    @Override
    public void close() {
        drawing.close();
    }
}
