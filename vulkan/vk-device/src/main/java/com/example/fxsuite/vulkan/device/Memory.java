package com.example.fxsuite.vulkan.device;

import java.util.Locale;

/**
 * How much memory is held on a card by whoever opened it, in megabytes: of the card's own memory, of memory the
 * processor reaches, and how much of its own the card has in all. The card's whole is shared with every other
 * program drawing on it, of which this says nothing.
 *
 * @param carded  megabytes held of the card's own memory
 * @param reached megabytes held where the processor reaches them
 * @param whole   megabytes of its own memory the card has
 */
public record Memory(double carded, double reached, double whole) {

    /** It, in a line. */
    public String said() {
        return String.format(Locale.ENGLISH, "On the card: %,.0f MB of %,.0f; reached by the processor: %,.0f MB",
                carded, whole, reached);
    }
}
