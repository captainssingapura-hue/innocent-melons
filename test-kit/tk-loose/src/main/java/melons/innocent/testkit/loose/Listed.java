package melons.innocent.testkit.loose;

import com.example.fxsuite.taxonomy.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What each thing of a trial is, by its name: what it is made of, and which kind of thing it is, a node of the tree
 * of things, by which what can be done with it is found. Things gone since are still listed, and things made since,
 * as the slices of a melon cut, are added.
 */
final class Listed {

    private final Map<String, Stuff> made = new LinkedHashMap<>();
    private final Map<String, Node> kinds = new LinkedHashMap<>();

    /** The thing called {@code name} is of that {@code kind}, made of {@code stuff}. */
    void is(final String name, final Node kind, final Stuff stuff) {
        made.put(name, stuff);
        kinds.put(name, kind);
    }

    /** What the thing called {@code name} is made of. */
    Stuff stuff(final String name) {
        return made.get(name);
    }

    /** Which kind of thing the one called {@code name} is. */
    Node kind(final String name) {
        return kinds.get(name);
    }

    /** The names of the things listed, in the order they were. */
    List<String> names() {
        return List.copyOf(made.keySet());
    }

    /** What each is made of, by name. */
    Map<String, Stuff> made() {
        return Map.copyOf(made);
    }

    /** Nothing listed any more. */
    void clear() {
        made.clear();
        kinds.clear();
    }
}
