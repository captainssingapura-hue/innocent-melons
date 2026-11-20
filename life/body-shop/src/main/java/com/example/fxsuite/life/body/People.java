package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * The people of the game's world as it is, declared: each a body, a skin and what they wear ({@link Person}), every one
 * the same model. The roles the world has (its stallholders, customers and riders; the office's scrum master and
 * developers; the temple's monks; a bar's bartender and drinkers; guests out to enjoy a place; the two sides at a
 * game), each in the colours the game has always drawn them in, at real heights, each of a kind a little different from
 * the next: a different height, build, skin and hair, by their number. To be placed in a world by declaration.
 */
public final class People {

    /** Hoodies, hair, shirts and shorts, as the game has always had them. */
    static final String[] HOODIES = {"#4a5d7a", "#6b4a7a", "#3f6b52", "#7a5a3f", "#555b63"};
    static final String[] HAIR = {"#1c1c1c", "#6b4a2b", "#c9a14a", "#2a1d14", "#8a8a8a"};
    static final String[] SHIRTS = {"#b04a3a", "#3a6ea5", "#e0c35a", "#5d8a54", "#8a5aa8", "#d9d4c7"};
    static final String[] SHORTS = {"#d8cfb8", "#2e3440", "#6b7f99", "#9a8566"};
    /** Skins, light to dark. */
    static final String[] SKINS = {"#f1c6a5", "#e0b48f", "#d7a17a", "#c99670", "#b98762", "#8d5a3b", "#6b4430"};

    private People() {
    }

    /** A body {@code tall} cm, so broad. */
    private static Proportions body(final double tall, final double build) {
        return Person.body(tall, build);
    }

    /** The {@code n}th of a kind's body: between 1.62 and 1.88 m, a little broader or slighter, by its number. */
    private static Proportions nth(final int n) {
        return body(162 + (n * 37 % 27), 0.92 + (n * 13 % 7) * 0.04);
    }

    private static String skin(final int n) {
        return SKINS[n % SKINS.length];
    }

    /** Hair of the {@code n}th: short mostly, long or in a bun now and then. */
    private static Wear hair(final int n, final String colour) {
        return switch (n % 5) {
            case 1 -> new Wear.LongHair(colour, 16);
            case 3 -> new Wear.Bun(colour);
            default -> new Wear.ShortHair(colour, 1);
        };
    }

    private static Outfit outfit(final String name, final Wear... wear) {
        return new Outfit(name, List.of(wear));
    }

    /** Behind a stall: a jacket and trousers, an apron over them. */
    public static Person stallholder() {
        return new Person("Stallholder", body(170, 1.1), skin(1), outfit("Stallholder",
                new Wear.Shirt("#d9d4c7", 0.8, true), new Wear.Trousers("#4a4f58", 1.4, 0.95),
                new Wear.Jacket("#3a3f45", 2.4, 6), new Wear.Shoes("#1c1c1c"), new Wear.Apron("#e8e4da", 50, true),
                new Wear.ShortHair("#1c1c1c", 1)));
    }

    /** At a stall, buying: dark all over. */
    public static Person customer() {
        return new Person("Customer", body(176, 1), skin(3), outfit("Customer",
                new Wear.Trousers("#18191b", 1.4, 0.95), new Wear.Jacket("#18191b", 2.4, 8),
                new Wear.Shoes("#121212"), new Wear.ShortHair("#121212", 1)));
    }

    /** Riding: a jacket, trousers, boots and a helmet. */
    public static Person rider() {
        return new Person("Rider", body(172, 1.05), skin(2), outfit("Rider",
                new Wear.Trousers("#2f4560", 1.6, 0.95), new Wear.Jacket("#5a3d2b", 2.6, 4),
                new Wear.Boots("#2a1d14", 0.4), new Wear.ShortHair("#2a1d14", 1), new Wear.Helmet("#d0d4d8")));
    }

