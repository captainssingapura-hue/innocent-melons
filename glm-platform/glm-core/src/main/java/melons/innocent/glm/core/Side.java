package melons.innocent.glm.core;

/**
 * Which side of the platform a part is on: down its middle, once; or one of a pair, on its right or its left.
 */
public enum Side {
    MIDDLE, RIGHT, LEFT;

    /** What a point is called on this side, of a part down the middle that is joined to both sides. */
    public String named(final String point) {
        return this == LEFT ? point.replace(", right", ", left") : point;
    }
}
