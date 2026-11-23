package melons.innocent.terrain.tiles;

import hue.captains.singapura.tao.ontology.Scalar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Which tiles are wanted around a spot, the area that matters: from the tile over the whole map, each tile cut into
 * its four while the spot is nearer it than {@code near} times its side, and it is coarser than the {@code finest}
 * level; so fine near the spot and coarser further off, the whole map covered once. Then no tile is left beside one
 * more than a level finer: such a one is cut too, until none is. Each chosen tile is told which of its edges meet a
 * coarser tile. Nearest first.
 *
 * @param tiling how the map is cut into tiles
 * @param near   how many times its side a tile may be from the spot and still be cut into its four
 * @param finest the finest level a tile may be
 */
public record Choice(Tiling tiling, double near, int finest) implements Scalar {

    /** Each way across and along from a tile to the one beside it, with the bit for that edge. */
    private static final int[][] SIDES = {{-1, 0, Chosen.WEST}, {1, 0, Chosen.EAST}, {0, -1, Chosen.NEAR},
        {0, 1, Chosen.FAR}};

    public Choice {
        if (!(near > 0) || finest < 0 || finest > TileKey.DEEPEST) {
            throw new IllegalArgumentException("tiles cut some way off, to a level there is: " + near + ", " + finest);
        }
    }

    /** The tiles wanted around ({@code x}, {@code z}), nearest first. */
    public List<Chosen> around(final double x, final double z) {
        final Set<TileKey> leaves = new HashSet<>();
        cut(new TileKey(), x, z, leaves);
        balanced(leaves);
        final List<Chosen> chosen = new ArrayList<>();
        for (final TileKey leaf : leaves) {
            int coarser = 0;
            for (final int[] side : SIDES) {
                if (leaf.beside(side[0], side[1])) {
                    final TileKey over = covering(leaf.moved(side[0], side[1]), leaves);
                    if (over != null && over.level() < leaf.level()) {
                        coarser |= side[2];
                    }
                }
            }
            chosen.add(new Chosen(leaf, coarser));
        }
        chosen.sort(Comparator.comparingDouble((Chosen c) -> tiling.distance(c.key(), x, z))
                .thenComparingInt(c -> c.key().level()));
        return List.copyOf(chosen);
    }

    /** {@code key} kept whole, or cut into its four and each of them likewise, as near the spot as it is. */
    private void cut(final TileKey key, final double x, final double z, final Set<TileKey> leaves) {
        if (key.level() < finest && tiling.distance(key, x, z) < near * tiling.side(key.level())) {
            key.children().forEach(child -> cut(child, x, z, leaves));
        } else {
            leaves.add(key);
        }
    }

    /** {@code leaves} cut further until none is beside one more than a level finer than it. */
    private void balanced(final Set<TileKey> leaves) {
        boolean cutting = true;
        while (cutting) {
            cutting = false;
            for (final TileKey leaf : List.copyOf(leaves)) {
                for (final int[] side : SIDES) {
                    if (!leaf.beside(side[0], side[1])) {
                        continue;
                    }
                    final TileKey over = covering(leaf.moved(side[0], side[1]), leaves);
                    if (over != null && over.level() < leaf.level() - 1 && leaves.remove(over)) {
                        leaves.addAll(over.children());
                        cutting = true;
                    }
                }
            }
        }
    }

    /** The leaf that {@code key} is, or lies within; none if {@code key} is cut finer than it. */
    private TileKey covering(final TileKey key, final Set<TileKey> leaves) {
        TileKey at = key;
        while (true) {
            if (leaves.contains(at)) {
                return at;
            }
            if (at.level() == 0) {
                return null;
            }
            at = at.parent();
        }
    }
}
