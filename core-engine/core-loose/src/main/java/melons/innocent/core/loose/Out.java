package melons.innocent.core.loose;

import melons.innocent.core.rest.Simulator;

import java.util.Optional;

/**
 * What a part of the loose body gives for a tick, or for a part of one, before it is handed back to the core.
 *
 * @param next how the thing stands and moves afterwards
 * @param on   what it came down on, where it came down
 * @param why  what was worked out
 */
record Out(Simulator.Mover next, Optional<String> on, Why why) {
}
