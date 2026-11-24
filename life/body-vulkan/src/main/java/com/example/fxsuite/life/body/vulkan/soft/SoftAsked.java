package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.soft.SoftRegion;

/**
 * What the soft bench on the card is asked to show, as its JavaFX window says it on the bench's bus: the region to be
 * simulated, how much of a muscle must be in it to be taken in, how big the cubes of its elements are; whether the
 * muscles held round it are shown, the bones, the taken muscles' surfaces, and their elements; and whether as
 * solved. Whole, never changed.
 *
 * @param lo       the region's lowest corner, cm, the body's frame
 * @param hi       its highest
 * @param share    how much of a muscle's points must be in the region for it to be taken in, 0 to 1
 * @param cell     how big each cube of the elements is, cm
 * @param held     whether the muscles held round it are shown
 * @param bones    whether the bones are shown
 * @param surfaces whether the taken muscles' surfaces are shown
 * @param elements whether their elements are shown
 * @param solved   whether the muscles are shown as solved at rest, where they have been
 * @param fascia   whether the fascia wrapping them is shown, where there is one
 * @param onePiece whether the region's soft tissue is shown as one piece, each of its cells given to one tissue
 */
public record SoftAsked(V3 lo, V3 hi, double share, double cell, boolean held, boolean bones, boolean surfaces,
                        boolean elements, boolean solved, boolean fascia, boolean onePiece) {

    /** The region. */
    public SoftRegion region() {
        return new SoftRegion(lo, hi);
    }

    /** Whether {@code other} asks for the same region, share, cubes and way: whether it is to be found afresh. */
    public boolean sameRegion(final SoftAsked other) {
        return other != null && lo.equals(other.lo) && hi.equals(other.hi) && share == other.share
                && cell == other.cell && onePiece == other.onePiece;
    }
}
