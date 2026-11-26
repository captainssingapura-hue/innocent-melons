package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * What else there is, for a thing that has just been stepped to find in its path: the outsiders; every other thing
 * in motion, from where it was to where its own engine has just put it; the round things at rest, which nothing
 * rests on and the regular tick does not meet; and, for a thing with corners, the things with corners at rest,
 * which its regular tick meets only from above. All of it is as one tick found it and made it, so every
 * thing's path is looked along against the same world, whatever order they are taken in.
 *
 * <p>Where what is met can itself be moved, the two are brought to how they would go on together along the line
 * they meet on: each one's going weighed by its mass. So what one loses the other gains, and neither is thrown
 * back. A thing at rest that is come at slowly is not moved: it stands, and what leans on it stops.
 *
 * @param things    every thing of the world, by its name
 * @param resting   where each thing at rest stands
 * @param carried   how a thing at rest is going over the ground, being carried or not
 * @param before    how each thing in motion stood and moved when the tick began
 * @param after     how each thing in motion stands and moves as its engine has just left it, uncorrected
 * @param outsiders what is in the world this tick and is not the world's to step
 * @param seconds   how long the tick is
 */
record Others(Map<String, Rigid> things, Map<String, Placed> resting, Function<String, Spot> carried,
              Map<String, Simulator.Mover> before, Map<String, Simulator.Mover> after,
              Map<String, Outsider> outsiders, double seconds) {

    /** How fast a thing at rest must be come at to be moved by it, in centimetres a second. */
    private static final double STRIKES = 1;

    /** What the thing in motion called {@code name} may find in its path. */
    Map<String, Blocker> round(final String name) {
        final Map<String, Blocker> blockers = new TreeMap<>();
        outsiders.forEach((with, outsider) -> blockers.put(with,
                new Blocker(outsider.box(), outsider.placed(), outsider.placed(), outsider.going())));
        final Spot going = after.get(name).moved().velocity();
        after.forEach((with, stepped) -> {
            if (!with.equals(name)) {
                blockers.put(with, new Blocker(things.get(with).shape(), before.get(with).placed(),
                        stepped.placed(), together(name, going, with, stepped.moved().velocity())));
            }
        });
        resting.forEach((with, stands) -> {
            if (things.get(with).shape() instanceof Ellipsoid) {
                final Spot its = carried.apply(with);
                blockers.put(with, new Blocker(things.get(with).shape(), stands, stands,
                        going.minus(its).length() < STRIKES ? its : together(name, going, with, its)));
            } else if (!(things.get(name).shape() instanceof Ellipsoid)
                    && !(things.get(with).shape() instanceof Shell)) {
                // a thing with corners at rest bars another's path and stands: from where it was carried from
                final Spot its = carried.apply(with);
                blockers.put(with, new Blocker(things.get(with).shape(), stands.moved(its.times(-seconds)),
                        stands, its));
            }
        });
        return blockers;
    }

    /** How two things would go on together: each one's going, weighed by its mass. */
    private Spot together(final String name, final Spot going, final String with, final Spot other) {
        final double mine = things.get(name).kilograms();
        final double its = things.get(with).kilograms();
        return going.times(mine / (mine + its)).plus(other.times(its / (mine + its)));
    }

    /**
     * The things at rest that were struck, of all the collisions {@code found}, each with the first collision that
     * struck it: a round thing at rest that a thing in motion was coming at, and fast enough to move it.
     */
    Map<String, Collision> struck(final Map<String, List<Collision>> found) {
        final Map<String, Collision> struck = new TreeMap<>();
        found.forEach((name, collisions) -> collisions.forEach(collision -> {
            final String with = collision.with();
            if (resting.containsKey(with) && things.get(with).shape() instanceof Ellipsoid
                    && after.get(name).moved().velocity().minus(carried.apply(with))
                            .dot(collision.touch().pushes()) < -STRIKES) {
                struck.putIfAbsent(with, collision);
            }
        }));
        return struck;
    }
}
