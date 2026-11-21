package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the ancient Egyptians, by their kingdoms. Their years as historians commonly give them; each era a
 * record, its years fixed in its class.
 */
public record EgyptianEras() implements StatelessFunctionalObject {

    public static final EgyptianEras INSTANCE = new EgyptianEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new OldKingdom(), new MiddleKingdom(), new NewKingdom(),
                new Ptolemaic());
    }

    /**
     * The Old Kingdom: the pyramids of Giza.
     */
    public record OldKingdom() implements Era<Egyptian> {
        public String name() { return "Old Kingdom"; }
        public Year from() { return Years.INSTANCE.bce(2686); }
        public Year to() { return Years.INSTANCE.bce(2181); }
        public Egyptian civilization() { return Egyptian.IT; }
    }

    /**
     * The Middle Kingdom: Egypt reunited; rock-cut tombs.
     */
    public record MiddleKingdom() implements Era<Egyptian> {
        public String name() { return "Middle Kingdom"; }
        public Year from() { return Years.INSTANCE.bce(2055); }
        public Year to() { return Years.INSTANCE.bce(1650); }
        public Egyptian civilization() { return Egyptian.IT; }
    }

    /**
     * The New Kingdom: Karnak and Luxor, the Valley of the Kings.
     */
    public record NewKingdom() implements Era<Egyptian> {
        public String name() { return "New Kingdom"; }
        public Year from() { return Years.INSTANCE.bce(1550); }
        public Year to() { return Years.INSTANCE.bce(1069); }
        public Egyptian civilization() { return Egyptian.IT; }
    }

    /**
     * The Ptolemaic: Alexandria; Greek kings in the pharaohs' ways.
     */
    public record Ptolemaic() implements Era<Egyptian> {
        public String name() { return "Ptolemaic"; }
        public Year from() { return Years.INSTANCE.bce(305); }
        public Year to() { return Years.INSTANCE.bce(30); }
        public Egyptian civilization() { return Egyptian.IT; }
    }
}
