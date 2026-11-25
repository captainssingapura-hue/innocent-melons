package melons.innocent.core.interaction;

import java.util.List;

/**
 * What is bound to one kind of thing: the actions that can be done with it, said one of two ways. Added, they come on
 * top of whatever the kinds over it give it, which is what most kinds want; or they are all it has, whatever is over
 * it, which a kind wants that must lose something it would inherit: a melon is a fruit, and is not eaten as it is.
 */
public sealed interface Binding {

    /** The actions it says, in the order they are offered. */
    List<Action> actions();

    /** These, on top of what is inherited. */
    record Adding(List<Action> actions) implements Binding {

        public Adding {
            actions = List.copyOf(actions);
        }

        /** These, on top of what is inherited. */
        public Adding(final Action... actions) {
            this(List.of(actions));
        }
    }

    /** These and no others, whatever is inherited. */
    record Only(List<Action> actions) implements Binding {

        public Only {
            actions = List.copyOf(actions);
        }

        /** These and no others, whatever is inherited. */
        public Only(final Action... actions) {
            this(List.of(actions));
        }
    }
}
