package melons.innocent.terrain.drawn;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.TileData;
import melons.innocent.terrain.tiles.Triangles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tile's meshes, made from its data away from the card, ready to be put on it: a sheet for each layer its triangles
 * are in, cut where what lies on them parts ({@link Triangles}); its points in the tile's own frame from its near left
 * corner, drawn {@code up} times as high, and where on the land each is, in the map's own frame, as its surface's
 * pattern reads it, so the pattern runs on unbroken from one tile into the next. Where an edge meets a coarser tile,
 * every other point along it is laid on the line between the two either side, as the coarser tile's edge runs, and
 * faces as they do between them, so no crack shows; a point a cut added lies on its edge however that edge is laid.
 * Over it, where water lies, the water's level sheet ({@link Water}), if water is drawn. And about how much room they
 * take, here and on the card.
 */
public final class TileMeshes {

    /** About how much room a point of a mesh takes here, its three vectors among it, and a corner of a triangle. */
    private static final long HELD_POINT = 132;
    private static final long HELD_CORNER = 20;
    /** About how much room a point takes on the card, three vectors of three floats, and a corner. */
    private static final long CARD_POINT = 36;
    private static final long CARD_CORNER = 4;

    private final Chosen chosen;
    private final Vec3 corner;
    private final Map<Integer, Mesh> sheets = new LinkedHashMap<>();
    private final Mesh water;

    /** {@code tile}'s meshes, chosen as {@code chosen} says, {@code up} times as high; its water's, if {@code wet}. */
    public TileMeshes(final TileData tile, final Chosen chosen, final double up, final boolean wet) {
        this.chosen = chosen;
        this.corner = new Vec3(tile.x0(), 0, tile.z0());
        final Triangles cut = tile.triangles();
        final Map<Integer, List<Integer>> sorted = new LinkedHashMap<>();
        for (int k = 0; k < cut.count(); k++) {
            sorted.computeIfAbsent(cut.sort(k), s -> new ArrayList<>()).add(k);
        }
        sorted.forEach((layer, triangles) -> sheets.put(layer, sheet(tile, up, triangles)));
        final Mesh level = wet ? new Water(tile, chosen).sheet(up) : null;
        this.water = level == null || level.triangles() == 0 ? null : level;
    }

    /** Which tile, and which of its edges meet coarser ones. */
    public Chosen chosen() {
        return chosen;
    }

    /** Where its near left corner is on the map: where its points are measured from. */
    public Vec3 corner() {
        return corner;
    }

    /** Its sheets, by the layer each is drawn in. */
    public Map<Integer, Mesh> sheets() {
        return sheets;
    }

    /** Its water's level sheet, if water lies over it and is drawn. */
    public Mesh water() {
        return water;
    }

    /** About how much room they take here, in bytes. */
    public long held() {
        return room(HELD_POINT, HELD_CORNER);
    }

    /** About how much room they take on the card, in bytes. */
    public long carded() {
        return room(CARD_POINT, CARD_CORNER);
    }

    private long room(final long point, final long corner) {
        long room = 0;
        for (final Mesh mesh : sheets.values()) {
            room += mesh.points().size() * point + mesh.corners().size() * corner;
        }
        return water == null ? room : room + water.points().size() * point + water.corners().size() * corner;
    }

    /** The sheet of {@code triangles} of the tile's cut triangles, its points only those they use. */
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
}
