package com.example.fxsuite.existence;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists, whoever built it and whenever: the four trees the worlds need so far. The kinds of building
 * ({@link Buildings}); what their storeys and spaces are for ({@link Uses}); the spaces they are made of
 * ({@link Spaces}); the elements they are built of ({@link Elements}). Whose a thing is and when is the historian's.
 */
public record Existence() implements StatelessFunctionalObject {

    public static final Existence INSTANCE = new Existence();

    /** The four trees. */
    public List<Taxonomy> trees() {
        return List.of(Buildings.TREE, Uses.TREE, Spaces.TREE, Elements.TREE);
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(p -> tree.name() + ": " + p)).toList();
    }
}
