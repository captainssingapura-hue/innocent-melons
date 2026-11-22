package melons.innocent.terrain.tiles;

import hue.captains.singapura.tao.ontology.Scalar;

/**
 * A tile chosen to be shown, and which of its edges meet a tile a level coarser: there its every other point is to
 * be laid on the line between the two either side of it, as the coarser tile has its edge, so no crack shows.
 *
 * @param key     the tile
 * @param coarser which of its edges meet a coarser tile, one bit to each: {@link #WEST}, {@link #EAST}, {@link #NEAR},
 *                {@link #FAR}
 */
public record Chosen(TileKey key, int coarser) implements Scalar {

    /** Its edge furthest left, across. */
    public static final int WEST = 1;
    /** Its edge furthest right. */
    public static final int EAST = 2;
    /** Its nearest edge, along. */
    public static final int NEAR = 4;
    /** Its furthest edge. */
    public static final int FAR = 8;

    /** Whether its edge {@code edge} meets a coarser tile. */
    public boolean coarserAt(final int edge) {
        return (coarser & edge) != 0;
    }
}
