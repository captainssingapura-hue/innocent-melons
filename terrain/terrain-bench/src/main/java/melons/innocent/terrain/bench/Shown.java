package melons.innocent.terrain.bench;

import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The tiles to be shown, nearest first, filed so what of them lies over any tile's ground is found at once: each under
 * every tile its ground lies within, at every level up, and by itself. A tile's ground and a shown one's overlap when
 * one lies within the other.
 */
final class Shown {

    private final List<Chosen> tiles;
    private final Set<Chosen> has;
    private final Map<TileKey, Chosen> byKey = new HashMap<>();
    private final Map<TileKey, List<Chosen>> under = new HashMap<>();

    /** {@code tiles}, nearest first. */
    Shown(final List<Chosen> tiles) {
        this.tiles = List.copyOf(tiles);
        this.has = Set.copyOf(this.tiles);
        for (final Chosen tile : this.tiles) {
            byKey.put(tile.key(), tile);
            TileKey up = tile.key();
            while (true) {
                under.computeIfAbsent(up, key -> new ArrayList<>()).add(tile);
                if (up.level() == 0) {
                    break;
                }
                up = up.parent();
            }
        }
    }

    /** They, nearest first. */
    List<Chosen> tiles() {
        return tiles;
    }

    /** Whether {@code tile} is among them, its edges as they are. */
    boolean has(final Chosen tile) {
        return has.contains(tile);
    }

    /** Those of them whose ground overlaps {@code key}'s: within it, it itself, or round it. */
    List<Chosen> over(final TileKey key) {
        final List<Chosen> over = new ArrayList<>(under.getOrDefault(key, List.of()));
        TileKey up = key;
        while (up.level() > 0) {
            up = up.parent();
            final Chosen round = byKey.get(up);
            if (round != null) {
                over.add(round);
            }
        }
        return over;
    }
}
