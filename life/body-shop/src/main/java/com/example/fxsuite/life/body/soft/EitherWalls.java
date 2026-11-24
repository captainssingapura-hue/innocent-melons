package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/**
 * Two walls together, the bones and the muscles at rest, say: a point is as far outside them as it is outside the
 * nearer, and the way out of them is the way out of that one.
 *
 * @param one   the one
 * @param other the other
 */
public record EitherWalls(Walls one, Walls other) implements Walls {

    @Override
    public double distance(final double x, final double y, final double z) {
        return Math.min(one.distance(x, y, z), other.distance(x, y, z));
    }

    @Override
    public V3 out(final double x, final double y, final double z) {
        return one.distance(x, y, z) <= other.distance(x, y, z) ? one.out(x, y, z) : other.out(x, y, z);
    }
}
