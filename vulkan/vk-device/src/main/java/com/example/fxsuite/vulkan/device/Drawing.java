package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Shader;

import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;

/**
 * A look on the card, built once and ready to draw with: its two shaders compiled, what it is told described, and
 * the bytes its frame's telling is read from. Everything the card is told of its layouts comes from {@code laid},
 * which the shaders' declarations and the mesh's bytes come from too. Closed, it lets go of all it made, newest
 * first.
 */
public final class Drawing implements AutoCloseable {

    private final Laid laid;
    private final Kept<Of.Told> told;
    private final Kept<Of.Telling> telling;
    private final Kept<Of.Pipeline> pipeline;
    private final Bytes frame;
    private final Kept<Of.Tellings> tellings;
    private final Kept<Of.Bound> bound;

    Drawing(final Reach reach, final Compiler compiler, final Laid laid, final Shader vertex,
            final Shader fragment) {
        final Described described = new Described(reach);
        this.laid = laid;
        this.told = described.told();
        this.telling = described.telling(told, laid.draw());
        this.pipeline = new Piped(reach, compiler).pipeline(laid, vertex, fragment, telling);
        this.frame = new Bytes(reach, "a look's frame", laid.frame().blockBytes(),
                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
        this.tellings = described.tellings();
        this.bound = described.bound(tellings, told, frame);
    }

    /** The layouts it was built from. */
    public Laid laid() {
        return laid;
    }

    /** Told what {@code filling} holds of the frame; one laid out otherwise than the look reads it is refused. */
    void told(final Filling filling) {
        if (!filling.layout().equals(laid.frame())) {
            throw new IllegalArgumentException("the look reads a frame laid out as " + laid.frame() + ", not as "
                    + filling.layout());
        }
        frame.filled(filling.filled());
    }

    Kept<Of.Pipeline> pipeline() {
        return pipeline;
    }

    Kept<Of.Telling> telling() {
        return telling;
    }

    Kept<Of.Bound> bound() {
        return bound;
    }

    @Override
    public void close() {
        bound.close();
        tellings.close();
        frame.close();
        pipeline.close();
        telling.close();
        told.close();
    }
}
