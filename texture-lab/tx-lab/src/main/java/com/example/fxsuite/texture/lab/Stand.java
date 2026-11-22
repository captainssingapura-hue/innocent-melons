package com.example.fxsuite.texture.lab;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * The shapes on show, standing in a row on the ground in the order they are given, the row's middle over the
 * world's: where each stands, and what to look at to see them all. One shape stands at the middle; with none there
 * is only the ground. A wall, a ball and a block, unless others are chosen.
 */
public record Stand(List<Shape> shown) {

    /** How far apart one shape's side is from the next one's. */
    private static final double GAP = 60;
    /** How high is looked at when nothing stands there. */
    private static final double EYE = 100;

    public Stand {
        shown = List.copyOf(shown);
        if (shown.stream().distinct().count() != shown.size()) {
            throw new IllegalArgumentException("a shape stands on the bench once: " + shown);
        }
    }

    /** A wall, a ball and a block. */
    public Stand() {
        this(List.of(Shape.WALL, Shape.BALL, Shape.BLOCK));
    }

    /** How far the row is from end to end. */
    public double across() {
        return shown.stream().mapToDouble(Shape::across).sum() + GAP * Math.max(0, shown.size() - 1);
    }

    /** Where {@code shape}'s own middle stands; a shape that is not on show is refused. */
    public Vec3 stands(final Shape shape) {
        double from = -across() / 2;
        for (final Shape each : shown) {
            if (each == shape) {
                return new Vec3(from + each.across() / 2, each.lifted(), 0);
            }
            from += each.across() + GAP;
        }
        throw new IllegalArgumentException(shape + " is not on show");
    }

    /** What to look at to see them all: the middle of the row, half as high as the tallest stands. */
    public Vec3 middle() {
        return new Vec3(0, shown.stream().mapToDouble(shape -> 2 * Math.max(shape.lifted(), EYE / 4)).max()
                .orElse(EYE) / 2, 0);
    }
}
