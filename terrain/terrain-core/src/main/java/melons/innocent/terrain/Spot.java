package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A spot on the map: so far across (x) and along (z), in centimetres. Where lines on the map meet, they meet at a
 * spot they share.
 *
 * @param x how far across
 * @param z how far along
 */
public record Spot(double x, double z) implements Scalar {

    public Spot {
        if (!Double.isFinite(x) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("a spot somewhere on the map: (" + x + ", " + z + ")");
        }
    }

    /** How far it is from {@code other}. */
    public double to(final Spot other) {
        return Math.hypot(other.x - x, other.z - z);
    }
}
