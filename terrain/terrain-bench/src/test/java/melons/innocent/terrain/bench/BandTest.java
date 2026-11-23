package melons.innocent.terrain.bench;

import melons.innocent.terrain.Extent;
import melons.innocent.terrain.tiles.Chosen;
import melons.innocent.terrain.tiles.Tiling;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The eye's bands of height: every 500 m from the sea's level; held within its band; the land seen the more coarsely
 * the higher the band, as two pixels cover from its top. And what the eye sees: the spot under it and what lies ahead
 * of it, not what lies behind, and the further to the sides the wider the view; the tiles over it all of one level,
 * none coarser than the next, the nearest first.
 */
class BandTest {

    private static final double M = 100;

    private final Altitudes altitudes = new Altitudes(500 * M, 0.001);

    @Test
    void every500MetresHeldWithinTheLandTheCoarserTheHigher() {
        assertEquals(0, altitudes.band(0));
        assertEquals(2, altitudes.band(1_200 * M));
        assertEquals(1_000 * M, altitudes.floor(2));
        assertEquals(1_500 * M, altitudes.top(2));
        assertEquals(1_500 * M - 1, altitudes.within(2, 9_000 * M));
        assertEquals(1_000 * M, altitudes.within(2, 0));
        assertEquals(3 * M, altitudes.scale(2), 1e-9, "two pixels of a thousandth, from 1500 m");
        assertTrue(altitudes.scale(5) > altitudes.scale(2));
    }

    @Test
    void aWiderViewSeesFurtherToTheSides() {
        final Flight flight = new Flight(0, 0, 1_000 * M, 0, 60, 1);
        final double aside = 1_100 * M;
        assertTrue(new Footprint(flight, 50, 1.6).distance(aside, 700 * M) > 0, "beyond a view 1.6 times as wide");
        assertEquals(0, new Footprint(flight, 50, 2.4).distance(aside, 700 * M), 1e-6, "within one 2.4 times as wide");
    }

    @Test
    void theSpotUnderTheEyeAndWhatLiesAheadNotBehind() {
        final Flight flight = new Flight(0, 0, 1_000 * M, 0, 60, 1);
        final Footprint seen = new Footprint(flight, 50, 1.6);
        assertEquals(0, seen.distance(0, 0), 1e-6);
        assertEquals(0, seen.distance(0, 500 * M), 1e-6, "ahead, along the map");
        assertTrue(seen.distance(0, -1_000 * M) > 0, "behind");
        final Tiling tiling = new Tiling(new Extent(-3_000 * M, -3_000 * M, 3_000 * M, 3_000 * M), 32);
        final List<Chosen> tiles = seen.tiles(tiling, 5);
        assertTrue(tiles.size() > 4);
        assertTrue(tiles.stream().allMatch(tile -> tile.key().level() == 5 && tile.coarser() == 0));
        final double side = tiling.side(5);
        final Chosen nearest = tiles.getFirst();
        assertTrue(tiling.x0(nearest.key()) <= 0 && tiling.x0(nearest.key()) + side >= 0
                && tiling.z0(nearest.key()) <= 0 && tiling.z0(nearest.key()) + side >= 0, "the tile under the eye");
    }
}