    /** The office's scrum master: an orange hoodie, a red cap, a badge on a lanyard. */
    public static Person scrumMaster() {
        return new Person("Scrum master", body(168, 1.05), skin(4), outfit("Scrum master",
                new Wear.Trousers("#2b3a55", 1.4, 0.95), new Wear.Hoodie("#f07a1a", 1.8),
                new Wear.Trainers("#e9e4d8", "#f7f7f5"), new Wear.ShortHair("#3b2a1f", 1), new Wear.Cap("#c8102e"),
                new Wear.Badge("#ffffff", "#c8102e")));
    }

    /** The office's {@code n}th developer: a hoodie of their own, dark trousers, trainers. */
    public static Person developer(final int n) {
        return new Person("Developer " + (n + 1), nth(n + 3), skin(n + 2), outfit("Developer",
                new Wear.Trousers("#2e3440", 1.4, 0.95), new Wear.Hoodie(HOODIES[n % HOODIES.length], 1.8),
                new Wear.Trainers("#e9e4d8", "#f7f7f5"), hair(n, HAIR[n % HAIR.length])));
    }

    /** Behind a bar: a white shirt, black trousers, a black apron. */
    public static Person bartender() {
        return new Person("Bartender", body(178, 1), skin(5), outfit("Bartender",
                new Wear.Shirt("#f1f0ea", 0.8, true), new Wear.Trousers("#1d1f22", 1.4, 0.95),
                new Wear.Shoes("#121212"), new Wear.Apron("#1d1f22", 55, true), new Wear.ShortHair("#2a1d14", 1)));
    }

    /** The {@code n}th at a bar: a shirt of their own, trousers. */
    public static Person drinker(final int n) {
        return new Person("At the bar " + (n + 1), nth(n + 7), skin(n + 4), outfit("At a bar",
                new Wear.Shirt(SHIRTS[n % SHIRTS.length], 0.9, n % 2 == 1), new Wear.Trousers("#2e3440", 1.4, 0.95),
                new Wear.Shoes("#262321"), hair(n + 2, HAIR[(n + 2) % HAIR.length])));
    }

    /** The {@code n}th guest out to enjoy a place: a short-sleeved shirt, shorts, trainers. */
    public static Person guest(final int n) {
        return new Person("Guest " + (n + 1), nth(n + 11), skin(n), outfit("Guest",
                new Wear.Shirt(SHIRTS[(n + 3) % SHIRTS.length], 0.9, false),
                new Wear.Shorts(SHORTS[n % SHORTS.length], 1.4, 0.45), new Wear.Trainers("#e9e4d8", "#f7f7f5"),
                hair(n + 1, HAIR[(n + 1) % HAIR.length])));
    }

    /** The {@code n}th player of the home side, in red, or the away side, in blue: a jersey, white shorts, socks. */
    public static Person player(final boolean home, final int n) {
        final String side = home ? "#c0392b" : "#2a6fb0";
        return new Person((home ? "Home " : "Away ") + "player " + (n + 1), nth(n + (home ? 17 : 23)),
                skin(n + (home ? 1 : 4)), outfit(home ? "Home side" : "Away side",
                new Wear.Jersey(side, "#f1f0ea", 1), new Wear.Shorts("#f1f0ea", 1.2, 0.32),
                new Wear.Socks(side, 0.65), new Wear.Trainers("#1c1c1c", "#f7f7f5"),
                new Wear.ShortHair(HAIR[(n + (home ? 0 : 3)) % HAIR.length], 0.8)));
    }

    /** Everyone of the world as it is, a few of each kind that varies. */
    public static List<Person> all() {
        final List<Person> all = new ArrayList<>(List.of(stallholder(), customer(), rider(), scrumMaster()));
        for (int n = 0; n < HOODIES.length; n++) {
            all.add(developer(n));
        }
        all.addAll(Person.TEMPLE);
        all.add(bartender());
        for (int n = 0; n < 3; n++) {
            all.add(drinker(n));
        }
        for (int n = 0; n < 3; n++) {
            all.add(guest(n));
        }
        for (int n = 0; n < 2; n++) {
            all.add(player(true, n));
            all.add(player(false, n));
        }
        return all;
    }
}
