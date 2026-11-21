package com.example.fxsuite.life.body.muscle;

/**
 * How much muscle a body has, muscle by muscle: each one's volume, the size of its belly, its tendons as they are. The
 * one thing about a muscle that differs from body to body; where it lies, how its belly is shaped and how long its
 * tendons are being its anatomy's ({@link Muscle}). A muscle's sections, as written, are its size as drawn: so much
 * volume, from which a body is the same or more or less.
 */
public interface Muscularity {

    /** How many cm3 muscle {@code m} is on this body, its sections as written holding {@code asDrawn}, smoothed. */
    double volume(Muscle m, double asDrawn);
}
