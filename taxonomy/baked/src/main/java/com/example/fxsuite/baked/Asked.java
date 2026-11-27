package com.example.fxsuite.baked;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

/**
 * A thing asked for: a leaf of a tree, made as finely as asked, by the code that makes it as that code is now. A thing
 * kept is taken for what is asked only if it was kept as all of these.
 *
 * @param tree       the tree the leaf is of
 * @param leaf       what it is
 * @param resolution how finely it is made
 * @param madeBy     what makes it as it is now ({@link Recipe#madeBy})
 */
public record Asked(Taxonomy tree, Leaf<?> leaf, Resolution resolution, String madeBy) {

    /** A leaf of its tree, made by something said. */
    public Asked {
        if (!tree.has(leaf)) {
            throw new IllegalArgumentException(leaf.title() + " is no leaf of " + tree.name());
        }
        if (madeBy.isBlank()) {
            throw new IllegalArgumentException("what makes " + leaf.title() + " is not said");
        }
    }
}
