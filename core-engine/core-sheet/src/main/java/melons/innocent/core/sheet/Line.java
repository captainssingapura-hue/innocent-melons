package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.List;

/**
 * A line a sheet is swept along, seen from above with its height along it: a road's middle line, a ramp's, a roof's
 * ridge. Measured along itself as seen from above, from nothing at its start to its length, in centimetres. A
 * heading is counted as a compass counts: from along round toward across, in radians.
 */
public interface Line {

    /** How long it is, as seen from above. */
    double length();

    /** Where it is so {@code far} along itself, its height included. */
    Spot at(double far);

    /** Which way it heads there, in radians. */
    double heading(double far);

    /** How much it rises for each centimetre it goes on, there. */
    double grade(double far);

    /** How sharply it bends to its right there: one over the radius it turns on, less than nothing to its left. */
    double bends(double far);

    /** Whether it comes back to its start at its own height, so that it has no ends. */
    default boolean closed() {
        return false;
    }

    /**
     * Each place of it abreast of the floor spot so far {@code across} and {@code along}: where a line square to it
     * meets the spot. One for a straight line; one to each turn of a round one that passes the spot.
     */
    List<Abreast> abreast(double across, double along);

    /**
     * A place of a line abreast of a spot.
     *
     * @param layer which pass of the line by the spot this is, counted from nothing
     * @param far   how far along the line the place is
     * @param aside how far to the line's right the spot is of it; less than nothing, to its left
     */
    record Abreast(int layer, double far, double aside) {
    }
}
