package melons.innocent.core.rest;

import melons.innocent.core.step.Said;

/**
 * What a simulator says of a thing that came down on something in the tick.
 *
 * @param on what it came down on
 */
public record Landing(String on) implements Said {
}
