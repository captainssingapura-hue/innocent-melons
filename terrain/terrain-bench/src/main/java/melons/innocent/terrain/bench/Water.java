package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileData;

import java.util.ArrayList;
import java.util.List;

/**
 * A tile's water as a level sheet: over every square of it where any corner's ground lies under the water's top,
 * the square laid at the water's top, facing straight up; a corner beyond the water's reach laid as high as the
 * corners within it, on the average. The sheet goes on past where the water meets the ground, so the ground rises
 * out of it just where it does, and the shore is where the two meet, as finely as they are drawn. Where an edge meets
 * a coarser tile, every other point along it is laid halfway between the two either side, as the ground is.
 *
 * @param tile   the tile
 * @param chosen which of its edges meet coarser tiles
 */
public record Water(TileData tile, Chosen chosen) {

    /** The sheet, its points in the tile's own frame, {@code up} times as high, where on the land in the map's. */
    public Mesh sheet(final double up) {
        final int n = tile.points();
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Vec3> ons = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>();
        final int[][] square = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        for (int j = 0; j + 1 < n; j++) {
            for (int i = 0; i + 1 < n; i++) {
                final double[] tops = new double[square.length];
                double sum = 0;
                int within = 0;
                boolean flooded = false;
                for (int k = 0; k < square.length; k++) {
                    tops[k] = top(i + square[k][0], j + square[k][1]);
                    if (!Double.isNaN(tops[k])) {
                        sum += tops[k];
                        within++;
                        flooded |= tops[k] > tile.height(i + square[k][0], j + square[k][1]);
                    }
                }
                if (flooded) {
                    final int base = points.size();
                    for (int k = 0; k < square.length; k++) {
                        final double top = Double.isNaN(tops[k]) ? sum / within : tops[k];
                        final int ci = i + square[k][0];
                        final int cj = j + square[k][1];
                        points.add(new Vec3(ci * tile.spacing(), top * up, cj * tile.spacing()));
                        facings.add(new Vec3(0, 1, 0));
                        ons.add(new Vec3(tile.x(ci), top, tile.z(cj)));
                    }
                    corners.addAll(List.of(base, base + 1, base + 2, base + 2, base + 1, base + 3));
                }
            }
        }
        return new Mesh(points, facings, corners, ons);
    }

    /** How high the water's top is at point ({@code i}, {@code j}), laid on a coarser neighbour's edge if need be. */
    private double top(final int i, final int j) {
        final int last = tile.points() - 1;
        final boolean across = (i == 0 && chosen.coarserAt(Chosen.WEST) || i == last && chosen.coarserAt(Chosen.EAST))
                && j % 2 == 1;
        final boolean along = (j == 0 && chosen.coarserAt(Chosen.NEAR) || j == last && chosen.coarserAt(Chosen.FAR))
                && i % 2 == 1;
        if (across) {
            return halfway(tile.water(i, j - 1), tile.water(i, j + 1), tile.water(i, j));
        }
        if (along) {
            return halfway(tile.water(i - 1, j), tile.water(i + 1, j), tile.water(i, j));
        }
        return tile.water(i, j);
    }

    /** Halfway between {@code one} and {@code other}, if both are numbers; else {@code own}. */
    private double halfway(final double one, final double other, final double own) {
        return Double.isNaN(one) || Double.isNaN(other) ? own : (one + other) / 2;
    }
}
