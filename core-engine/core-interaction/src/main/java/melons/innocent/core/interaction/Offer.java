package melons.innocent.core.interaction;

import java.util.Optional;

/**
 * An action as it is offered to one who stands some way to a thing: what it is, what it is called to them, whether
 * it is the one done by the plain asking, and why it cannot be done just now, where it cannot: it is offered all the
 * same, so that what could be done is learnt.
 *
 * @param action  the action
 * @param label   what it is called
 * @param primary whether it is the first offered, done by the plain asking
 * @param barred  why it cannot be done just now; nothing, where it can
 */
public record Offer(Action action, String label, boolean primary, Optional<String> barred) {

    /** What it is called, with why it cannot be done just now, where it cannot. */
    public String said() {
        return barred.map(why -> label + " (" + why + ")").orElse(label);
    }
}
