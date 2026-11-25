package melons.innocent.core.interaction;

import java.util.List;
import java.util.Optional;

/**
 * A menu of what else can be done with a thing: the offers besides the one done by the plain asking, in their
 * order, and which of them is marked. The mark is moved up and down, round from the last to the first, and never
 * rests on an offer that cannot be done just now, which is listed with why all the same. It is a value: moving the
 * mark gives another menu.
 *
 * @param offers what it lists
 * @param at     which of them is marked, the first 0
 */
public record Menu(List<Offer> offers, int at) {

    public Menu {
        offers = List.copyOf(offers);
        if (at < 0 || at >= Math.max(1, offers.size())) {
            throw new IllegalArgumentException("marked one of " + offers.size() + ": " + at);
        }
    }

    /** A menu of {@code offers}, the first that can be done marked. */
    public Menu(final List<Offer> offers) {
        this(offers, offers.stream().filter(offer -> offer.barred().isEmpty()).findFirst().map(offers::indexOf)
                .orElse(0));
    }

    /** Whether it lists nothing. */
    public boolean empty() {
        return offers.isEmpty();
    }

    /** This menu with the mark moved {@code by} places, down for more than nothing, to the next that can be done. */
    public Menu moved(final int by) {
        if (by == 0 || offers.stream().allMatch(offer -> offer.barred().isPresent())) {
            return this;
        }
        int next = at;
        do {
            next = Math.floorMod(next + Integer.signum(by), offers.size());
        } while (offers.get(next).barred().isPresent());
        return new Menu(offers, next);
    }

    /** The offer marked, where it can be done: what choosing does. */
    public Optional<Offer> chosen() {
        return offers.isEmpty() ? Optional.empty()
                : Optional.of(offers.get(at)).filter(offer -> offer.barred().isEmpty());
    }
}
