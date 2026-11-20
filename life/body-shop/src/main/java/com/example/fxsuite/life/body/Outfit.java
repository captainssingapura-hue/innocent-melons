package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * What someone wears, declared: a few pieces of clothing ({@link Wear}), each at most once of its kind, put on any body
 * ({@link Dressed}). Nothing in it of whom it is on.
 *
 * @param name what it is called
 * @param wear its pieces
 */
public record Outfit(String name, List<Wear> wear) {

    /** Nothing on. */
    public static final Outfit BARE = new Outfit("Bare", List.of());

    /** A monk's: a robe in {@code robe}, wide-sleeved, its collar and cuffs darker; the kasaya darker still; white leggings, black cloth shoes. */
    public static Outfit monk(final String name, final String robe) {
        return new Outfit(name, List.of(new Wear.Robe(robe, darker(robe, 0.3), 1.5, 11, 0.35, true),
                new Wear.Sash(darker(robe, 0.6), 13), new Wear.Leggings("#e8e4da"), new Wear.Shoes("#262321")));
    }

    /** The outfits there are, to choose from. */
    public static final List<Outfit> ALL = List.of(BARE, monk("Monk, red", "#a3242b"), monk("Monk, blue", "#2e5a9e"),
            monk("Monk, saffron", "#d9a521"));

    /** Its piece of the kind of {@code kind}, if it wears one. */
    public <W extends Wear> Optional<W> piece(final Class<W> kind) {
        return wear.stream().filter(kind::isInstance).map(kind::cast).findFirst();
    }

    /** It with {@code piece} on, in place of any of its kind; or, if {@code on} is not, without any of its kind. */
    public Outfit with(final Class<? extends Wear> kind, final Wear piece, final boolean on) {
        final List<Wear> next = new ArrayList<>(wear.stream().filter(w -> !kind.isInstance(w)).toList());
        if (on) {
            next.add(piece);
        }
        return new Outfit(name, next);
    }

    /** {@code colour} as dark as {@code share} of itself. */
    static String darker(final String colour, final double share) {
        final int c = Integer.parseInt(colour.substring(1), 16);
        final int r = (int) Math.round(((c >> 16) & 0xff) * share);
        final int g = (int) Math.round(((c >> 8) & 0xff) * share);
        final int b = (int) Math.round((c & 0xff) * share);
        return String.format("#%02x%02x%02x", r, g, b);
    }

    @Override
    public String toString() {
        return name;
    }
}
