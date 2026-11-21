package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;

/**
 * Years as historians write them, made Java's: a year of the common era is itself; a year before it is counted as
 * Java counts it, with no year 0 between them in the historian's reckoning but year 0 in Java's (1 BCE is Java's year
 * 0, 2 BCE its year -1).
 */
public record Years() implements StatelessFunctionalObject {

    public static final Years INSTANCE = new Years();

    /** Still going: the furthest year Java counts. */
    public Year ongoing() {
        return Year.of(Year.MAX_VALUE);
    }

    /** The year {@code year} before the common era. */
    public Year bce(final int year) {
        if (year < 1) {
            throw new IllegalArgumentException("a year before the common era is 1 BCE or earlier, not " + year);
        }
        return Year.of(1 - year);
    }

    /** The year {@code year} of the common era. */
    public Year ce(final int year) {
        if (year < 1) {
            throw new IllegalArgumentException("a year of the common era is 1 CE or later, not " + year);
        }
        return Year.of(year);
    }

    /** {@code year} as a historian writes it: 221 BCE, 960, or now for one still going. */
    public String said(final Year year) {
        if (year.equals(ongoing())) {
            return "now";
        }
        return year.getValue() < 1 ? (1 - year.getValue()) + " BCE" : String.valueOf(year.getValue());
    }
}
