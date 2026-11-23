package melons.innocent.terrain.tiles;

import hue.captains.singapura.tao.ontology.Scalar;
import melons.innocent.terrain.Extent;

/**
 * How a map is cut into tiles: one square over the whole of it, from ({@code x0}, {@code z0}) {@code side}
 * centimetres each way, halved each way at each level down; each tile so many {@code cells} to a side, whatever its
 * level, so a tile a level down has its points half as far apart. The cells are even in number, so a tile's every
 * other point along an edge lies where the points of a tile a level up lie.
 *
 * @param x0    where the square over the map begins across
 * @param z0    where it begins along
 * @param side  how far it goes each way, in centimetres
 * @param cells how many cells each tile has to a side: even, two at least
 */
public record Tiling(double x0, double z0, double side, int cells) implements Scalar {

    public Tiling {
        if (!(side > 0) || cells < 2 || cells % 2 != 0) {
            throw new IllegalArgumentException("a square of some size, cut into an even number of cells: " + side
                    + ", " + cells);
        }
    }

    /** The tiling of {@code extent}: a square from its near left corner as big as its longer way. */
    public Tiling(final Extent extent, final int cells) {
        this(extent.x0(), extent.z0(), Math.max(extent.across(), extent.along()), cells);
    }

    /** How far a tile of level {@code level} goes each way. */
    public double side(final int level) {
        return side / (1L << level);
    }

    /** How far apart a tile of level {@code level} has its points. */
    public double spacing(final int level) {
        return side(level) / cells;
    }

    /** Where {@code key} begins across. */
    public double x0(final TileKey key) {
        return x0 + key.i() * side(key.level());
    }

    /** Where {@code key} begins along. */
    public double z0(final TileKey key) {
        return z0 + key.j() * side(key.level());
    }

    /** How far ({@code x}, {@code z}) is from {@code key}'s square: nothing within it. */
    public double distance(final TileKey key, final double x, final double z) {
        final double side = side(key.level());
        final double across = Math.max(0, Math.max(x0(key) - x, x - x0(key) - side));
        final double along = Math.max(0, Math.max(z0(key) - z, z - z0(key) - side));
        return Math.hypot(across, along);
    }

    /** The highest level whose tiles have their points no further apart than {@code spacing}, or the deepest. */
    public int level(final double spacing) {
        int level = 0;
        while (spacing(level) > spacing && level < TileKey.DEEPEST) {
            level++;
        }
        return level;
    }
}
