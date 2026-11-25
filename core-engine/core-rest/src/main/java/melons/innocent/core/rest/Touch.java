package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * A spot where a thing is touched from below by something that could hold it up: where, which way it is pushed
 * there (one long, and upward at least a little), how far the two have run into each other there, and what it is
 * that touches it.
 *
 * @param at     where they touch
 * @param pushes which way the thing is pushed there
 * @param deep   how far into each other they are there, in centimetres: nothing, where they only touch
 * @param on     the name of what touches it; the ground's name, where it is the ground
 */
public record Touch(Spot at, Spot pushes, double deep, String on) {
}
