package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras of the British, by their reigns, as their buildings are known. Their years as historians commonly give them;
 * each era a record, its years fixed in its class.
 */
public final class BritishEras {

    private BritishEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new Georgian(), new Victorian(), new Edwardian());

    /**
     * Georgian: brick terraces, sash windows, the Straits' first colonial buildings.
     */
    public record Georgian() implements Era<British> {
        public String name() { return "Georgian"; }
        public Year from() { return Years.ce(1714); }
        public Year to() { return Years.ce(1837); }
        public British civilization() { return British.IT; }
    }

    /**
     * Victorian: iron and glass, revived styles; much of colonial Singapore.
     */
    public record Victorian() implements Era<British> {
        public String name() { return "Victorian"; }
        public Year from() { return Years.ce(1837); }
        public Year to() { return Years.ce(1901); }
        public British civilization() { return British.IT; }
    }

    /**
     * Edwardian: Baroque revived, lighter; the Straits' grander civic buildings.
     */
    public record Edwardian() implements Era<British> {
        public String name() { return "Edwardian"; }
        public Year from() { return Years.ce(1901); }
        public Year to() { return Years.ce(1914); }
        public British civilization() { return British.IT; }
    }
}
