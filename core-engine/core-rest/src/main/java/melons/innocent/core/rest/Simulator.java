package melons.innocent.core.rest;

import java.util.List;
import java.util.Optional;

/**
 * What steps a thing in motion: one for each way of moving, fitted to the core from outside it. It is told the
 * thing, how it stands and moves now, what it may ask of the world round it, and how long a tick is; it says how the
 * thing stands and moves a tick on, and what it landed on if it landed. It touches nothing else, and told the same
 * it says the same.
 */
public interface Simulator {

    /** What a simulator may ask of the world round the thing it steps. */
    interface Around {

        /** Where {@code rigid}, were it placed so, would be touched from below by what could hold it up. */
        List<Touch> touches(Rigid rigid, Placed placed);
    }

    /**
     * A thing in motion, at a tick.
     *
     * @param placed where it is and how it is turned
     * @param moved  how it is moving
     * @param landed whether it has come down on something and stopped there, and is not yet at rest
     * @param still  how many ticks running it has been held without moving
     */
    record Mover(Placed placed, Moved moved, boolean landed, int still) {

        /** A thing just let go where it stood: not moving yet, and on nothing. */
        public Mover(final Placed placed) {
            this(placed, new Moved(), false, 0);
        }
    }

    /**
     * A tick's stepping of a thing.
     *
     * @param next how it stands and moves a tick on
     * @param on   what it landed on in the tick, where it landed
     */
    record Stepped(Mover next, Optional<String> on) {
    }

    /** {@code rigid}, standing and moving as {@code now} says, a tick of so many {@code seconds} on. */
    Stepped stepped(Rigid rigid, Mover now, Around around, double seconds);
}
