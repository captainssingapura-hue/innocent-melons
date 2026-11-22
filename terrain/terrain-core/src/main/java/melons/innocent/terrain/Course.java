package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.Scalar;
import melons.innocent.math.curves.CurveImpl;

/**
 * How a line on the map runs from its first spot to its second: straight, or bowed to one side along a curve. What
 * an edge of the map's graph carries.
 */
public sealed interface Course extends Scalar permits Course.Straight, Course.Bowed {

    /** Straight from its first spot to its second. */
    record Straight() implements Course {
    }

    /**
     * Bowed: at each distance along the straight line from its first spot to its second, so far to its left as
     * {@code offset} says there, to its right where that is less than nothing. The curve runs from nothing to the
     * line's length, and is nothing at both ends, so the line still meets its spots.
     *
     * @param offset how far to the left of the straight line it runs, at each distance along it
     */
    record Bowed(CurveImpl<?> offset) implements Course {
    }
}
