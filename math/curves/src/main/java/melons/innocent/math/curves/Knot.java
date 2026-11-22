package melons.innocent.math.curves;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A point a curve is given: at {@code x}, the value {@code y}. Both finite.
 *
 * @param x where along
 * @param y its value there
 */
public record Knot(double x, double y) implements Scalar {

    public Knot {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("a knot somewhere, of some value: (" + x + ", " + y + ")");
        }
    }
}
