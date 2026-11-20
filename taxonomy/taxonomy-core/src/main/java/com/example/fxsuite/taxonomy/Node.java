package com.example.fxsuite.taxonomy;

/**
 * A node of a taxonomy: a kind of thing, written as a type. A stateless record, one of it; its type is its meaning,
 * so no node is a string, and every use of it is found by the compiler. Its place in its tree is in its declaration
 * ({@link L0}, {@link L1}, {@link L2}, {@link L3}, {@link Leaf}); what it means is written beside its tree
 * ({@link Meanings}).
 */
public interface Node {

    /** What it is called in its meanings: its class's own name, {@code HotelRooms}. */
    default String title() {
        return getClass().getSimpleName();
    }

    /** Its name, from its class: {@code HotelRooms} is {@code hotel-rooms}. */
    default String name() {
        return title().replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase(java.util.Locale.ROOT);
    }
}
