package melons.innocent.testkit.loose;

import melons.innocent.core.interaction.Menu;
import melons.innocent.core.interaction.Offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * What the walker's hands have to tell, in words: over the picture, and in the walk's window. Over the picture it
 * names what is held or at hand and says what T does and that G has more; with the menu open, it lists the menu,
 * its mark before the one marked; and with a thing readied to be thrown, it says so, with how much power has
 * gathered as a bar and what the mouse does. The lettering over the picture has no brackets but square ones and no
 * comma, so why a thing cannot be done is set off by a dash.
 *
 * @param handling the hands
 */
record Told(Handling handling) {

    /** How many marks the bar of gathered power has when it is full. */
    private static final int BAR = 12;

    /** What is written over the picture, line by line; nothing, with nothing held or at hand. */
    List<String> hints() {
        final Optional<String> target = handling.target();
        if (target.isEmpty()) {
            return List.of();
        }
        if (handling.menu().isPresent()) {
            return listed(target.get(), handling.menu().get());
        }
        if (handling.mode().isPresent()) {
            final int marks = (int) Math.round(handling.power() * BAR);
            return List.of(handling.mode().get().label() + " " + target.get(),
                    "[" + "*".repeat(marks) + ".".repeat(BAR - marks) + "] "
                            + Math.round(handling.power() * 100) + "%",
                    "HOLD THE MOUSE BUTTON - LET GO TO THROW   [G] BACK");
        }
        final List<Offer> offers = handling.offers();
        final String first = offers.isEmpty() ? "" : "[T] " + plain(offers.getFirst());
        return List.of((handling.held().isPresent() ? "HOLDING " : "") + target.get(),
                first + (offers.size() > 1 ? "   [G] MORE" : ""));
    }

    /** The menu as lines, every offer as wide as the widest so that they stand in a column under the name. */
    private List<String> listed(final String name, final Menu menu) {
        final int wide = menu.offers().stream().mapToInt(offer -> plain(offer).length()).max().orElse(0);
        final List<String> lines = new ArrayList<>(List.of(name));
        for (int offer = 0; offer < menu.offers().size(); offer++) {
            lines.add(String.format(Locale.ENGLISH, "%s%-" + wide + "s", offer == menu.at() ? "> " : "  ",
                    plain(menu.offers().get(offer))));
        }
        lines.add("UP - DOWN: CHOOSE   [T] DO IT   [G] CLOSE");
        return lines;
    }

    /** An offer as the lettering over the picture can write it. */
    private String plain(final Offer offer) {
        return offer.barred().map(why -> offer.label() + " - " + why).orElse(offer.label());
    }

    /** What the window says: what is held or at hand, what is offered of it, its state, and what was last done. */
    String said() {
        final String at = handling.target().map(name -> (handling.held().isPresent() ? "In hand: " : "At hand: ")
                + name + ", a " + handling.kind(name).name().replace('-', ' ') + ": "
                + handling.offers().stream().map(Offer::said).collect(Collectors.joining(", "))
                + handling.mode().map(mode -> String.format(Locale.ENGLISH, "; %s, power %.0f%%",
                        mode.label().toLowerCase(Locale.ENGLISH), handling.power() * 100)).orElse(""))
                .orElse("Nothing at hand");
        return handling.last().isEmpty() ? at : at + System.lineSeparator() + "Last: " + handling.last();
    }
}
