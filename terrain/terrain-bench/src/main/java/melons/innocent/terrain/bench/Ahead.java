package melons.innocent.terrain.bench;

import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.Choice;
import melons.innocent.terrain.tiles.TileKey;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The tiles wanted ahead of the camera: for each spot it is to come to, in the order it comes to them, the tiles that
 * would be shown around it there, each nearest first, but none already shown. Built against its coming, not shown
 * until it does.
 *
 * @param choice which tiles are shown around a spot
 */
public record Ahead(Choice choice) {

    /** The tiles wanted around each of {@code spots}, across and along, in turn, but none of {@code shown}. */
    public List<TileKey> keys(final List<double[]> spots, final List<Chosen> shown) {
        final Set<TileKey> already = new LinkedHashSet<>();
        shown.forEach(tile -> already.add(tile.key()));
        final List<TileKey> keys = new ArrayList<>();
        for (final double[] spot : spots) {
            for (final Chosen tile : choice.around(spot[0], spot[1])) {
                if (already.add(tile.key())) {
                    keys.add(tile.key());
                }
            }
        }
        return List.copyOf(keys);
    }
}
