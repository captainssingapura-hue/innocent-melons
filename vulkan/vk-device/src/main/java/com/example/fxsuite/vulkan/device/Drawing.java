package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Shader;

import java.util.Optional;

import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;

/**
 * A look on the card, built once and ready to draw with: its two shaders compiled, what it is told described, and
 * the bytes its frame's telling is read from. Everything the card is told of its layouts comes from {@code laid},
 * which the shaders' declarations and the mesh's bytes come from too. It either writes its colours over what is
 * already drawn, as nearly every look does, or mixes them with it, as a look for what is seen through does. Each
 * frame it is told what the frame holds, and which picture of depths to look things up in. Closed, it lets go of
 * all it made, newest first.
 */
public final class Drawing implements AutoCloseable {

    private final Laid laid;
    private final boolean mixed;
    private final Described described;
    private final Kept<Of.Told> told;
    private final Kept<Of.Telling> telling;
    private final Kept<Of.Pipeline> pipeline;
    private final Bytes frame;
    private final Kept<Of.Tellings> tellings;
    private final Kept<Of.Bound> bound;
    /** The picture of depths it is set to look things up in, once it has been set to one. */
    private Optional<Depths> looked = Optional.empty();
    /** What it is set to ask rays of, as the card has it, once it has been set to any. */
    private Optional<Kept<Of.Structure>> asked = Optional.empty();

    /**
     * The look of {@code vertex} and, where it colours anything, {@code fragment}; where {@code mixed}, what it
     * draws is mixed with what is already drawn and not written over it.
     */
    Drawing(final Reach reach, final Compiler compiler, final Laid laid, final Shader vertex,
            final Optional<Shader> fragment, final boolean mixed) {
        this.described = new Described(reach, laid);
        this.laid = laid;
        this.mixed = mixed;
        this.told = described.told();
        this.telling = described.telling(told, laid.draw());
        this.pipeline = new Piped(reach, compiler).pipeline(laid, vertex, fragment, mixed, telling);
        this.frame = new Bytes(reach, "a look's frame", laid.frame().blockBytes(),
                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT);
        this.tellings = described.tellings();
        this.bound = described.bound(tellings, told, frame);
    }

    /** The layouts it was built from. */
    public Laid laid() {
        return laid;
    }

    /**
     * Whether what it draws is mixed with what is already drawn. Such a look is drawn after every look that
     * writes over what is there, and leaves nothing in a picture of depths.
     */
    public boolean mixes() {
        return mixed;
    }

    /**
     * Told what {@code filling} holds of the frame, and to read what {@code read} has where there is any: its
     * picture of depths to look things up in and, for a look that asks rays, what stands in the frame;
     * a frame laid out otherwise than the look reads it is refused.
     */
    void told(final Filling filling, final Optional<Read> read) {
        final Optional<Depths> depths = read.map(Read::depths);
        final Optional<Kept<Of.Structure>> stood = read.flatMap(Read::standing).map(Standing::structure);
        if (!filling.layout().equals(laid.frame())) {
            throw new IllegalArgumentException("the look reads a frame laid out as " + laid.frame() + ", not as "
                    + filling.layout());
        }
        frame.filled(filling.filled());
        if (depths.isPresent() && !depths.equals(looked)) {
            described.looked(bound, depths.get().view(), depths.get().sampler());
            looked = depths;
        }
        if (laid.rays() && stood.isPresent() && !stood.equals(asked)) {
            described.asked(bound, stood.get());
            asked = stood;
        }
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
