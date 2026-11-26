package melons.innocent.core.rest;

import melons.innocent.core.step.Correcting;
import melons.innocent.core.step.Engine;
import melons.innocent.core.step.Explanation;

import java.util.List;

/**
 * What steps a thing in motion: an engine, in the one shape of an engine, for a rigid thing; one for each way of
 * moving, fitted to the core from outside it. It is handed the thing as it is described, how it stands and moves
 * now and what is round it ({@link Body}), the tick it is to work out, which is the world's own, and what is asked
 * of the thing; it gives how the thing stands and moves a tick on, its explanation of what it worked out, and what
 * it says happened, a landing ({@link Landing}). It touches nothing else, keeps nothing, and handed the same it
 * gives the same.
 *
 * <p>Its first correction ({@link #correctionS1}) is handed the same body and tick, what its stepping gave, and
 * the collisions the world then found in the thing's path; a simulator that corrects nothing gives its stepping
 * back.
 *
 * @param <E> its explanation of what it worked out
 */
public interface Simulator<E extends Explanation<Rigid>>
        extends Engine<Rigid, Body, E>, Correcting<Rigid, Body, E, Collision> {

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
}
