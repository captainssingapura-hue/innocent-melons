package com.example.fxsuite.vulkan.render;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Sent;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.shade.Laid;

/**
 * A form on the card, sent once, to be drawn with any look laid out the same way: so one shape is drawn in many
 * materials, and many materials need no copy of the shape each. Closed, it is gone from the card.
 */
public final class Shaped implements AutoCloseable {

    private final Sent sent;

    /** {@code form}, on {@code card}, its points packed as {@code laid} says a point is. */
    public Shaped(final Card card, final Laid laid, final Mesh form) {
        this.sent = card.sent(new Packed(laid.point(), form));
    }

    /**
     * {@code form} on {@code card}; where not {@code standing}, it stands in the way of no ray, and is spared what
     * that costs: for what is too far off for its shadow to matter.
     */
    public Shaped(final Card card, final Laid laid, final Mesh form, final boolean standing) {
        this.sent = standing ? card.sent(new Packed(laid.point(), form))
                : card.sentUnstanding(new Packed(laid.point(), form));
    }

    /** The form as the card has it, for a look to draw. */
    Sent sent() {
        return sent;
    }

    @Override
    public void close() {
        sent.close();
    }
}
