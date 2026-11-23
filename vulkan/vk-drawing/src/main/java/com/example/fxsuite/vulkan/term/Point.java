package com.example.fxsuite.vulkan.term;

/**
 * A place in an expression: how far across, up and along, in centimetres, from wherever its frame has its middle (a
 * thing's own middle, or the eye).
 */
public record Point(Node node) {

    public Point {
        if (node.size() != 3) {
            throw new IllegalArgumentException("a place is three numbers, not " + node.size());
        }
    }

    /** The place {@code across} further across and {@code up} further up. */
    public Point moved(final double across, final double up) {
        return new Point(new Three(node).plus(new Three(across, up, 0)).node());
    }

    /**
     * The place with each of its numbers made so many times as much: what is laid out over it in even cells then
     * lies in cells drawn out the way a number was made smaller, and squeezed the way one was made larger.
     */
    public Point scaled(final double across, final double up, final double along) {
        return new Point(new Three(node).times(new Three(across, up, along)).node());
    }

    public Real across() {
        return new Three(node).picked(Shaped.FIRST);
    }

    public Real up() {
        return new Three(node).picked(Shaped.SECOND);
    }

    public Real along() {
        return new Three(node).picked(Shaped.THIRD);
    }

    /** The way from it back to its frame's middle, one long: from a place as the camera sees it, the way to the eye. */
    public Direction back() {
        return new Direction(new Three(node).negated().unit().node());
    }
}
