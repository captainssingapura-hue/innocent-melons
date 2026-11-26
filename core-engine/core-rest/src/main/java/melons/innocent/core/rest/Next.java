package melons.innocent.core.rest;

/**
 * What a tick gives: the state after it, and what happened in it, stamped with the tick that was taken.
 *
 * @param state   the whole state of the world a tick on
 * @param stamped what happened in the tick, with which tick it was
 */
public record Next(State state, Stamped stamped) {
}
