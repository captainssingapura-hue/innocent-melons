package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.FarmhouseSpec;
import com.example.fxsuite.building.spec.QuaySpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The country and the river's bank. The farmhouse walled all round but for its door, thatched, a table inside; it and
 * its yard closed round by its fence, gone into by its gateway only, the yard reached by it and the house by its door.
 * The quay: its deck stood on level with the ground behind it, its face in the way; its steps walked down from its deck
 * to its landing, none higher than is stepped; its mooring posts along its edge, none before a flight's head; nothing
 * wrong with either as what it is for, and a quay too short for its steps refused.
 */
class SongCountryTest {

    private static final FarmhouseSpec<Chinese, ChineseEras.NorthernSong> FARM = new FarmhouseSpec<>(3, 330, 600,
            Roofing.THATCH, 800);
    private static final QuaySpec<Chinese, ChineseEras.NorthernSong> QUAY = new QuaySpec<>(1_500, 200, 400, 200, 1);
    private static final double BODY = 20;

    /** Whether a body standing at {@code floor} at ({@code x}, {@code z}) is in nothing of {@code built}. */
    private static boolean clear(final Structure built, final double x, final double z, final double floor) {
        return built.shape().stream().allMatch(v -> v.push(x, z, BODY, floor + 20, floor + 180) == null);
    }

    /** How high what is stood on at ({@code x}, {@code z}) is, coming from {@code from}: a step up at most. */
    private static double top(final Structure built, final double x, final double z, final double from) {
        return built.shape().stream().filter(v -> v.covers(x, z, 5)).mapToDouble(v -> v.topAt(x, z, 5, from + 20))
                .filter(t -> t <= from + 20).max().orElse(Double.NaN);
    }

    @Test
    void theFarmhouseWalledAllRoundButForItsDoorThatchedATableInside() {
        final Structure farm = new SongFarmhouse().build(FARM);
        assertEquals(List.of(), farm.problems());
        assertEquals(farm.rooms(), farm.reached(), "its yard by its gateway, its house by its door");
        final double d = FARM.depth() / 2;
        final double floor = SongFrame.PLATFORM;
        assertTrue(clear(farm, SongFrame.door(FARM), d, floor), "its doorway");
        assertFalse(clear(farm, SongFrame.door(FARM) + 250, d, floor), "its front wall beside it");
        assertFalse(clear(farm, 0, -d, floor), "its back wall");
        assertTrue(farm.solids(new Elements.Roof()).stream().allMatch(r -> r.material().name().equals("THATCH")));
        assertEquals(1, farm.solids(new Elements.Counter()).size());
        assertEquals(new Spaces.Courtyard(), farm.rooms().get(1).kind());
    }

    @Test
    void itAndItsYardClosedRoundByItsFenceGoneIntoByItsGatewayOnly() {
        final Structure farm = new SongFarmhouse().build(FARM);
        final double[] f = SongFarmhouse.fence(FARM);
        assertTrue(clear(farm, SongFrame.door(FARM), f[2], 0), "its gateway");
        // walked round outside it: its fence in the way all round but at its gateway
        for (double x = -f[0]; x <= f[0]; x += 50) {
            assertFalse(clear(farm, x, f[1], 0), "its fence behind, at " + x);
            final boolean gateway = Math.abs(x - SongFrame.door(FARM)) < SongFarmhouse.GATE / 2 - BODY - 6;
            assertEquals(gateway, clear(farm, x, f[2], 0),
                    "its fence before, at " + x);
        }
        for (double z = f[1]; z <= f[2]; z += 50) {
            assertFalse(clear(farm, -f[0], z, 0) || clear(farm, f[0], z, 0), "its fence at its sides, at " + z);
        }
        assertEquals(FARM.depth() / 2 + FARM.yard(), f[2], 1e-9, "its yard as deep as asked");
        assertTrue(farm.solids(new Elements.Screen()).stream().map(Solid::material)
                .allMatch(m -> m.name().equals("REED") || m.name().equals("TIMBER")), "of wattle on timber posts");
    }

    @Test
    void theQuaysDeckStoodOnLevelWithTheGroundItsFaceInTheWay() {
        final Structure quay = new SongQuay().build(QUAY);
        assertEquals(List.of(), quay.problems());
        assertEquals(quay.rooms(), quay.reached(), "its deck from behind, its landing by its steps");
        for (final double x : new double[] {-700, 0, 700}) {
            for (final double z : new double[] {-SongQuay.BACK + 25, 0, QUAY.out() - 30}) {
                assertEquals(0, top(quay, x, z, 0), 1e-9, "its deck at " + x + ", " + z);
            }
        }
        assertEquals(1, quay.solids(new Elements.Wall()).size());
        assertTrue(quay.solids(new Elements.Wall()).getFirst().corners().stream()
                .allMatch(p -> p.up() <= 0 && p.up() >= -QUAY.bed() - 40), "its face from its top to the bed");
        // off its face, a body on the water's level is stopped by it
        assertFalse(clear(quay, 600, QUAY.out() - 10, -QUAY.landing()), "its face");
    }

    @Test
    void itsStepsWalkedDownFromItsDeckToItsLandingNoneHigherThanIsStepped() {
        final Structure quay = new SongQuay().build(QUAY);
        final double x0 = SongQuay.head(QUAY, 0);
        final int n = SongQuay.steps(QUAY);
        final double z = QUAY.out() + SongQuay.WIDE / 2;
        // stepped off the deck's edge onto the first step, then down along the flight
        double at = 0;
        for (double x = x0 + 5; x <= x0 + n * SongQuay.TREAD + SongQuay.LANDING - 5; x += 5) {
            final double here = x;
            final double next = quay.shape().stream().filter(v -> v.covers(here, z, 5))
                    .mapToDouble(v -> v.topAt(here, z, 5, Double.POSITIVE_INFINITY)).max().orElseThrow();
            assertTrue(next <= at + 1e-9 && at - next <= SongQuay.RISER, "at " + x + ": from " + at + " to " + next);
            at = next;
        }
        assertEquals(-QUAY.landing(), at, 1e-9, "on its landing");
        assertTrue(clear(quay, x0 + SongQuay.TREAD / 2, z, -QUAY.landing() / n), "nothing over its steps");
        // no post before its head
        for (final Solid post : quay.solids(new Elements.Pole())) {
            final double x = post.corners().getFirst().across();
            assertTrue(x < x0 - SongQuay.THICK / 2 || x > x0 + n * SongQuay.TREAD + SongQuay.LANDING, "a post at " + x);
        }
        assertEquals(2, quay.solids(new Elements.Pole()).size(), "4 m apart, the two before its flight left out");
    }

    @Test
    void aQuayTooShortForItsStepsRefused() {
        assertThrows(IllegalArgumentException.class, () -> new SongQuay().build(new QuaySpec<>(600, 200, 400, 200,
                1)));
        assertThrows(IllegalArgumentException.class, () -> new QuaySpec<Chinese, ChineseEras.NorthernSong>(1_500, 200,
                150, 200, 1), "its landings under the bed");
        assertThrows(IllegalArgumentException.class, () -> new FarmhouseSpec<Chinese, ChineseEras.NorthernSong>(3, 330,
                600, Roofing.THATCH, 100), "a yard too shallow");
    }
}
