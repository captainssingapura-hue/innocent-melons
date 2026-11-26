package melons.innocent.core.step;

import java.util.List;

/**
 * What an engine gives for a tick: the object's state a tick on, the explanation of how it came by it, and what it
 * says happened.
 *
 * @param state the object's state a tick on
 * @param why   what the engine worked out, in its own type; with explaining off, its one empty explanation
 * @param said  what the engine says happened in the tick
 * @param <O>   the kind of object
 * @param <S>   the state of such an object
 * @param <E>   the engine's explanation
 */
public record Explained<O extends Simulated, S extends Situated<O>, E extends Explanation<O>>(
        S state, E why, List<Said> said) {

    public Explained {
        said = List.copyOf(said);
    }

    /** A state and its explanation, nothing said to have happened. */
    public Explained(final S state, final E why) {
        this(state, why, List.of());
    }
}
