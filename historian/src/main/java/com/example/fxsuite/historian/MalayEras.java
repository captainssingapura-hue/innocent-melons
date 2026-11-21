package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of the Malays. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public record MalayEras() implements StatelessFunctionalObject {

    public static final MalayEras INSTANCE = new MalayEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new Srivijaya(), new MalaccaSultanate());
    }

    /**
     * Srivijaya: the sea empire of the straits.
     */
    public record Srivijaya() implements Era<Malay> {
        public String name() { return "Srivijaya"; }
        public Year from() { return Years.INSTANCE.ce(650); }
        public Year to() { return Years.INSTANCE.ce(1275); }
        public Malay civilization() { return Malay.IT; }
    }

    /**
     * The Malacca Sultanate: the straits' great port, until the Portuguese took it.
     */
    public record MalaccaSultanate() implements Era<Malay> {
        public String name() { return "Malacca Sultanate"; }
        public Year from() { return Years.INSTANCE.ce(1400); }
        public Year to() { return Years.INSTANCE.ce(1511); }
        public Malay civilization() { return Malay.IT; }
    }
}
