package melons.innocent.core.step;

/**
 * The state of one object as an engine is handed it and gives it back: the object as it is described, how it
 * stands and moves, and what is round it, all the engine may read. The core puts it together for each object before
 * it hands it over, and keeps what comes back; the engine reaches for nothing else.
 *
 * @param <O> the kind of object it is the state of
 */
public interface Situated<O extends Simulated> {
}
