package melons.innocent.core.walk;

import java.util.Optional;

/**
 * What a part of the walking engine gives for a tick, before it is handed back to the core.
 *
 * @param next   how the walker stands and goes afterwards
 * @param why    what was worked out
 * @param landed what it came down on, where it came down
 */
record Out(Standing next, Stride why, Optional<String> landed) {
}
