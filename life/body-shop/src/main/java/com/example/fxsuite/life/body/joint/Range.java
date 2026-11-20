package com.example.fxsuite.life.body.joint;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How far a joint may turn one way, degrees, from {@code least} to {@code most}: a bend forward from as far back as it
 * goes to as far forward, a twist from as far left to as far right.
 *
 * @param least as far as it goes the other way, less than nothing or nothing
 * @param most  as far as it goes this way
 */
public record Range(double least, double most) implements Scalar {

    /** Not at all. */
    public static final Range NONE = new Range(0, 0);

    public Range {
        if (least > most) {
            throw new IllegalArgumentException("a range from " + least + " to " + most);
        }
    }

    /** Whether it may turn {@code degrees}. */
    public boolean holds(final double degrees) {
        return degrees >= least - 1e-9 && degrees <= most + 1e-9;
    }

    /** {@code share} of the way to its end, that way: less than nothing toward {@link #least}, more toward most. */
    public double at(final double share) {
        return share < 0 ? -share * least : share * most;
    }
}
