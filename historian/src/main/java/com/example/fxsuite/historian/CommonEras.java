package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras every civilization shares, the root's: steel, concrete and glass built alike the world over. Their years as
 * historians commonly give them; each era a record, its years fixed in its class.
 */
public final class CommonEras {

    private CommonEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new Modern(), new Contemporary());

    /**
     * Modern: steel and concrete frames, the lift, the curtain wall; the same towers going up everywhere.
     */
    public record Modern() implements Era<Civilization> {
        public String name() { return "Modern"; }
        public Year from() { return Years.ce(1900); }
        public Year to() { return Years.ce(1989); }
        public Civilization civilization() { return Civilization.ROOT; }
    }

    /**
     * Contemporary: the towers of glass of today's cities, built alike everywhere, Marina Bay's among them.
     */
    public record Contemporary() implements Era<Civilization> {
        public String name() { return "Contemporary"; }
        public Year from() { return Years.ce(1990); }
        public Year to() { return Years.ONGOING; }
        public Civilization civilization() { return Civilization.ROOT; }
    }
}
