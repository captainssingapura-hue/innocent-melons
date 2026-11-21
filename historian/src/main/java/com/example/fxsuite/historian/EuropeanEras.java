package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the Europeans, by how they built. Their years as historians commonly give them; each era a record, its
 * years fixed in its class.
 */
public record EuropeanEras() implements StatelessFunctionalObject {

    public static final EuropeanEras INSTANCE = new EuropeanEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new AncientGreek(), new Roman(), new Byzantine(), new Romanesque(),
                new Gothic(), new Renaissance(), new Baroque(), new Neoclassical());
    }

    /**
     * Ancient Greek: temples of columns and their orders.
     */
    public record AncientGreek() implements Era<European> {
        public String name() { return "Ancient Greek"; }
        public Year from() { return Years.INSTANCE.bce(800); }
        public Year to() { return Years.INSTANCE.bce(146); }
        public European civilization() { return European.IT; }
    }

    /**
     * Roman: the arch, the vault, concrete; roads and aqueducts.
     */
    public record Roman() implements Era<European> {
        public String name() { return "Roman"; }
        public Year from() { return Years.INSTANCE.bce(509); }
        public Year to() { return Years.INSTANCE.ce(476); }
        public European civilization() { return European.IT; }
    }

    /**
     * Byzantine: Constantinople; domes on pendentives, Hagia Sophia.
     */
    public record Byzantine() implements Era<European> {
        public String name() { return "Byzantine"; }
        public Year from() { return Years.INSTANCE.ce(330); }
        public Year to() { return Years.INSTANCE.ce(1453); }
        public European civilization() { return European.IT; }
    }

    /**
     * Romanesque: round arches, thick walls, barrel vaults.
     */
    public record Romanesque() implements Era<European> {
        public String name() { return "Romanesque"; }
        public Year from() { return Years.INSTANCE.ce(1000); }
        public Year to() { return Years.INSTANCE.ce(1150); }
        public European civilization() { return European.IT; }
    }

    /**
     * Gothic: pointed arches, ribbed vaults, flying buttresses, great windows.
     */
    public record Gothic() implements Era<European> {
        public String name() { return "Gothic"; }
        public Year from() { return Years.INSTANCE.ce(1140); }
        public Year to() { return Years.INSTANCE.ce(1500); }
        public European civilization() { return European.IT; }
    }

    /**
     * Renaissance: the ancients' orders taken up again; domes and proportion.
     */
    public record Renaissance() implements Era<European> {
        public String name() { return "Renaissance"; }
        public Year from() { return Years.INSTANCE.ce(1400); }
        public Year to() { return Years.INSTANCE.ce(1600); }
        public European civilization() { return European.IT; }
    }

    /**
     * Baroque: curves, light and grandeur.
     */
    public record Baroque() implements Era<European> {
        public String name() { return "Baroque"; }
        public Year from() { return Years.INSTANCE.ce(1600); }
        public Year to() { return Years.INSTANCE.ce(1750); }
        public European civilization() { return European.IT; }
    }

    /**
     * Neoclassical: the ancients' temples again, plainer.
     */
    public record Neoclassical() implements Era<European> {
        public String name() { return "Neoclassical"; }
        public Year from() { return Years.INSTANCE.ce(1750); }
        public Year to() { return Years.INSTANCE.ce(1850); }
        public European civilization() { return European.IT; }
    }
}
