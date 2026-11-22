package melons.innocent.terrain.tiles;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * Which tile: its level, nothing for the one tile over the whole map and each level down halving it each way, and
 * its place among the tiles of its level, so many across ({@code i}) and along ({@code j}) from the map's near left
 * corner.
 *
 * @param level how far down it is
 * @param i     how many tiles of its level across from the near left
 * @param j     how many along
 */
public record TileKey(int level, int i, int j) implements Scalar {

    /** How many levels down a tile may be, at most: more than a map the size of a continent needs. */
    public static final int DEEPEST = 30;

    public TileKey {
        if (level < 0 || level > DEEPEST || i < 0 || j < 0 || i >= 1 << level || j >= 1 << level) {
            throw new IllegalArgumentException("no tile " + i + ", " + j + " at level " + level);
        }
    }

    /** The tile over the whole map. */
    public TileKey() {
        this(0, 0, 0);
    }

    /** How many tiles of its level there are each way. */
    public int across() {
        return 1 << level;
    }

    /** Its four, a level down: near left, near right, far left, far right. */
    public List<TileKey> children() {
        return List.of(new TileKey(level + 1, 2 * i, 2 * j), new TileKey(level + 1, 2 * i + 1, 2 * j),
                new TileKey(level + 1, 2 * i, 2 * j + 1), new TileKey(level + 1, 2 * i + 1, 2 * j + 1));
    }

    /** The tile it is one of the four of, a level up; refused for the tile over the whole map. */
    public TileKey parent() {
        return new TileKey(level - 1, i / 2, j / 2);
    }

    /** Whether there is a tile of its level so many across and along from it, within the map. */
    public boolean beside(final int across, final int along) {
        final int ni = i + across;
        final int nj = j + along;
        return ni >= 0 && nj >= 0 && ni < across() && nj < across();
    }

    /** The tile of its level so many across and along from it, which there must be. */
    public TileKey moved(final int across, final int along) {
        return new TileKey(level, i + across, j + along);
    }
}
