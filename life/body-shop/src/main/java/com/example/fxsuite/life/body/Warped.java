package com.example.fxsuite.life.body;

/**
 * A shape bent as a warp bends space ({@link Warp}): at a point, how far outside it the point was before it was bent,
 * {@code back} taking it there; near enough, the bending being gentle. Its box, {@code box}.
 *
 * @param inner the shape, as it was
 * @param back  the warp taking where it is bent to back to where it was
 * @param box   its box, bent
 */
public record Warped(Field.Shape inner, Warp back, double[] box) implements Field.Shape {

    @Override
    public double at(final double x, final double y, final double z) {
        final V3 p = back.apply(new V3(x, y, z));
        return inner.at(p.x(), p.y(), p.z());
    }
}
