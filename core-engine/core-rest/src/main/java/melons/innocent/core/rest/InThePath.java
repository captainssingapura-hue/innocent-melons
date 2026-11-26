package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * What stands in the path a thing took in a tick: looked for by the world once every thing's engine has said where
 * it goes, never by an engine itself. The path, from where the thing was to where its engine put it, is gone along
 * in short parts, each blocker gone along its own path beside it; where the two first touch, the place is found
 * more nearly by halving.
 *
 * @param touching how two shapes are found to meet
 */
record InThePath(Touching touching) {

    /** How long a part of a path is at most, in centimetres; and how many parts a path is cut into at most. */
    private static final double PART = 2;
    private static final int MOST = 16;
    /** How many times the place a thing first meets what is in its path is halved toward. */
    private static final int HALVINGS = 8;

    /**
     * What a thing called {@code name}, of {@code shape}, met going from {@code from} to {@code to}: one collision
     * to each of {@code blockers} it met, in the order of their names.
     */
    List<Collision> of(final String name, final ShapeImpl<?> shape, final Placed from, final Placed to,
                       final Map<String, Blocker> blockers) {
        final List<Collision> collisions = new ArrayList<>();
        final Spot path = to.at().minus(from.at());
        new TreeMap<>(blockers).forEach((with, blocker) -> {
            final double longest = Math.max(path.length(), blocker.to().at().minus(blocker.from().at()).length());
            final int parts = (int) Math.max(1, Math.min(MOST, Math.ceil(longest / PART)));
            for (int part = 0; part <= parts; part++) {
                if (!met(shape, from, to, (double) part / parts, with, blocker).isEmpty()) {
                    collisions.add(first(name, shape, from, to, part, parts, with, blocker));
                    break;
                }
            }
        });
        return collisions;
    }

    /**
     * The collision of a thing with a blocker it is found touching at the end of part {@code part} of its path:
     * where it first touched it within that part, found by halving; at its very start, where it touched it already.
     */
    private Collision first(final String name, final ShapeImpl<?> shape, final Placed from, final Placed to,
                            final int part, final int parts, final String with, final Blocker blocker) {
        double free = Math.max(0, part - 1.0) / parts;
        double blocked = (double) part / parts;
        for (int halving = 0; halving < HALVINGS && part > 0; halving++) {
            final double between = (free + blocked) / 2;
            if (met(shape, from, to, between, with, blocker).isEmpty()) {
                free = between;
            } else {
                blocked = between;
            }
        }
        final double share = part == 0 ? 0 : free;
        return new Collision(name, with, share, along(from, to, share),
                met(shape, from, to, blocked, with, blocker).getFirst());
    }

    /** Where a thing going from {@code from} to {@code to} is, so much of the way: turned as it ends. */
    private Placed along(final Placed from, final Placed to, final double share) {
        return new Placed(from.at().plus(to.at().minus(from.at()).times(share)), to.turned());
    }

    /**
     * Where a thing of {@code shape}, so much of the way along its path, meets a blocker as far along its own:
     * with how the thing is to go against it.
     */
    private List<Touch> met(final ShapeImpl<?> shape, final Placed from, final Placed to, final double share,
                            final String with, final Blocker blocker) {
        return touching.met(shape, along(from, to, share), with, blocker.shape(),
                        along(blocker.from(), blocker.to(), share)).stream()
                .map(touch -> touch.carried(blocker.going())).toList();
    }
}
