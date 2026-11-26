package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;

/** The level part of a way or a place: what is left of it with its height taken away, or set. */
record Level() {

    /** {@code way} with no part of it upward. */
    Spot of(final Spot way) {
        return new Spot(way.across(), 0, way.along());
    }

    /** {@code spot} at the height {@code up}. */
    Spot at(final Spot spot, final double up) {
        return new Spot(spot.across(), up, spot.along());
    }
}
