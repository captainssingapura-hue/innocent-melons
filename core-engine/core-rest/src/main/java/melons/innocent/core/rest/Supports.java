package melons.innocent.core.rest;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * What stands on what: the graph the check goes by. Each thing at rest is kept with the things that hold it up, and
 * each thing with those it holds up, so that when one is taken away or let go, the things standing on it are found
 * at once and nothing else is looked at. The ground holds whatever stands on it and is itself held by nothing.
 */
public final class Supports {

    /** What each thing at rest stands on, by name. */
    private final Map<String, Set<String>> on = new TreeMap<>();
    /** What each thing holds up, by name: the other way round. */
    private final Map<String, Set<String>> holds = new TreeMap<>();

    /** {@code thing} now stands on {@code these}, and on nothing it stood on before. */
    public void stands(final String thing, final Set<String> these) {
        left(thing);
        on.put(thing, new TreeSet<>(these));
        these.forEach(holder -> holds.computeIfAbsent(holder, none -> new TreeSet<>()).add(thing));
    }

    /** {@code thing} no longer stands on anything: let go, or gone. What it holds up still names it. */
    public void left(final String thing) {
        final Set<String> were = on.remove(thing);
        if (were != null) {
            were.forEach(holder -> holds.getOrDefault(holder, new TreeSet<>()).remove(thing));
        }
    }

    /** {@code thing} is gone from the world: it stands on nothing and holds nothing up. */
    public void gone(final String thing) {
        left(thing);
        holds.remove(thing);
    }

    /** What {@code thing} stands on; nothing, where it is not at rest. */
    public Set<String> under(final String thing) {
        return Set.copyOf(on.getOrDefault(thing, Set.of()));
    }

    /** What stands on {@code thing}. */
    public Set<String> over(final String thing) {
        return new TreeSet<>(holds.getOrDefault(thing, Set.of()));
    }
}
