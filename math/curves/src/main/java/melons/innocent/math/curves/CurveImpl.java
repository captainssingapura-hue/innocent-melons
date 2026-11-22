package melons.innocent.math.curves;

import com.example.fxsuite.taxonomy.Leaf;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A curve worked out: one value for each x from its {@code start} to its {@code end}, the same every time, and x
 * outside them refused. A value of its own: its components are what it is given, its start and end among them, so
 * two equal curves are the one curve. Its type says which kind of curve it is, a leaf of the curves' tree
 * ({@code Curves}), as a building's implementation says what it builds; a kind may be worked out more than one way.
 * In real units: a curve is never stretched to a share, so a kind stays its kind.
 *
 * @param <K> the kind of curve it is
 */
public interface CurveImpl<K extends Leaf<?>> extends Scalar {

    /** How far either side of x its slope is taken, where it has no slope of its own, as a share of its span. */
    double SLOPE_SPAN = 1e-6;

    /** The kind of curve it is. */
    K kind();

    /** Where it starts. */
    double start();

    /** Where it ends, past its start. */
    double end();

    /** Its value at {@code x}, from its start to its end. */
    double at(double x);

    /** Its value at {@code x}, or at its start or its end, whichever is nearer, if x is outside them. */
    default double held(final double x) {
        return at(Math.clamp(x, start(), end()));
    }

    /** How steeply it rises at {@code x}: worked out from either side of it, unless the curve says it exactly. */
    default double slope(final double x) {
        final double span = (end() - start()) * SLOPE_SPAN;
        final double low = Math.max(start(), within(x) - span);
        final double high = Math.min(end(), x + span);
        return (at(high) - at(low)) / (high - low);
    }

    /** {@code x}, if it is from its start to its end; refused if not. */
    default double within(final double x) {
        if (!(x >= start() && x <= end())) {
            throw new IllegalArgumentException(kind().title() + " from " + start() + " to " + end() + ", not at " + x);
        }
        return x;
    }
}
