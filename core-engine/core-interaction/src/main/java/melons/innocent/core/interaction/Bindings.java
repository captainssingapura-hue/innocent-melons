package melons.innocent.core.interaction;

import com.example.fxsuite.taxonomy.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Which actions are bound to which kinds of thing: a table kept apart from the tree the kinds are in, which says
 * only what a thing is. What can be done with a kind is worked out down its lineage, from the root of its tree to
 * itself: beginning with nothing, each kind on the way that has a binding adds its actions to what has come so far,
 * or replaces it ({@link Binding}); a kind with no binding changes nothing, and so has just what the kind over it
 * has. The order is kept, since the first action offered to one who stands some way to a thing is the one done by
 * the plain asking.
 *
 * <p>It is a value: binding a kind gives another table and leaves this one as it was. The kinds may be of any trees.
 */
public final class Bindings {

    private final Map<Node, Binding> bound;

    /** A table with nothing bound. */
    public Bindings() {
        this(Map.of());
    }

    private Bindings(final Map<Node, Binding> bound) {
        this.bound = bound;
    }

    /** This table with {@code binding} bound to {@code kind}, in place of whatever was bound to it. */
    public Bindings bound(final Node kind, final Binding binding) {
        final Map<Node, Binding> more = new LinkedHashMap<>(bound);
        more.put(kind, binding);
        return new Bindings(more);
    }

    /** What is bound to {@code kind} itself: nothing, where it has only what is over it. */
    public Optional<Binding> own(final Node kind) {
        return Optional.ofNullable(bound.get(kind));
    }

    /** The actions that can be done with a thing of {@code kind}, in the order they are offered. */
    public List<Action> of(final Node kind) {
        final List<Action> actions = new ArrayList<>();
        for (final Node over : kind.lineage().reversed()) {
            final Binding binding = bound.get(over);
            if (binding instanceof Binding.Only) {
                actions.clear();
            }
            if (binding != null) {
                binding.actions().stream().filter(action -> !actions.contains(action)).forEach(actions::add);
            }
        }
        return List.copyOf(actions);
    }

    /** What is offered to one who stands so to a thing of {@code kind}. */
    public Offers offers(final Node kind, final Relation relation) {
        return new Offers(of(kind), relation);
    }
}
