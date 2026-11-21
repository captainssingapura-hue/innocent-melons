package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the Indians. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public record IndianEras() implements StatelessFunctionalObject {

    public static final IndianEras INSTANCE = new IndianEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new IndusValley(), new Maurya(), new Gupta(), new Chola(),
                new Mughal());
    }

    /**
     * The Indus Valley: Harappa and Mohenjo-daro, cities of fired brick on grids.
     */
    public record IndusValley() implements Era<Indian> {
        public String name() { return "Indus Valley"; }
        public Year from() { return Years.INSTANCE.bce(3300); }
        public Year to() { return Years.INSTANCE.bce(1300); }
        public Indian civilization() { return Indian.IT; }
    }

    /**
     * The Maurya: Ashoka's empire; the first stupas.
     */
    public record Maurya() implements Era<Indian> {
        public String name() { return "Maurya"; }
        public Year from() { return Years.INSTANCE.bce(322); }
        public Year to() { return Years.INSTANCE.bce(185); }
        public Indian civilization() { return Indian.IT; }
    }

    /**
     * The Gupta: the classical age; the first stone temples.
     */
    public record Gupta() implements Era<Indian> {
        public String name() { return "Gupta"; }
        public Year from() { return Years.INSTANCE.ce(320); }
        public Year to() { return Years.INSTANCE.ce(550); }
        public Indian civilization() { return Indian.IT; }
    }

    /**
     * The Chola: the south's great temples, Thanjavur's.
     */
    public record Chola() implements Era<Indian> {
        public String name() { return "Chola"; }
        public Year from() { return Years.INSTANCE.ce(848); }
        public Year to() { return Years.INSTANCE.ce(1279); }
        public Indian civilization() { return Indian.IT; }
    }

    /**
     * The Mughal: domes and gardens; the Taj Mahal.
     */
    public record Mughal() implements Era<Indian> {
        public String name() { return "Mughal"; }
        public Year from() { return Years.INSTANCE.ce(1526); }
        public Year to() { return Years.INSTANCE.ce(1857); }
        public Indian civilization() { return Indian.IT; }
    }
}
