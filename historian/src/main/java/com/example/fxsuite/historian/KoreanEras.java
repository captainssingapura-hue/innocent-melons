package com.example.fxsuite.historian;

import java.time.Year;
import java.util.List;

/**
 * The eras of the Koreans. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public final class KoreanEras {

    private KoreanEras() {
    }

    /** All of them, oldest first. */
    public static final List<Era<?>> ALL = List.of(new ThreeKingdoms(), new UnifiedSilla(), new Goryeo(), new Joseon());

    /**
     * The Three Kingdoms: Goguryeo, Baekje and Silla.
     */
    public record ThreeKingdoms() implements Era<Korean> {
        public String name() { return "Three Kingdoms"; }
        public Year from() { return Years.bce(57); }
        public Year to() { return Years.ce(668); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Unified Silla: its capital at Gyeongju; Bulguksa.
     */
    public record UnifiedSilla() implements Era<Korean> {
        public String name() { return "Unified Silla"; }
        public Year from() { return Years.ce(668); }
        public Year to() { return Years.ce(935); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Goryeo: celadon, the Tripitaka carved.
     */
    public record Goryeo() implements Era<Korean> {
        public String name() { return "Goryeo"; }
        public Year from() { return Years.ce(918); }
        public Year to() { return Years.ce(1392); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Joseon: Gyeongbokgung, hanok houses.
     */
    public record Joseon() implements Era<Korean> {
        public String name() { return "Joseon"; }
        public Year from() { return Years.ce(1392); }
        public Year to() { return Years.ce(1897); }
        public Korean civilization() { return Korean.IT; }
    }
}
