package com.example.fxsuite.vulkan.device;

/**
 * How much memory the kit holds on a card, counted as it is asked for and let go: so much of the card's own, and so
 * much where the processor reaches it. The kit's own count, in bytes: it knows nothing of what other programs hold
 * of the same card.
 */
final class Holding {

    private long carded;
    private long reached;

    /** {@code bytes} more held: of the card's own memory where {@code own}, else where the processor reaches. */
    void more(final long bytes, final boolean own) {
        if (own) {
            carded += bytes;
        } else {
            reached += bytes;
        }
    }

    /** How many bytes of the card's own memory are held. */
    long carded() {
        return carded;
    }

    /** How many bytes are held where the processor reaches them. */
    long reached() {
        return reached;
    }
}
