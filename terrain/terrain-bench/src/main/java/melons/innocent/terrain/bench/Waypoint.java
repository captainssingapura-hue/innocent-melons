package melons.innocent.terrain.bench;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A point a route goes through: so far across and along the map, and so high, in centimetres.
 *
 * @param x      how far across
 * @param z      how far along
 * @param height how high, over the sea's level
 */
public record Waypoint(double x, double z, double height) implements Scalar {

    public Waypoint {
        if (!Double.isFinite(x) || !Double.isFinite(z) || !Double.isFinite(height)) {
            throw new IllegalArgumentException("a waypoint somewhere: " + x + ", " + z + ", " + height);
        }
    }

    /** The same point over the map, {@code height} high. */
    public Waypoint raised(final double height) {
        return new Waypoint(x, z, height);
    }

    /** How far it is from {@code other} over the map, not counting how high either is. */
    public double over(final Waypoint other) {
        return Math.hypot(other.x - x, other.z - z);
    }
}
