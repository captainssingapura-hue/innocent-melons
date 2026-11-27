package com.example.fxsuite.baked;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Things made as their recipes say ({@link Recipe}), looking on a shelf first ({@link Shelf}): one dear to make taken
 * from the shelf if it is kept there, made by the code as it is now, else made and then kept; one cheap made each
 * time and never kept. And things baked ahead: every dear resolution of a recipe made and kept, if not kept already.
 */
public final class Baker {

    private final Shelf shelf;

    /** A baker keeping what is dear on {@code shelf}. */
    public Baker(final Shelf shelf) {
        this.shelf = shelf;
    }

    /**
     * What {@code recipe} makes, at {@code resolution}: taken from the shelf if it is dear and kept, else made, each
     * step told to {@code told}, and kept if dear.
     */
    public List<Piece> pieces(final Recipe recipe, final Resolution resolution, final Consumer<String> told) {
        if (!recipe.offered().contains(resolution)) {
            throw new IllegalArgumentException(recipe.leaf().title() + " is not made " + resolution.named());
        }
        if (!recipe.dear(resolution)) {
            return recipe.made(resolution, told);
        }
        final Asked asked = new Asked(recipe.tree(), recipe.leaf(), resolution, recipe.madeBy());
        final Optional<List<Piece>> kept = shelf.taken(asked);
        if (kept.isPresent()) {
            told.accept(recipe.leaf().title() + ", " + resolution.named() + ", as baked");
            return kept.get();
        }
        final List<Piece> made = recipe.made(resolution, told);
        told.accept(recipe.leaf().title() + ", " + resolution.named() + (shelf.put(asked, made) ? ", baked"
                : ", made but not kept"));
        return made;
    }

    /**
     * Every resolution of {@code recipe} dear to make, made and kept if it is not kept already, each step told to
     * {@code told}; how many were made.
     */
    public int baked(final Recipe recipe, final Consumer<String> told) {
        int made = 0;
        for (final Resolution r : recipe.offered()) {
            if (recipe.dear(r) && shelf.taken(new Asked(recipe.tree(), recipe.leaf(), r, recipe.madeBy())).isEmpty()) {
                pieces(recipe, r, told);
                made++;
            }
        }
        return made;
    }
}
