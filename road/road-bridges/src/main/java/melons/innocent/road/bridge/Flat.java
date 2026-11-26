package melons.innocent.road.bridge;

import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;

/**
 * Level ground, {@code height} high everywhere, as far as anything is built: what a bridge stands on when no terrain
 * is given it.
 *
 * @param height how high it is
 */
public record Flat(double height) implements Ground {

    /** How far it reaches each way from the middle, in centimetres: 100 km. */
    private static final double REACH = 10_000_000;

    @Override
    public Extent extent() {
        return new Extent(-REACH, -REACH, REACH, REACH);
    }

    @Override
    public double height(final double x, final double z) {
        return height;
    }
}
