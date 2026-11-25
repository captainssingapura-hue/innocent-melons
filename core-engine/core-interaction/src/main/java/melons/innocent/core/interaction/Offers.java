package melons.innocent.core.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * What is offered to one who stands some way to a thing, of the actions that can be done with it: those that are
 * called something to one standing so, in their order, the first the one done by the plain asking, and each with
 * why it cannot be done just now, where whoever is asked says so.
 *
 * @param actions  the actions that can be done with the thing
 * @param relation how the one acting stands to it
 */
public record Offers(List<Action> actions, Relation relation) {

    public Offers {
        actions = List.copyOf(actions);
    }

    /** What is offered, each barred as {@code barred} says of its action. */
    public List<Offer> offered(final Function<Action, Optional<String>> barred) {
        final List<Offer> offers = new ArrayList<>();
        for (final Action action : actions) {
            action.offered(relation).ifPresent(label ->
                    offers.add(new Offer(action, label, offers.isEmpty(), barred.apply(action))));
        }
        return offers;
    }

    /** What is offered, nothing barred. */
    public List<Offer> offered() {
        return offered(action -> Optional.empty());
    }

    /** The one done by the plain asking: the first offered; none, where nothing is. */
    public Optional<Offer> primary() {
        return offered().stream().findFirst();
    }

    /** Whether {@code action} is among what is offered. */
    public boolean offers(final Action action) {
        return actions.contains(action) && action.offered(relation).isPresent();
    }
}
