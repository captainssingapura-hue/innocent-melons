package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists, whoever built it and whenever: the four trees the worlds need so far. The kinds of building
 * ({@link Buildings}); what their storeys and spaces are for ({@link Uses}); the spaces they are made of
 * ({@link Spaces}); the elements they are built of ({@link Elements}). Whose a thing is and when is the historian's.
 */
public final class Existence {

    private Existence() {
    }

    /** The four trees. */
    public static final List<Taxonomy> TREES = List.of(Buildings.TREE, Uses.TREE, Spaces.TREE, Elements.TREE);

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public static List<String> problems() {
        return TREES.stream().flatMap(tree -> tree.problems().stream().map(p -> tree.name() + ": " + p)).toList();
    }
}
