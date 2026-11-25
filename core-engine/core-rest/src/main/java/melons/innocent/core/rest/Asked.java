package melons.innocent.core.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * What is asked of a world for one tick, from outside it: the things to be destroyed, the things to be thrown, each
 * with the motion it is given, and the driven things to be driven otherwise, each with how it is to go from now on.
 * They are done in that order, each kind by name, before anything in the world is checked or stepped.
 *
 * @param destroyed the names of the things taken out of the world
 * @param thrown    the things given a motion all at once, on top of whatever motion they had
 * @param driven    the driven things whose going is changed
 */
public record Asked(Collection<String> destroyed, Map<String, Moved> thrown, Map<String, Moved> driven) {

    public Asked {
        destroyed = List.copyOf(destroyed);
        thrown = Map.copyOf(thrown);
        driven = Map.copyOf(driven);
    }

    /** Nothing asked. */
    public Asked() {
        this(List.of(), Map.of(), Map.of());
    }
}
