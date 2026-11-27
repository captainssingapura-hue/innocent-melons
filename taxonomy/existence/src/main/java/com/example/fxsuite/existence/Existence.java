package com.example.fxsuite.existence;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists, whoever built it and whenever: the nine trees the worlds need so far. The land's base terrain
 * ({@link Terrains}) and its features ({@link Features}); what is made on it ({@link Made}); what storeys and spaces
 * are for ({@link Uses}); the spaces buildings are made of ({@link Spaces}); the elements they are built of
 * ({@link Elements}); what lies about and is handled ({@link Things}); what is worn ({@link Wearables}); bodies that
 * move themselves ({@link Figures}). Whose a thing is and when is the historian's.
 */
public record Existence() implements StatelessFunctionalObject {

    public static final Existence INSTANCE = new Existence();

    /** The nine trees. */
    public List<Taxonomy> trees() {
        return List.of(Terrains.TREE, Features.TREE, Made.TREE, Uses.TREE, Spaces.TREE, Elements.TREE, Things.TREE,
                Wearables.TREE, Figures.TREE);
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(p -> tree.name() + ": " + p)).toList();
    }
}
