package melons.innocent.terrain.tiles;

import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The land in tiles: each halved into four a level down, so many points to a side whatever the level; a tile sampled
 * from the land, how the ground rises at its edges taken from beyond them; the tiles around a spot covering the map
 * once, finest at the spot, none beside one more than a level finer, each told which edges meet a coarser one, the
 * nearest first.
 */
class TilesTest {

    /** Ground 6.4 km square, rising 1 for every 10 across, and so many points of it sorted by which half it is in. */
    private record Slope() implements Ground {
        public Extent extent() {
            return new Extent(0, 0, 640_000, 400_000);
        }

        public double height(final double x, final double z) {
            return x / 10;
        }
    }

    private final Tiling tiling = new Tiling(new Slope().extent(), 32);

    @Test
    void eachHalvedIntoFourALevelDownSoManyPointsToASideWhateverTheLevel() {
        assertEquals(640_000, tiling.side(), 1e-9, "a square as big as the map's longer way");
        assertEquals(80_000, tiling.side(3), 1e-9);
        assertEquals(2_500, tiling.spacing(3), 1e-9);
        final TileKey key = new TileKey(3, 5, 2);
        assertEquals(400_000, tiling.x0(key), 1e-9);
        assertEquals(160_000, tiling.z0(key), 1e-9);
        assertEquals(List.of(new TileKey(4, 10, 4), new TileKey(4, 11, 4), new TileKey(4, 10, 5),
                new TileKey(4, 11, 5)), key.children());
        assertEquals(key, key.children().get(3).parent());
        assertEquals(0, tiling.distance(key, 410_000, 170_000), 1e-9);
        assertEquals(10_000, tiling.distance(key, 390_000, 170_000), 1e-9);
        assertEquals(7, tiling.level(200), "the first level with points 2 m apart or nearer: 156 cm");
        assertThrows(IllegalArgumentException.class, () -> new TileKey(2, 4, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tiling(0, 0, 100, 3));
        assertFalse(new TileKey(1, 1, 0).beside(1, 0));
    }

    @Test
    void aTileSampledFromTheLandHowItRisesAtItsEdgesFromBeyondThem() {
        final TileData tile = new TileData(tiling, new TileKey(3, 5, 2), new Slope(), at -> at.x() < 410_000 ? 0 : 1);
        assertEquals(33, tile.points());
        assertEquals(40_000, tile.height(0, 0), 1e-9);
        assertEquals(48_000, tile.height(32, 7), 1e-9);
        assertEquals(0.1, tile.across(0, 0), 1e-12, "at its edge, from the ground beyond it");
        assertEquals(0, tile.along(32, 32), 1e-12);
        assertEquals(0, tile.sort(3, 0));
        assertEquals(1, tile.sort(4, 0));
    }

    @Test
    void itsTrianglesCutWhereWhatLiesOnThemPartsCoveringItOnce() {
        // what lies west of 412.34 m across sorted one way, east of it the other: no point of the tile lies on it
        final TileData tile = new TileData(tiling, new TileKey(3, 5, 2), new Slope(), at -> at.x() < 412_340 ? 0 : 1);
        final Triangles cut = tile.triangles();
        double area = 0;
        for (int k = 0; k < cut.count(); k++) {
            final double[][] corner = new double[3][];
            double x = 0;
            for (int c = 0; c < 3; c++) {
                corner[c] = at(tile, cut, cut.corner(k, c));
                x += corner[c][0] / 3;
            }
            area += Math.abs((corner[1][0] - corner[0][0]) * (corner[2][1] - corner[0][1])
                    - (corner[2][0] - corner[0][0]) * (corner[1][1] - corner[0][1])) / 2;
            assertEquals(x < 412_340 ? 0 : 1, cut.sort(k), "each piece on its side of the line, at " + x);
        }
        assertEquals(Math.pow(tiling.side(3), 2), area, 1e-3, "the tile covered once");
        assertTrue(cut.added() > 0 && cut.added() < 2 * tile.points(), "cut only along the line");
    }

    /** Where point {@code c} of {@code cut} is, across and along. */
    private double[] at(final TileData tile, final Triangles cut, final int c) {
        if (c < cut.own()) {
            return new double[] {tile.x(c % tile.points()), tile.z(c / tile.points())};
        }
        final double[] sum = new double[2];
        for (int k = 0; k < 3; k++) {
            final double[] part = at(tile, cut, cut.from(c - cut.own(), k));
            sum[0] += cut.share(c - cut.own(), k) * part[0];
            sum[1] += cut.share(c - cut.own(), k) * part[1];
        }
        return sum;
    }

    @Test
    void theTilesAroundASpotCoverTheMapOnceFinestAtTheSpotNoneBesideOneMoreThanALevelFiner() {
        final Choice choice = new Choice(tiling, 1.5, 6);
        final List<Chosen> chosen = choice.around(100_000, 300_000);
        double area = 0;
        for (final Chosen tile : chosen) {
            area += Math.pow(tiling.side(tile.key().level()), 2);
        }
        assertEquals(Math.pow(tiling.side(), 2), area, 1e-3, "every part of the map covered once");
        assertEquals(6, chosen.getFirst().key().level(), "the nearest, at the spot, at the finest level");
        assertEquals(0, tiling.distance(chosen.getFirst().key(), 100_000, 300_000), 1e-9);
        final Set<TileKey> keys = new HashSet<>();
        chosen.forEach(tile -> keys.add(tile.key()));
        for (final Chosen tile : chosen) {
            final TileKey key = tile.key();
            if (key.beside(1, 0)) {
                final int other = levelAt(keys, key.moved(1, 0));
                assertTrue(Math.abs(other - key.level()) <= 1 || other > key.level(), "balanced at " + key);
                assertEquals(other < key.level(), tile.coarserAt(Chosen.EAST), "coarser to the east of " + key);
            }
        }
        assertEquals(chosen.size(), keys.size());
        for (int k = 1; k < chosen.size(); k++) {
            assertTrue(tiling.distance(chosen.get(k - 1).key(), 100_000, 300_000)
                    <= tiling.distance(chosen.get(k).key(), 100_000, 300_000), "nearest first");
        }
    }

    /** The level of the tile among {@code keys} that {@code key} lies in; one finer than it, if cut finer. */
    private int levelAt(final Set<TileKey> keys, final TileKey key) {
        TileKey at = key;
        while (!keys.contains(at)) {
            if (at.level() == 0) {
                return key.level() + 1;
            }
            at = at.parent();
        }
        return at.level();
    }
}
