package melons.innocent.core.step;

import melons.innocent.core.tick.Tick;

import java.util.List;

/**
 * An engine: a function that finds an object's state a tick on. It is handed the state the tick before left, with
 * what is round the object in it; the tick, which is the world's own; and what is asked of the object at that
 * tick. It gives the state a tick on, with its explanation and what it says happened.
 *
 * <p>An engine keeps nothing from one tick to the next, reads nothing but what it is handed, and changes nothing:
 * handed the same, it gives the same. It may be made with the rules' own figures, which are the same at every tick,
 * and it may hand its object on to an engine under it.
 *
 * @param <O> the kind of object it simulates
 * @param <S> the state of such an object
 * @param <E> its explanation of what it worked out
 */
public interface Engine<O extends Simulated, S extends Situated<O>, E extends Explanation<O>> {

    /** The state a tick on from {@code before}, the tick being {@code tick} and {@code events} asked at it. */
    Explained<O, S, E> simulated(S before, Tick tick, List<? extends Event<O>> events);
}
