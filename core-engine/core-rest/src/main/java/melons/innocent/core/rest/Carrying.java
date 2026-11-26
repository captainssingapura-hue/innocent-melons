package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * What is driven, and what rides it. A driven thing goes as it is told to, whatever stands on it or under it: a
 * train on its rails. It is a frame for what rests on it: a thing at rest whose every supporter is the driven thing,
 * or rides it, rides it too, and is carried along with it each tick, at rest in its frame though it moves over the
 * ground. A thing that stands on a driven thing and on something else besides is not carried; it is to be checked.
 */
final class Carrying {

    /** How each driven thing is going, by its name. */
    private final Map<String, Moved> driving = new TreeMap<>();
    /** What each thing that rides is carried by, by the rider's name. */
    private final Map<String, String> riding = new TreeMap<>();

    /** What is driven and what rides as a state keeps them, to be worked on for a tick or only asked. */
    Carrying(final Map<String, Moved> driving, final Map<String, String> riding) {
        this.driving.putAll(driving);
        this.riding.putAll(riding);
    }

    /** How each driven thing is going, as a state keeps it. */
    Map<String, Moved> driving() {
        return new TreeMap<>(driving);
    }

    /** What each thing that rides is carried by, as a state keeps it. */
    Map<String, String> riding() {
        return new TreeMap<>(riding);
    }

    /** The thing called {@code name} is driven, going as {@code going} says from now on. */
    void driven(final String name, final Moved going) {
        driving.put(name, going);
    }

    /** Whether the thing called {@code name} is driven. */
    boolean drives(final String name) {
        return driving.containsKey(name);
    }

    /** The thing called {@code name} is gone, or no longer rides anything. */
    void left(final String name) {
        driving.remove(name);
        riding.remove(name);
    }

    /** How the thing called {@code name} is going over the ground, driven or riding; not at all, otherwise. */
    Spot going(final String name) {
        final Moved driven = driving.getOrDefault(name, driving.get(riding.getOrDefault(name, "")));
        return driven == null ? new Spot(0, 0, 0) : driven.velocity();
    }

    /** What rides the driven thing called {@code name}, of the things {@code resting}: nearest it first, by name. */
    List<String> riders(final String name, final Supports supports, final Map<String, Placed> resting) {
        final Set<String> carried = new TreeSet<>(Set.of(name));
        final List<String> riders = new ArrayList<>();
        boolean grew = true;
        while (grew) {
            grew = false;
            for (final String carrier : List.copyOf(carried)) {
                for (final String over : supports.over(carrier)) {
                    if (resting.containsKey(over) && !carried.contains(over)
                            && carried.containsAll(supports.under(over))) {
                        carried.add(over);
                        riders.add(over);
                        grew = true;
                    }
                }
            }
        }
        return riders;
    }

    /**
     * Who rides what, worked out afresh from what stands on what. Given back: the things that stand on a driven
     * thing or its riders without riding it, each standing on something else too.
     */
    Set<String> ridden(final Supports supports, final Map<String, Placed> resting) {
        riding.clear();
        final Set<String> torn = new TreeSet<>();
        for (final String name : driving.keySet()) {
            final List<String> riders = riders(name, supports, resting);
            riders.forEach(rider -> riding.put(rider, name));
            final List<String> carried = new ArrayList<>(riders);
            carried.add(name);
            for (final String carrier : carried) {
                supports.over(carrier).stream().filter(over -> resting.containsKey(over)
                        && !riders.contains(over)).forEach(torn::add);
            }
        }
        return torn;
    }

    /** Each driven thing gone on for so many {@code seconds}, and what rides it with it. */
    void carried(final Map<String, Placed> resting, final double seconds) {
        for (final String name : List.copyOf(resting.keySet())) {
            final Spot going = going(name);
            if (going.length() > 0) {
                resting.put(name, resting.get(name).moved(going.times(seconds)));
            }
        }
    }
}
