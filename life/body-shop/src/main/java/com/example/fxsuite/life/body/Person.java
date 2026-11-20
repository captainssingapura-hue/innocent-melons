package com.example.fxsuite.life.body;

import java.util.List;

/**
 * Someone, declared: a body made as its proportions say ({@link Proportions}), the colour of its skin, and what it
 * wears ({@link Outfit}). Every one the same model, so whatever makes the model better makes every one better: to be
 * placed in a world by declaration.
 *
 * @param name   what it is called
 * @param body   how it is made
 * @param skin   its skin's colour
 * @param outfit what it wears
 */
public record Person(String name, Proportions body, String skin, Outfit outfit) {

    /** The base character: the papers' body, bare. */
    public static final Person BASE = new Person("Base", Proportions.PAPERS, "#d7a17a", Outfit.BARE);

    /** The temple's monks, as the game has them: a small one in red, a tall one in blue, a stout one in saffron. */
    public static final List<Person> TEMPLE = List.of(
            new Person("Small monk", body(162, 1), "#d7a17a", Outfit.monk("Monk, red", "#a3242b")),
            new Person("Tall monk", body(188, 1), "#c99670", Outfit.monk("Monk, blue", "#2e5a9e")),
            new Person("Stout monk", body(172, 1.3), "#b98762", Outfit.monk("Monk, saffron", "#d9a521")));

    /** The papers' body, so tall and so broad. */
    static Proportions body(final double tall, final double build) {
        final Proportions p = Proportions.PAPERS;
        return new Proportions(tall, build, p.legs(), p.arms(), p.shoulders(), p.hips());
    }

    public Person with(final Proportions made) {
        return new Person(name, made, skin, outfit);
    }

    public Person withSkin(final String colour) {
        return new Person(name, body, colour, outfit);
    }

    public Person with(final Outfit wearing) {
        return new Person(name, body, skin, wearing);
    }
}
