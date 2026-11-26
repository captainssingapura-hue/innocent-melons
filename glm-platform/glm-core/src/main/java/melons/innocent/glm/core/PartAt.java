package melons.innocent.glm.core;

/**
 * One part of a platform put together: a part on its side, a part down the middle once and one of a pair twice.
 *
 * @param part the part
 * @param side which side it is on
 */
public record PartAt(Part part, Side side) {

    /** Its points and its form as it is on its side: the left one the right's mirror. */
    public Part shape() {
        return side == Side.LEFT ? part.mirrored() : part;
    }

    /** Its point called {@code named}, as it is on its side. */
    public Point point(final String named) {
        return side == Side.LEFT ? part.point(named).mirrored() : part.point(named);
    }

    /** In words: "right toe link", "underbody". */
    public String said() {
        return (side == Side.MIDDLE ? "" : side.name().toLowerCase(java.util.Locale.ROOT) + " ") + part.name();
    }
}
