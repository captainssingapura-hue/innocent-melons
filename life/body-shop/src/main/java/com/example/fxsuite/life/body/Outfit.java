package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * What someone wears, declared: a list of things worn ({@link Wear}), inner first, each as it is set, any kind any
 * number of times; put on any body ({@link Dressed}). Nothing in it of whom it is on.
 *
 * @param name what it is called
 * @param wear what it is, inner first
 */
public record Outfit(String name, List<Wear> wear) {

    public Outfit {
        wear = List.copyOf(wear);
    }

    /** Nothing on. */
    public static final Outfit BARE = new Outfit("Bare", List.of());

    /**
     * A monk's: white leggings and black cloth shoes, a robe in {@code robe}, wide-sleeved, its collar and cuffs darker,
     * the kasaya over it darker still.
     */
    public static Outfit monk(final String name, final String robe) {
        return new Outfit(name, List.of(new Wear.Leggings("#e8e4da"), new Wear.Shoes("#262321"),
                new Wear.Robe(robe, darker(robe, 0.3), 1.5, 11, 0.35, true), new Wear.Sash(darker(robe, 0.6), 13)));
    }

    /** Someone in a shirt and boots, belted. */
    public static final Outfit TRAVELLER = new Outfit("Traveller", List.of(new Wear.Leggings("#4a4f58"),
            new Wear.Shirt("#e9e4d8", 0.8, true), new Wear.Boots("#3b2a1f", 0.45), new Wear.Belt("#3b2a1f", 4)));

    /** Men's underwear: an undershirt and briefs. */
    public static final Outfit MENS_UNDERWEAR = new Outfit("Underwear, men's", List.of(
            new Wear.Undershirt("#f2efe9", 0.3), new Wear.Briefs("#f2efe9", "#3a3f46", 0.65)));

    /** Women's underwear: a bra and panties. */
    public static final Outfit WOMENS_UNDERWEAR = new Outfit("Underwear, women's", List.of(
            new Wear.Bra("#e9d7d2", 3, 2.5), new Wear.Panties("#e9d7d2", "#d6bfb8", 0.6)));

    /** The outfits there are, to start from. */
    public static final List<Outfit> ALL = List.of(BARE, monk("Monk, red", "#a3242b"), monk("Monk, blue", "#2e5a9e"),
            monk("Monk, saffron", "#d9a521"), TRAVELLER, MENS_UNDERWEAR, WOMENS_UNDERWEAR);

    /** It with {@code piece} put on over the rest. */
    public Outfit with(final Wear piece) {
        final List<Wear> next = new ArrayList<>(wear);
        next.add(piece);
        return new Outfit(name, next);
    }

    /** It with what it wears at {@code at} set as {@code piece}. */
    public Outfit with(final int at, final Wear piece) {
        final List<Wear> next = new ArrayList<>(wear);
        next.set(at, piece);
        return new Outfit(name, next);
    }

    /** It without what it wears at {@code at}. */
    public Outfit without(final int at) {
        final List<Wear> next = new ArrayList<>(wear);
        next.remove(at);
        return new Outfit(name, next);
    }

    /** It with what it wears at {@code at} moved {@code by} places, outward when more than nothing. */
    public Outfit moved(final int at, final int by) {
        final int to = at + by;
        if (to < 0 || to >= wear.size()) {
            return this;
        }
        final List<Wear> next = new ArrayList<>(wear);
        next.add(to, next.remove(at));
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
