package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.List;

/**
 * The eras of Mesoamerica. Their years as historians commonly give them; each era a record, its years fixed in its
 * class.
 */
public record MesoamericanEras() implements StatelessFunctionalObject {

    public static final MesoamericanEras INSTANCE = new MesoamericanEras();

    /** All of them, oldest first. */
    public List<Era<?>> all() {
        return List.of(new Olmec(), new ClassicMaya(), new Aztec());
    }

    /**
     * Olmec: the first ceremonial centres, the colossal heads.
     */
    public record Olmec() implements Era<Mesoamerican> {
        public String name() { return "Olmec"; }
        public Year from() { return Years.INSTANCE.bce(1200); }
        public Year to() { return Years.INSTANCE.bce(400); }
        public Mesoamerican civilization() { return Mesoamerican.IT; }
    }

    /**
     * Classic Maya: Tikal and Palenque, stepped pyramids of stone.
     */
    public record ClassicMaya() implements Era<Mesoamerican> {
        public String name() { return "Classic Maya"; }
        public Year from() { return Years.INSTANCE.ce(250); }
        public Year to() { return Years.INSTANCE.ce(900); }
        public Mesoamerican civilization() { return Mesoamerican.IT; }
    }

    /**
     * Aztec: Tenochtitlan on its lake, until the Spanish took it.
     */
    public record Aztec() implements Era<Mesoamerican> {
        public String name() { return "Aztec"; }
        public Year from() { return Years.INSTANCE.ce(1325); }
        public Year to() { return Years.INSTANCE.ce(1521); }
        public Mesoamerican civilization() { return Mesoamerican.IT; }
    }
}
