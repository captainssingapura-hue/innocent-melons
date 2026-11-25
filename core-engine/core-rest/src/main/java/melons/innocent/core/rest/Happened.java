package melons.innocent.core.rest;

import java.util.Set;

/**
 * Something that happened in a tick of the world at rest and in motion, for whatever is outside to go by, and for
 * the next tick's check: each names the thing it happened to.
 */
public sealed interface Happened {

    /** The thing it happened to. */
    String thing();

    /** A thing was destroyed: it is no longer in the world, and holds nothing up. */
    record Destroyed(String thing) implements Happened {
    }

    /** A driven thing was set going otherwise: what rode it is let go, keeping the way it was going. */
    record Driven(String thing) implements Happened {
    }

    /** A thing was thrown: given a motion all at once, and in motion from this tick. */
    record Thrown(String thing) implements Happened {
    }

    /** A thing at rest was found no longer held, and is in motion from this tick. */
    record Unsettled(String thing) implements Happened {
    }

    /** A thing in motion came down on {@code on} and stopped there. */
    record Landed(String thing, String on) implements Happened {
    }

    /** A thing in motion has come to rest, standing on {@code on}. */
    record Rested(String thing, Set<String> on) implements Happened {
    }

    /** Two things were found run into each other where they were described: a fault in the description. */
    record Overlapping(String thing, String other) implements Happened {
    }
}
