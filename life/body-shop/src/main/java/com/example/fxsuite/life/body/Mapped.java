package com.example.fxsuite.life.body;

import java.util.function.UnaryOperator;

/**
 * A shape moved point by point as a map takes it: at a point, how far outside it the point was before it was moved,
 * {@code back} taking it there; near enough, the map being gentle. Its box, {@code box}.
 *
 * @param inner the shape, as it was
 * @param back  the map taking where it is moved to back to where it was
 * @param box   its box, moved
 */
public record Mapped(Field.Shape inner, UnaryOperator<V3> back, double[] box) implements Field.Shape {

    @Override
    public double at(final double x, final double y, final double z) {
        final V3 p = back.apply(new V3(x, y, z));
        return inner.at(p.x(), p.y(), p.z());
    }
}
