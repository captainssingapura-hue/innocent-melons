package com.example.fxsuite.historian;

/**
 * Whose a thing is and when: a civilization, and an era of its own lineage. That the era is its civilization's, an
 * ancestor's or everyone's is checked by the compiler ({@code E extends Era<? super C>}); that the civilization was
 * there in the era's years is checked here: a Straits Chinese thing in the Northern Song is refused.
 *
 * @param civilization whose it is
 * @param era          when it is from
 * @param <C>          its civilization's kind
 * @param <E>          its era's kind
 */
public record Provenance<C extends Civilization, E extends Era<? super C>>(C civilization, E era) {

    public Provenance {
        if (civilization == null || era == null) {
            throw new IllegalArgumentException("a provenance is a civilization and an era");
        }
        if (civilization.to().isBefore(era.from()) || era.to().isBefore(civilization.from())) {
            throw new IllegalArgumentException(civilization.name() + " was not there in the " + era.name() + " ("
                    + Years.INSTANCE.said(era.from()) + " to " + Years.INSTANCE.said(era.to()) + ")");
        }
    }
}
