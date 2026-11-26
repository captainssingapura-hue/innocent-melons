package melons.innocent.core.rest;

import melons.innocent.core.step.Explanation;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The core: what works out a tick. It holds no state of any world. It is the rules a world goes by, the engine
 * that steps what nothing drives and the figures the check is made with, the same at every tick; and handed a
 * world's state and what is asked of it, it gives the state a tick on and what happened ({@link #ticked}). The same
 * state and the same things asked give the same state after, every time: nothing is read but what is handed, and
 * the tick is the state's own.
 *
 * @param loose     the engine that steps whatever nothing drives
 * @param touching  how near counts as touching
 * @param reckoning the check of a thing's forces
 * @param <E>       that engine's explanation of what it worked out
 */
public record Core<E extends Explanation<Rigid>>(Simulator<E> loose, Touching touching, Reckoning reckoning) {

    /** A core on the Earth, whatever nothing drives stepped by {@code loose}. */
    public Core(final Simulator<E> loose) {
        this(loose, new Touching(), new Reckoning());
    }

    /**
     * The state a tick on from {@code before}, what is {@code asked} done first: the tick taken is the one
     * {@code before} says is next, and what happened in it is given back stamped with it.
     */
    public Next ticked(final State before, final Asked asked) {
        return new Working<>(this, before).ticked(asked);
    }

    /**
     * What something that is not a thing of the world met, going from {@code from} to {@code to} as a box, while
     * the world went from {@code before} to {@code after}: one collision to each thing it met, each thing where
     * it was at the same moment of the tick and going as it goes. It is how a walker, which another engine steps,
     * finds what stood in its path.
     */
    public List<Collision> met(final State before, final State after, final String name, final Box box,
                               final Placed from, final Placed to) {
        final Map<String, Blocker> blockers = new TreeMap<>();
        after.things().forEach((with, rigid) -> {
            final Placed stands = after.placed(with).orElseThrow();
            blockers.put(with, new Blocker(rigid.shape(), before.placed(with).orElse(stands), stands,
                    after.moved(with).velocity()));
        });
        return new InThePath(touching).of(name, box, from, to, blockers);
    }
}
