package melons.innocent.core.step;

import melons.innocent.core.tick.Tick;

import java.util.List;

/**
 * The corrections of a tick's stepping, each a function of its own, named for its step. A step is handed what the
 * step before it was handed, that step's result, and what is new for this step and nothing else; it gives the
 * result after all. An engine that corrects nothing gives the result back as it was.
 *
 * @param <O> the kind of object
 * @param <S> the state of such an object
 * @param <E> the engine's explanation
 * @param <F> what the core finds and hands a correction: a collision
 */
public interface Correcting<O extends Simulated, S extends Situated<O>, E extends Explanation<O>, F> {

    /**
     * The first correction: {@code stepped}, which is what the engine gave for the very same {@code before} and
     * {@code tick}, put right for what was {@code found} afterwards.
     */
    default Explained<O, S, E> correctionS1(final S before, final Tick tick, final Explained<O, S, E> stepped,
                                            final List<F> found) {
        return stepped;
    }
}
