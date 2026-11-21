package com.example.fxsuite.vulkan.term;

/** A way in an expression: the way a surface faces, the way a light shines, the way to the eye. */
public record Direction(Node node) {

    public Direction {
        if (node.size() != 3) {
            throw new IllegalArgumentException("a way is three numbers, not " + node.size());
        }
    }

    /** The same way, one long. */
    public Direction unit() {
        return new Direction(new Three(node).unit().node());
    }

    /** The other way. */
    public Direction negated() {
        return new Direction(new Three(node).negated().node());
    }

    /** How much of each lies along the other: one for two ways one long that are the same, nothing for two square. */
    public Real dot(final Direction other) {
        return new Three(node).dot(new Three(other.node));
    }

    /**
     * The way a light coming from {@code light} leaves a surface facing this way, as a mirror would send it: both
     * one long.
     */
    public Direction mirrored(final Direction light) {
        final Three facing = new Three(node);
        return new Direction(facing.scaled(dot(light).times(2)).less(new Three(light.node)).node());
    }
}
