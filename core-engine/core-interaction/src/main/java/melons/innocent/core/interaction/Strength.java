package melons.innocent.core.interaction;

import java.util.Locale;
import java.util.Optional;

/**
 * How strong the one acting is: the heaviest thing they can take up, and the heaviest they can throw. An action a
 * thing is too heavy for is still offered, with why it cannot be done: the binding says what can be done with a kind
 * of thing, and the weight of this one says whether it can be done now.
 *
 * @param carried the heaviest thing carried, in kilograms
 * @param thrown  the heaviest thing thrown, in kilograms
 */
public record Strength(double carried, double thrown) {

    /** One who carries forty kilograms and throws twenty. */
    public Strength() {
        this(40, 20);
    }

    /** Why {@code action} cannot be done with a thing weighing so many {@code kilograms}; nothing, where it can. */
    public Optional<String> barred(final Action action, final double kilograms) {
        final double most = action == Action.CARRY ? carried : action == Action.THROW ? thrown
                : Double.POSITIVE_INFINITY;
        return kilograms > most ? Optional.of(String.format(Locale.ENGLISH, "too heavy at %.0f kg", kilograms))
                : Optional.empty();
    }
}
