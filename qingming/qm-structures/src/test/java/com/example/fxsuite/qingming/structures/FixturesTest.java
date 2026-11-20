package com.example.fxsuite.qingming.structures;

import com.example.fxsuite.building.Atlas;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.qingming.land.Reach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What stands in the reach: nothing wrong with its map; the Rainbow Bridge at its origin, landing on the street both
 * ways; fourteen shops along the street, each facing it and clear of it, none in another; each a piece of the world.
 */
class FixturesTest {

    private static final List<Atlas.Fixture> ALL = Fixtures.atlas().fixtures();

    @Test
    void nothingWrongWithItsMapTheBridgeAtItsOriginLandingOnTheStreet() {
        assertEquals(List.of(), Fixtures.atlas().problems());
        final Atlas.Fixture bridge = ALL.getFirst();
        assertEquals("the Rainbow Bridge", bridge.name());
        assertEquals(new Buildings.Bridge(), bridge.thing());
        assertEquals(0, Math.hypot(bridge.x(), bridge.z()));
        assertEquals(0, Reach.ground().height(0, 1_250), 30, "the street where it lands, north");
        assertEquals(0, Reach.ground().height(0, -1_250), 30, "and south");
    }

    @Test
    void fourteenShopsAlongTheStreetEachFacingItAndClearOfItNoneInAnother() {
        final List<Atlas.Fixture> shops = ALL.stream().filter(f -> f.thing().equals(new Buildings.ShopHouse()))
                .toList();
        assertEquals(14, shops.size());
        for (final Atlas.Fixture shop : shops) {
            final boolean north = shop.z() > Fixtures.STREET;
            assertEquals(north ? 180 : 0, shop.yaw(), shop.name() + " facing the street");
            // its front 3 m back from the street's edge, its eaves, 90 cm out past it, clear of the street
            final double front = Math.abs(shop.z() - Fixtures.STREET) - reach(shop, false);
            assertEquals(Fixtures.BACK, front, 1e-6, shop.name());
            assertTrue(front - 90 > 400, shop.name() + " clear of the street");
            assertTrue(shop.x() < -10_000 && shop.x() > -30_000, shop.name() + " on the straight stretch");
        }
        for (final String row : List.of("north", "south")) {
            final List<Atlas.Fixture> inRow = shops.stream().filter(f -> f.name().contains(row)).toList();
            assertEquals(7, inRow.size());
            for (int k = 0; k + 1 < inRow.size(); k++) {
                // each wholly west of the last, their gables' overhangs, 50 cm each, apart
                assertTrue(inRow.get(k).x() - reach(inRow.get(k), true) > inRow.get(k + 1).x()
                        + reach(inRow.get(k + 1), true) + 100, row + " " + (k + 1) + " and the next");
            }
        }
    }

    /** How far {@code shop}'s floor reaches from its middle: across, along its front; or along, to its front. */
    private static double reach(final Atlas.Fixture shop, final boolean across) {
        return shop.structure().rooms().getFirst().outline().stream().mapToDouble(p -> across ? p.x() : p.z()).max()
                .orElseThrow();
    }

    @Test
    void eachAPieceOfTheWorld() {
        assertEquals(15, Fixtures.pieces().size());
    }
}
