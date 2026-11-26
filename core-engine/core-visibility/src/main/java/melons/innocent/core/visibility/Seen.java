package melons.innocent.core.visibility;

import melons.innocent.core.motion.Spot;

/**
 * A thing as it is asked about: its middle, and how far any of it reaches from there. A tree, a square of forest, a
 * building: whatever is drawn at one fineness. In centimetres.
 *
 * @param middle its middle
 * @param reach  how far any of it is from its middle, at most
 */
public record Seen(Spot middle, double reach) {

    public Seen {
        if (!(reach >= 0)) {
            throw new IllegalArgumentException("a thing reaches some way from its middle, or no way: " + reach);
        }
    }

    /** A thing with no size to speak of, at {@code middle}. */
    public Seen(final Spot middle) {
        this(middle, 0);
    }
}
