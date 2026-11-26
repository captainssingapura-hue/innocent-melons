package melons.innocent.vehicles.boat;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A named point of a hull's lines, where two of them meet or one turns: so far across from the middle line (to the
 * right), up from the water's line, and along toward the bow, in centimetres. Lines that meet share the point.
 *
 * @param name   what it is called, as {@code sheer.3} or {@code stem.head}
 * @param across how far to the right of the middle line
 * @param up     how high over the water's line
 * @param along  how far toward the bow from the middle
 */
public record HullPoint(String name, double across, double up, double along) implements Scalar {

    public HullPoint {
        if (name == null || name.isBlank() || !Double.isFinite(across) || !Double.isFinite(up)
                || !Double.isFinite(along)) {
            throw new IllegalArgumentException("a hull's point is named and somewhere: " + name);
        }
    }
}
