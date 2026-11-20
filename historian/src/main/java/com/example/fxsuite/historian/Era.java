package com.example.fxsuite.historian;

import java.time.Year;

/**
 * An era of a civilization: the years it ran, and what it was called. It belongs to one civilization, its type's
 * argument, or to the root ({@link Civilization#ROOT}) for an era everyone shares; it may be a part of a longer one of
 * the same civilization ({@link #within}). Each is a kind, not an item: one record of it, its years fixed in its class.
 *
 * @param <C> the civilization whose era it is
 */
public interface Era<C extends Civilization> {

    /** What it is called. */
    String name();

    /** When it began. */
    Year from();

    /** When it ended; {@link Years#ONGOING} if it has not. */
    Year to();

    /** Whose era it is. */
    C civilization();

    /** The longer era of the same civilization it is a part of, if it is one; none. */
    default Era<?> within() {
        return null;
    }

    /** Whether {@code year} falls in it. */
    default boolean spans(final Year year) {
        return !year.isBefore(from()) && !year.isAfter(to());
    }

    /** Whether it and {@code other} ran at the same time, a year of each in common. */
    default boolean overlaps(final Era<?> other) {
        return !to().isBefore(other.from()) && !other.to().isBefore(from());
    }
}
