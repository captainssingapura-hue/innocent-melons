package melons.innocent.core.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Collision;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Simulator;

import java.util.List;

/**
 * A thing's path in a tick, barred by what the world found in it. The thing goes as far along its path as it had
 * come when it first met anything; of the rest of its path it keeps what is not into what it met, so it slides
 * along a thing in its way, and still falls beside it, and does not go through it.
 *
 * @param collisions what the world found in the thing's path, one at least
 */
record Barred(List<Collision> collisions) {

    /** The first of them the thing came to along its path. */
    Collision first() {
        Collision first = collisions.getFirst();
        for (final Collision collision : collisions) {
            first = collision.share() < first.share() ? collision : first;
        }
        return first;
    }

    /**
     * Where the thing is a tick on, after all: from where it first met anything, the rest of the path
     * {@code stepped} gave it from {@code now}, less what of that rest goes into each thing it met; and, where it
     * was into one already at its path's start, set back out of it. Turned as its stepping left it.
     */
    Placed placed(final Simulator.Mover now, final Simulator.Stepped stepped) {
        final Collision first = first();
        Spot rest = stepped.next().placed().at().minus(now.placed().at()).times(1 - first.share());
        for (final Collision collision : collisions) {
            final double into = rest.dot(collision.touch().pushes());
            rest = into < 0 ? rest.minus(collision.touch().pushes().times(into)) : rest;
        }
        final Spot out = first.share() == 0 ? first.touch().pushes().times(Math.max(0, first.touch().deep()))
                : new Spot(0, 0, 0);
        return new Placed(first.there().at().plus(rest).plus(out), stepped.next().placed().turned());
    }
}
