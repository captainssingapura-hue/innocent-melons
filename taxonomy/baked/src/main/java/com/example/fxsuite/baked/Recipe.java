package com.example.fxsuite.baked;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;
import java.util.function.Consumer;

/**
 * How a leaf of a tree is made, at each resolution it may be made at; which of those are dear to make, so kept once
 * made and taken again ({@link Baker}), and which cheap enough to make each time.
 */
public interface Recipe {

    /** The tree the leaf is of. */
    Taxonomy tree();

    /** What it makes. */
    Leaf<?> leaf();

    /** The resolutions it may be made at. */
    List<Resolution> offered();

    /** Whether making it at {@code resolution} is dear enough to keep what is made. */
    boolean dear(Resolution resolution);

    /**
     * What makes it as it is now: the code and all it is made from, as a word that changes when they do
     * ({@link Fingerprint}); so a thing kept from before they changed is not taken.
     */
    String madeBy();

    /** It made at {@code resolution}, each step told to {@code told} as it begins. */
    List<Piece> made(Resolution resolution, Consumer<String> told);
}
