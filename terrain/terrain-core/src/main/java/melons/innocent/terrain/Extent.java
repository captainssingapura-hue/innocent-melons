package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A stretch of the map, square to it: from {@code x0} to {@code x1} across and from {@code z0} to {@code z1} along, in
 * centimetres. It has some size each way.
 *
 * @param x0 its edge furthest left, across
 * @param z0 its edge nearest, along
 * @param x1 its edge furthest right
 * @param z1 its edge furthest off
 */
public record Extent(double x0, double z0, double x1, double z1) implements Scalar {

    public Extent {
        if (!(x0 < x1) || !(z0 < z1) || !Double.isFinite(x1 - x0) || !Double.isFinite(z1 - z0)) {
            throw new IllegalArgumentException("a stretch of the map of some size each way: " + x0 + ".." + x1
                    + " across, " + z0 + ".." + z1 + " along");
        }
    }

    /** How far it is across. */
    public double across() {
        return x1 - x0;
    }

    /** How far it is along. */
    public double along() {
        return z1 - z0;
    }

    /** Whether ({@code x}, {@code z}) lies in it, its edges included. */
    public boolean holds(final double x, final double z) {
        return x >= x0 && x <= x1 && z >= z0 && z <= z1;
    }
}
