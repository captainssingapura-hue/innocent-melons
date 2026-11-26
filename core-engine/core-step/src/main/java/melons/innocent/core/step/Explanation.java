package melons.innocent.core.step;

/**
 * What an engine worked out, in its own terms: the change of speed and of turning, what pulled and what held. Each
 * engine has a type of its own for it, closed over the ways that engine can come out, and an engine that hands on
 * to one under it gives that one's explanation inside its own. It is worked out with the state and is not state:
 * no engine is ever handed the explanation of the tick before.
 *
 * @param <O> the kind of object it explains the state of
 */
public interface Explanation<O extends Simulated> {
}
