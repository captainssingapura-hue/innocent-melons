package melons.innocent.core.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * What is asked of a world for one tick, from outside it: the things to be destroyed, the things to be taken up out
 * of it, the things to be thrown, each with the motion it is given, and the driven things to be driven otherwise,
 * each with how it is to go from now on. They are done in that order, each kind by name, before anything in the
 * world is checked or stepped.
 *
 * @param destroyed the names of the things that are no more
 * @param taken     the names of the things taken up out of the world, into someone's hands
 * @param thrown    the things given a motion all at once, on top of whatever motion they had
 * @param driven    the driven things whose going is changed
 * @param outsiders what is in the world this tick and is not the world's to step, each by its name: the game engine's
 *                  avatar. They are not done in any order: they are only there, for what moves to meet
 */
public record Asked(Collection<String> destroyed, Collection<String> taken, Map<String, Moved> thrown,
                    Map<String, Moved> driven, Map<String, Outsider> outsiders) {

    public Asked {
        destroyed = List.copyOf(destroyed);
        taken = List.copyOf(taken);
        thrown = Map.copyOf(thrown);
        driven = Map.copyOf(driven);
        outsiders = Map.copyOf(outsiders);
    }

    /** Things destroyed, taken up, thrown and driven otherwise, and nothing there but the world's own things. */
    public Asked(final Collection<String> destroyed, final Collection<String> taken, final Map<String, Moved> thrown,
                 final Map<String, Moved> driven) {
        this(destroyed, taken, thrown, driven, Map.of());
    }

    /** Things destroyed, thrown and driven otherwise, and nothing taken up. */
    public Asked(final Collection<String> destroyed, final Map<String, Moved> thrown,
                 final Map<String, Moved> driven) {
        this(destroyed, List.of(), thrown, driven);
    }

    /** Nothing asked. */
    public Asked() {
        this(List.of(), List.of(), Map.of(), Map.of());
    }
}
