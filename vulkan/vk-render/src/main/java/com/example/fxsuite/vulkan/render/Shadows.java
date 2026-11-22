package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Depths;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Tracing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Outlined;
import com.example.fxsuite.vulkan.view.Beam;

import java.util.function.Consumer;

/**
 * Shadows, on the card: the sun's own picture of a frame, so many pixels wide and tall, and the one look everything
 * is drawn into it with. Each frame that has shadows, whatever casts one is drawn here first, through the beam the
 * sun's light falls by; the frame itself is then drawn reading this picture, each look asking of each point
 * whether anything in the picture is nearer the sun.
 *
 * <p>The whole picture is drawn again each time: nothing of the last frame's is kept. Closed, it is gone from the
 * card.
 */
public final class Shadows implements AutoCloseable {

    private final int pixels;
    private final Depths depths;
    private final Tracing tracing;

    /** Shadows on {@code card} for looks laid out as {@code laid} says, in a picture {@code pixels} wide and tall. */
    public Shadows(final Card card, final Laid laid, final int pixels) {
        this.pixels = pixels;
        this.depths = card.depths(pixels);
        try {
            this.tracing = card.tracing(laid, new Outlined(laid).vertex());
        } catch (final RuntimeException refused) {
            depths.close();
            throw refused;
        }
    }

    /** How many pixels wide and tall the sun's picture is. */
    public int pixels() {
        return pixels;
    }

    /**
     * The sun's picture drawn afresh through {@code beam}, for the frame {@code framed} tells of: whatever
     * {@code casting} draws is in it, and casts a shadow; what it leaves out casts none. The picture, for the
     * frame to be drawn reading it.
     */
    public Depths cast(final Framed framed, final Beam beam, final Consumer<Pass> casting) {
        depths.drawn(tracing, framed.beamed(beam), casting);
        return depths;
    }

    @Override
    public void close() {
        tracing.close();
        depths.close();
    }
}
