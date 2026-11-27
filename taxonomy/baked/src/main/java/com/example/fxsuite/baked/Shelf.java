package com.example.fxsuite.baked;

import java.util.List;
import java.util.Optional;

/**
 * Where things made are kept, to be taken again rather than made afresh: one way or another of keeping them, on disk
 * as the trees are laid out ({@link TreeShelf}) to begin with.
 */
public interface Shelf {

    /**
     * What was kept as {@code asked}: none if nothing was, if what was kept was made by other code, or if it cannot be
     * read whole.
     */
    Optional<List<Piece>> taken(Asked asked);

    /** {@code pieces} kept as {@code asked}, in place of whatever was kept as it before; whether they could be. */
    boolean put(Asked asked, List<Piece> pieces);
}
