package com.example.fxsuite.math.existences;

import com.example.fxsuite.taxonomy.Taxonomy;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * What exists in mathematics, as far as the worlds need it: the one tree so far, the kinds of curve
 * ({@link Curves}). Abstract, timeless and made by no civilization; when one came to know of each is the historian's.
 */
public record Existences() implements StatelessFunctionalObject {

    public static final Existences INSTANCE = new Existences();

    /** The trees. */
    public List<Taxonomy> trees() {
        return List.of(Curves.TREE);
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(p -> tree.name() + ": " + p)).toList();
    }
}
