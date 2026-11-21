package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the Koreans. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public record KoreanEras() implements StatelessFunctionalObject {

    public static final KoreanEras INSTANCE = new KoreanEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new ThreeKingdoms(), new UnifiedSilla(), new Goryeo(), new Joseon());
    }

    /**
     * The Three Kingdoms: Goguryeo, Baekje and Silla.
     */
    public record ThreeKingdoms() implements Era<Korean> {
        public String name() { return "Three Kingdoms"; }
        public Year from() { return Years.INSTANCE.bce(57); }
        public Year to() { return Years.INSTANCE.ce(668); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Unified Silla: its capital at Gyeongju; Bulguksa.
     */
    public record UnifiedSilla() implements Era<Korean> {
        public String name() { return "Unified Silla"; }
        public Year from() { return Years.INSTANCE.ce(668); }
        public Year to() { return Years.INSTANCE.ce(935); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Goryeo: celadon, the Tripitaka carved.
     */
    public record Goryeo() implements Era<Korean> {
        public String name() { return "Goryeo"; }
        public Year from() { return Years.INSTANCE.ce(918); }
        public Year to() { return Years.INSTANCE.ce(1392); }
        public Korean civilization() { return Korean.IT; }
    }

    /**
     * Joseon: Gyeongbokgung, hanok houses.
     */
    public record Joseon() implements Era<Korean> {
        public String name() { return "Joseon"; }
        public Year from() { return Years.INSTANCE.ce(1392); }
        public Year to() { return Years.INSTANCE.ce(1897); }
        public Korean civilization() { return Korean.IT; }
    }
}
