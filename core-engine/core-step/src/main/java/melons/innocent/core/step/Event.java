package melons.innocent.core.step;

/**
 * Something asked of an object at a tick, from outside its engine: handed to the engine with the tick, beside the
 * state.
 *
 * @param <O> the kind of object it is asked of
 */
public interface Event<O extends Simulated> {
}
