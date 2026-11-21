package com.example.fxsuite.vulkan.worked;

import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A triangle as it falls on the picture: its three corners, and what lies between them at any pixel, worked out as
 * the card works it. What a point has of each corner is reckoned on the picture; what is carried between corners
 * (where on the thing, the way it faces) is then shared out by how far ahead each corner is, so it runs evenly over
 * the thing, not over the picture.
 */
record Flat(Corner first, Corner second, Corner third) {

    /**
     * Whether it faces the camera. Wound to face out of its form, a triangle facing the camera goes round clockwise
     * on the picture, which counts down from the top: the card is told the same.
     */
    boolean facesCamera() {
        return side(first, second, third.x(), third.y()) < 0;
    }

    /** The furthest left it reaches on the picture, and the furthest right, in pixels across. */
    double left() {
        return Math.min(first.x(), Math.min(second.x(), third.x()));
    }

    double right() {
        return Math.max(first.x(), Math.max(second.x(), third.x()));
    }

    /** The highest it reaches on the picture, and the lowest, in pixels down. */
    double top() {
        return Math.min(first.y(), Math.min(second.y(), third.y()));
    }

    double bottom() {
        return Math.max(first.y(), Math.max(second.y(), third.y()));
    }

    /** Whether the point ({@code x}, {@code y}) of the picture is within it. */
    boolean holds(final double x, final double y) {
        final double[] shares = shares(x, y);
        return shares[0] >= 0 && shares[1] >= 0 && shares[2] >= 0;
    }

    /** How deep it is at ({@code x}, {@code y}): evenly over the picture, as the card keeps depth. */
    double depth(final double x, final double y) {
        final double[] shares = shares(x, y);
        return shares[0] * first.depth() + shares[1] * second.depth() + shares[2] * third.depth();
    }

    /** What lies at ({@code x}, {@code y}) of the picture, within it or, carried on past its edge, without. */
    Corner at(final double x, final double y) {
        final double[] shares = shares(x, y);
        final double a = shares[0] / first.ahead();
        final double b = shares[1] / second.ahead();
        final double c = shares[2] / third.ahead();
        final double all = a + b + c;
        return new Corner(between(first.at(), second.at(), third.at(), a / all, b / all, c / all),
                between(first.faces(), second.faces(), third.faces(), a / all, b / all, c / all),
                between(first.from(), second.from(), third.from(), a / all, b / all, c / all),
                x, y, depth(x, y), 1 / all);
    }

    /** How much of each corner the point ({@code x}, {@code y}) of the picture has: the three together one. */
    private double[] shares(final double x, final double y) {
        final double whole = side(first, second, third.x(), third.y());
        return new double[] {side(second, third, x, y) / whole, side(third, first, x, y) / whole,
                side(first, second, x, y) / whole};
    }

    /** Which side of the line from {@code from} to {@code to} the point is, and how far, times the line's length. */
    private double side(final Corner from, final Corner to, final double x, final double y) {
        return (x - from.x()) * (to.y() - from.y()) - (y - from.y()) * (to.x() - from.x());
    }

    private Vec3 between(final Vec3 a, final Vec3 b, final Vec3 c, final double ofA, final double ofB,
                         final double ofC) {
        return a.times(ofA).plus(b.times(ofB)).plus(c.times(ofC));
    }
}
