package melons.innocent.core.whole;

import melons.innocent.core.rest.State;
import melons.innocent.core.walk.Standing;
import melons.innocent.core.walk.Walker;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The whole state of a world at a tick, as a value: its things, at rest and in motion, and its walkers, each as it
 * is described and as it stands and goes. Nothing in it changes; a tick gives the next ({@link Dispatcher#ticked}).
 *
 * @param things    the things of the world, and its ground, its gravity and its tick
 * @param walkers   every walker in it, as it is described, by its name
 * @param standings how each walker stands and goes
 */
public record Whole(State things, Map<String, Walker> walkers, Map<String, Standing> standings) {

    public Whole {
        walkers = Collections.unmodifiableSortedMap(new TreeMap<>(walkers));
        standings = Collections.unmodifiableSortedMap(new TreeMap<>(standings));
        if (!walkers.keySet().equals(standings.keySet())) {
            throw new IllegalArgumentException("every walker stands somewhere, and only walkers do");
        }
    }

    /** A world of things alone, nobody in it yet. */
    public Whole(final State things) {
        this(things, Map.of(), Map.of());
    }

    /** This whole with {@code walker} in it, standing and going as {@code standing} says. */
    public Whole walked(final Walker walker, final Standing standing) {
        if (walkers.containsKey(walker.name()) || things.things().containsKey(walker.name())) {
            throw new IllegalArgumentException("there is already something called " + walker.name());
        }
        final Map<String, Walker> all = new TreeMap<>(walkers);
        all.put(walker.name(), walker);
        final Map<String, Standing> stand = new TreeMap<>(standings);
        stand.put(walker.name(), standing);
        return new Whole(things, all, stand);
    }

    /** This whole with other things in it, and the same walkers. */
    public Whole of(final State other) {
        return new Whole(other, walkers, standings);
    }
}
