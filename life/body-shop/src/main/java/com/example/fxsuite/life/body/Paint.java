package com.example.fxsuite.life.body;

/**
 * What a piece of a body is coloured as: its skin, its bones (its radius and its kneecaps apart, as the interactive
 * model shows them), its muscles at the front, at the back and deep; and the marks of its balance, its centre of mass
 * and the area its feet hold it up over, and where its centre of gravity is kept, if it is.
 */
public enum Paint {
    SKIN("#d3d1c7"),
    BONE("#b4b2a9"),
    RADIUS("#f0997b"),
    PATELLA("#d3d1c7"),
    MUSCLE_FRONT("#d85a30"),
    MUSCLE_BACK("#7f77dd"),
    MUSCLE_DEEP("#534ab7"),
    MARK_MASS("#e24b4a"),
    MARK_SUPPORT("#ba7517"),
    MARK_FALLING("#a32d2d"),
    MARK_TARGET("#1d9e75");

    private final String colour;

    Paint(final String colour) {
        this.colour = colour;
    }

    /** Its colour, as the interactive model has it. */
    public String colour() {
        return colour;
    }
}
