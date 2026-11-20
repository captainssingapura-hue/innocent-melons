package com.example.fxsuite.historian;

import java.time.Year;

/**
 * A civilization: a people's way of building, making and living, carried on through its eras ({@link Era}). Its
 * lineage is Java's own: a civilization's interface extends the one it grew from, so an era's civilization is checked
 * by the compiler ({@link Provenance}); and each says its parent too, to be walked ({@link Historian#lineage}), the
 * two checked to agree ({@link #includes}).
 *
 * <p>Each is a kind, not an item: one record of it, its {@code IT}, its years fixed in its class.
 */
public interface Civilization {

    /** What it is called. */
    String name();

    /** When it began, as near as it is known. */
    Year from();

    /** When it ended; {@link Years#ONGOING} if it has not. */
    Year to();

    /** What it grew from or within; the root its own. */
    Civilization parent();

    /**
     * Whether {@code c} is this civilization or one grown from it: written again by each civilization's interface, as
     * an {@code instanceof} of itself, so it says what Java's lineage says.
     */
    default boolean includes(final Civilization c) {
        return true;
    }

    /** Whether it was there in {@code year}. */
    default boolean during(final Year year) {
        return !year.isBefore(from()) && !year.isAfter(to());
    }

    /** Humankind: every civilization's root, from the first settled towns; its eras everyone's. */
    Root ROOT = new Root();

    record Root() implements Civilization {
        public String name() {
            return "humankind";
        }

        public Year from() {
            return Years.bce(10_000);
        }

        public Year to() {
            return Years.ONGOING;
        }

        public Civilization parent() {
            return this;
        }
    }
}
