package com.example.fxsuite.taxonomy;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * A node of a taxonomy: a kind of thing, written as a type. A stateless record, one of it; its type is its meaning,
 * so no node is a string, and every use of it is found by the compiler. Its place in its tree is in its declaration
 * ({@link L0}, {@link L1}, {@link L2}, {@link L3}, {@link Leaf}); what it means is written beside its tree
 * ({@link Meanings}).
 */
public interface Node {

    /** How deep a lineage is followed, at most: past that, a loop. */
    int DEEPEST = 16;

    /** What it is called in its meanings: its class's own name, {@code HotelRooms}. */
    default String title() {
        return getClass().getSimpleName();
    }

    /** Its name, from its class: {@code HotelRooms} is {@code hotel-rooms}. */
    default String name() {
        return title().replaceAll("([a-z0-9])([A-Z])", "$1-$2").toLowerCase(java.util.Locale.ROOT);
    }

    /** It and its ancestors, from it up to its tree's root: where its lineage ends if a parent is missing or a loop. */
    default List<Node> lineage() {
        final List<Node> lineage = new ArrayList<>(List.of(this));
        Node at = this;
        while (at instanceof Child<?> child && lineage.size() < DEEPEST) {
            at = child.parent();
            if (at == null) {
                break;
            }
            lineage.add(at);
        }
        return lineage;
    }

    /**
     * The answer its lineage gives as {@code kind}: the nearest of it and its ancestors that is one, a trait such as
     * {@code Occupied} said once on a branch and so true of all under it; none if none is.
     */
    default <A> Optional<A> answer(final Class<A> kind) {
        return lineage().stream().filter(kind::isInstance).map(kind::cast).findFirst();
    }
}
