package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * A spot where a thing is touched by something that could hold it: where, which way it is pushed there (one long),
 * how far the two have run into each other there, what it is that touches it, and how that is itself going, so that
 * what is carried along by a thing in motion can be told from what stands on the ground.
 *
 * @param at     where they touch
 * @param pushes which way the thing is pushed there
 * @param deep   how far into each other they are there, in centimetres: nothing, where they only touch
 * @param on     the name of what touches it; the ground's name, where it is the ground
 * @param going  how what touches it is going there, in centimetres a second: not at all, for the ground
 */
public record Touch(Spot at, Spot pushes, double deep, String on, Spot going) {

    /** A touch by something that is not going anywhere. */
    public Touch(final Spot at, final Spot pushes, final double deep, final String on) {
        this(at, pushes, deep, on, new Spot(0, 0, 0));
    }

    /** The same touch, by something going as {@code going} says. */
    public Touch carried(final Spot going) {
        return new Touch(at, pushes, deep, on, going);
    }
}
