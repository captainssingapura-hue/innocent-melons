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
