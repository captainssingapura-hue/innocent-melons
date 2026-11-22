package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileData;
import melons.innocent.terrain.tiles.Triangles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tile on the card: a sheet for each layer its triangles are in, cut where what lies on them parts
 * ({@link Triangles}); its points in the tile's own frame from its near left corner, drawn {@code up} times as high,
 * and where on the land each is, in the map's own frame, as its surface's pattern reads it, so the pattern runs on
 * unbroken from one tile into the next. Where an edge meets a coarser tile, every other point along it is laid on the
 * line between the two either side, as the coarser tile's edge runs, and faces as they do between them, so no crack
 * shows; a point a cut added lies on its edge however that edge is laid. Over it, where water lies, the water's level
 * sheet ({@link Water}). Closed, it is gone from the card.
 */
public final class TileDrawn implements AutoCloseable {

    private final Chosen chosen;
    private final Vec3 corner;
    private final Map<Integer, Shaped> sheets = new LinkedHashMap<>();
    /** The layer its water is drawn in, and its water's level sheet: none where no water lies over its ground. */
    private final int waterLayer;
    private final Shaped water;

    /**
     * {@code tile}, chosen as {@code chosen} says, on {@code card}, laid out as {@code laid}; its water, if any, drawn
     * in layer {@code waterLayer}'s look.
     */
    public TileDrawn(final Card card, final Laid laid, final TileData tile, final Chosen chosen, final double up,
                     final int waterLayer) {
        this.chosen = chosen;
        this.waterLayer = waterLayer;
        this.corner = new Vec3(tile.x0(), 0, tile.z0());
        final Triangles cut = tile.triangles();
        final Map<Integer, List<Integer>> sorted = new LinkedHashMap<>();
        for (int k = 0; k < cut.count(); k++) {
            sorted.computeIfAbsent(cut.sort(k), s -> new ArrayList<>()).add(k);
        }
        sorted.forEach((layer, triangles) -> sheets.put(layer, new Shaped(card, laid, sheet(tile, up, triangles))));
        final Mesh level = new Water(tile, chosen).sheet(up);
        this.water = waterLayer < 0 || level.triangles() == 0 ? null : new Shaped(card, laid, level);
    }

    /** Which tile, and which of its edges meet coarser ones. */
    public Chosen chosen() {
        return chosen;
    }

    /** Drawn in {@code pass}, its layers in {@code looks}, as {@code flight} has the land, seen by {@code camera}. */
    public void drawn(final Pass pass, final Looks looks, final Flight flight, final Camera camera) {
        final Mat4 stands = flight.land().times(new Mat4(corner));
        sheets.forEach((layer, sheet) -> looks.of(layer).drawn(pass, sheet, stands, camera, 1));
        if (water != null) {
            looks.of(waterLayer).drawn(pass, water, stands, camera, 1);
        }
    }

    /** The sheet of {@code triangles} of its cut triangles, its points only those they use. */
    private Mesh sheet(final TileData tile, final double up, final List<Integer> triangles) {
        final Triangles cut = tile.triangles();
        final Map<Integer, Integer> own = new HashMap<>();
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        final List<Vec3> ons = new ArrayList<>();
        final List<Integer> corners = new ArrayList<>(triangles.size() * 3);
        for (final int triangle : triangles) {
            for (int k = 0; k < 3; k++) {
                corners.add(own.computeIfAbsent(cut.corner(triangle, k), c -> {
                    final double[] at = point(tile, c);
                    points.add(new Vec3(at[0] - tile.x0(), at[2] * up, at[1] - tile.z0()));
                    facings.add(new Vec3(-at[3] * up, 1, -at[4] * up).unit());
                    ons.add(new Vec3(at[0], at[2], at[1]));
                    return points.size() - 1;
                }));
            }
        }
        return new Mesh(points, facings, corners, ons);
    }

    /**
     * Point {@code c} of the tile's cut triangles, as laid: where on the map, across and along, how high, and how it
     * rises across and along; one a cut added made of the tile's own as they are laid, each in its share, so it lies
     * on the edge it cuts however that edge is laid.
     */
    private double[] point(final TileData tile, final int c) {
        final Triangles cut = tile.triangles();
        final int n = tile.points();
        if (c < cut.own()) {
            final double[] at = laid(tile, c % n, c / n);
            return new double[] {tile.x(c % n), tile.z(c / n), at[0], at[1], at[2]};
        }
        final double[] sum = new double[5];
        for (int k = 0; k < 3; k++) {
            final double share = cut.share(c - cut.own(), k);
            final double[] part = point(tile, cut.from(c - cut.own(), k));
            for (int q = 0; q < sum.length; q++) {
                sum[q] += share * part[q];
            }
        }
        return sum;
    }

    /**
     * How high point ({@code i}, {@code j}) is laid, and how it rises across and along: as the tile has it, or, every
     * other point along an edge that meets a coarser tile, halfway between the two either side of it.
     */
    private double[] laid(final TileData tile, final int i, final int j) {
        final int last = tile.points() - 1;
        final boolean onWest = i == 0 && chosen.coarserAt(Chosen.WEST);
        final boolean onEast = i == last && chosen.coarserAt(Chosen.EAST);
        final boolean onNear = j == 0 && chosen.coarserAt(Chosen.NEAR);
        final boolean onFar = j == last && chosen.coarserAt(Chosen.FAR);
        if ((onWest || onEast) && j % 2 == 1) {
            return halfway(tile, i, j - 1, i, j + 1);
        }
        if ((onNear || onFar) && i % 2 == 1) {
            return halfway(tile, i - 1, j, i + 1, j);
        }
        return new double[] {tile.height(i, j), tile.across(i, j), tile.along(i, j)};
    }

    /** Halfway between points ({@code i0}, {@code j0}) and ({@code i1}, {@code j1}): how high, and how it rises. */
    private double[] halfway(final TileData tile, final int i0, final int j0, final int i1, final int j1) {
        return new double[] {(tile.height(i0, j0) + tile.height(i1, j1)) / 2,
            (tile.across(i0, j0) + tile.across(i1, j1)) / 2, (tile.along(i0, j0) + tile.along(i1, j1)) / 2};
    }

    @Override
    public void close() {
        sheets.values().forEach(Shaped::close);
        if (water != null) {
            water.close();
        }
    }
}
