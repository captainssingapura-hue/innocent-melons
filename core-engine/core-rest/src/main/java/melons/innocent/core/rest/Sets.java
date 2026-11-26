package melons.innocent.core.rest;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Sets of names by name, as what stands on what is kept: copied either to be kept as part of a state, where nothing
 * of it can be changed, or to be worked on for a tick, where all of it can.
 *
 * @param of the sets, by name
 */
record Sets(Map<String, Set<String>> of) {

    /** A copy nothing of which can be changed, in the order of the names. */
    Map<String, Set<String>> frozen() {
        final TreeMap<String, Set<String>> frozen = new TreeMap<>();
        of.forEach((name, set) -> frozen.put(name, Collections.unmodifiableSortedSet(new TreeSet<>(set))));
        return Collections.unmodifiableSortedMap(frozen);
    }

    /** A copy all of which can be changed, in the order of the names. */
    Map<String, Set<String>> thawed() {
        final Map<String, Set<String>> thawed = new TreeMap<>();
        of.forEach((name, set) -> thawed.put(name, new TreeSet<>(set)));
        return thawed;
    }
}
