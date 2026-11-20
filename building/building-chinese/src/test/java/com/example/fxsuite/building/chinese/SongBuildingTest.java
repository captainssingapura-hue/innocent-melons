package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How the Northern Song built. The woven arch bridge as its historians read the Rainbow Bridge: 21 groups of ribs,
 * eleven of four members and ten of three, tied by five logs across; its crown some 5 m over the water; walked up and
 * over, the river's passage open under it; nothing wrong with it as a crossing. The shophouse as the Yingzao Fashi
 * frames one: its columns no taller than a bay is wide, its ridge a quarter of its span over its eaves, its roof
 * folded; walked into by its open front, its back and its gables shut; nothing wrong with it as a shop. Each the same
 * from the same specification.
 */
class SongBuildingTest {

    /** The Rainbow Bridge's figures, the water 2.14 m under the street. */
    private static final WovenArch RAINBOW = new WovenArch(2_000, 800, 360, 1_250, -170, 400, 21, 45, 40, 23, 900);
    private static final double WATER = -214;
    private static final Structure BRIDGE = new SongWovenArch().build(RAINBOW);

    private static final ShopHouseSpec<Chinese, ChineseEras.NorthernSong> SHOP = new ShopHouseSpec<>(3, 360, 600,
            Roofing.TILE);
    private static final Structure HOUSE = new SongShopHouse().build(SHOP);

    @Test
    void theWovenArchsRibsLogsAcrossRailingsAndPoles() {
        assertEquals(List.of(), BRIDGE.problems());
        assertEquals(2 * 11 * 2 + 10 * 3, BRIDGE.solids(new Elements.Rib()).size(), "11 groups of four, 10 of three");
        assertEquals(5, BRIDGE.solids(new Elements.Beam()).size(), "the logs across");
        assertEquals(50, BRIDGE.solids(new Elements.Decking()).size());
        assertEquals(2, BRIDGE.solids(new Elements.Abutment()).size());
        assertEquals(2 * 23 + 2 * 22 * 3, BRIDGE.solids(new Elements.Railing()).size(), "23 posts a side, their rails");
        assertEquals(4 * 3, BRIDGE.solids(new Elements.Pole()).size(), "four poles, a cross-bar of two on each");
        final double soffit = SongWovenArch.deck(RAINBOW, 0) - SongWovenArch.BOARD - RAINBOW.log() - WATER;
        assertTrue(soffit > 480 && soffit < 560, "its arch's crown over the water: " + soffit);
    }

    @Test
    void theWovenArchWalkedUpAndOverTheRiversPassageOpenUnderIt() {
        final Volume deck = BRIDGE.shape().getFirst();
        assertEquals(RAINBOW.crown(), deck.topAt(0, 0, 20), 5, "its crown walked on");
        assertEquals(SongWovenArch.deck(RAINBOW, 600), deck.topAt(0, 600, 20), 30, "its slope walked up");
        assertTrue(deck.topAt(0, 0, 20, WATER + 100) < WATER, "the passage's floor under the water");
        assertTrue(deck.undersideAt(0, 0, 20, WATER + 100) > WATER + 400, "4 m clear over the water");
        double steepest = 0;
        for (double z = -RAINBOW.landing(); z < RAINBOW.landing(); z += 10) {
            steepest = Math.max(steepest, Math.abs(SongWovenArch.deck(RAINBOW, z + 10) - SongWovenArch.deck(RAINBOW, z))
                    / 10);
        }
        assertTrue(steepest < Math.tan(Math.toRadians(35)), "its deck's steepest: " + steepest);
    }

    @Test
    void theShophousesFrameAndItsRoofAsTheBookHasThem() {
        assertEquals(List.of(), HOUSE.problems());
        assertEquals(320, SongShopHouse.column(SHOP), "no taller than a bay is wide, 3.2 m at most");
        assertEquals(300, SongShopHouse.column(new ShopHouseSpec<>(2, 300, 500, Roofing.THATCH)));
        // 4 lines of 2 columns, and their posts under the three purlins
        assertEquals(4 * 2 + 4 * 3, HOUSE.solids(new Elements.Column()).size());
        assertEquals(4 + 2 + 3, HOUSE.solids(new Elements.Beam()).size(), "four beams, two lintels, three purlins");
        final double span = SHOP.depth() + 2 * SongShopHouse.EAVE;
        final double eave = SongShopHouse.roof(SHOP, span / 2);
        final double ridge = SongShopHouse.roof(SHOP, 0);
        assertEquals(span / 4, ridge - eave, 1e-9, "its ridge a quarter of its span over its eaves");
        final double straight = eave + (ridge - eave) / 2;
        assertEquals((ridge - eave) / 10, straight - SongShopHouse.roof(SHOP, span / 4), 1e-9,
                "folded: the middle purlin a tenth of the rise under the straight line");
        assertEquals(SongShopHouse.roof(SHOP, 123), SongShopHouse.roof(SHOP, -123), 1e-9, "the same front and back");
    }

    @Test
    void theShophouseWalkedIntoByItsOpenFrontItsBackAndGablesShut() {
        final double d = SHOP.depth() / 2;
        final double a = SHOP.width() / 2;
        // a body's height over the floor, at the middle of the front and well inside: nothing in the way
        assertTrue(clear(0, d), "its front, between two columns");
        assertTrue(clear(0, 0), "inside");
        assertTrue(!clear(0, -d), "its back wall");
        assertTrue(!clear(-a, 0) && !clear(a, 0), "its gables");
        assertTrue(!clear(-a + SHOP.bay(), d), "a column of its front");
        // its floor a step up from the street, no higher than is stepped onto
        assertTrue(SongShopHouse.PLATFORM <= 20);
        assertEquals(SongShopHouse.PLATFORM, HOUSE.shape().stream().filter(v -> v.covers(0, 0, 20))
                .mapToDouble(v -> v.topAt(0, 0, 20, 100)).filter(t -> t <= 100).max().orElseThrow(), 1e-6);
    }

    /** Whether nothing of the house stands between a body's knees and its head at ({@code x}, {@code z}). */
    private static boolean clear(final double x, final double z) {
        return HOUSE.shape().stream().filter(v -> v.covers(x, z, 20))
                .noneMatch(v -> v.low() < SongShopHouse.PLATFORM + 180 && v.high() > SongShopHouse.PLATFORM + 40);
    }

    @Test
    void eachTheSameFromTheSameSpecification() {
        assertEquals(BRIDGE.solids(), new SongWovenArch().build(RAINBOW).solids());
        assertEquals(HOUSE.solids(),
                new SongShopHouse().build(new ShopHouseSpec<>(3, 360, 600, Roofing.TILE)).solids());
        assertEquals(HOUSE.solids().size(), new SongShopHouse().build(new ShopHouseSpec<>(3, 360, 600,
                Roofing.THATCH)).solids().size(), "thatched, the same parts");
    }
}
