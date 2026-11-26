package melons.innocent.core.walk;

import melons.innocent.core.rest.Gravity;
import melons.innocent.core.step.Situated;

/**
 * A walker as its engine is handed it and gives it back: the walker as described, how it stands and goes, what is
 * under it, and how hard its world pulls.
 *
 * @param walker   the walker, as it is described
 * @param standing how it stands and goes
 * @param under    what is under it, as the world is when it is handed over
 * @param gravity  how hard the world it is in pulls it down
 */
public record Walking(Walker walker, Standing standing, Under under, Gravity gravity) implements Situated<Walker> {

    /** The same walker in the same world, standing and going as {@code next} says. */
    public Walking stood(final Standing next) {
        return new Walking(walker, next, under, gravity);
    }
}
