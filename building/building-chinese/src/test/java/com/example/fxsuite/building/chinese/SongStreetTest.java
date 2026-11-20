package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.building.spec.WineHouseSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The rest of a Song street. The tea house open along its front and at both ends, shut along its back, a table and two
 * benches to a bay. The warehouse shut all round but for its doorway. The wine house of two storeys: its rooms reached
 * from its hall by its stair, each step no higher than is stepped, the well clear over it and railed off above. The
 * stall, a counter under an awning or an umbrella. The festive gateway gone under between its middle poles. Nothing
 * wrong with any of them as what it is for.
 */
class SongStreetTest {

    private static final HallSpec<Buildings.TeaHouse, Chinese, ChineseEras.NorthernSong> TEA = new HallSpec<>(3, 380,
            600, Roofing.THATCH);
    private static final HallSpec<Buildings.Warehouse, Chinese, ChineseEras.NorthernSong> STORE = new HallSpec<>(3, 400,
            700, Roofing.TILE);
    private static final WineHouseSpec<Chinese, ChineseEras.NorthernSong> WINE = new WineHouseSpec<>(5, 360, 720,
            Roofing.TILE);
    private static final double FLOOR = SongFrame.PLATFORM;

    /** Whether nothing of {@code built} stands between a body's knees and its head at ({@code x}, {@code z}). */
    private static boolean clear(final Structure built, final double x, final double z, final double floor) {
        return built.shape().stream().filter(v -> v.covers(x, z, 20))
                .noneMatch(v -> v.low() < floor + 180 && v.high() > floor + 40);
    }

    /** How high what is stood on at ({@code x}, {@code z}) is, coming from {@code below}. */
    private static double top(final Structure built, final double x, final double z, final double below) {
        return built.shape().stream().filter(v -> v.covers(x, z, 5)).mapToDouble(v -> v.topAt(x, z, 5, below + 25))
                .filter(t -> t <= below + 25).max().orElse(Double.NaN);
    }

    @Test
    void theTeaHouseOpenAlongItsFrontAndAtBothEndsShutAlongItsBack() {
        final Structure tea = new SongTeaHouse().build(TEA);
        assertEquals(List.of(), tea.problems());
        final double a = TEA.width() / 2;
        final double d = TEA.depth() / 2;
        assertTrue(clear(tea, 0, d, FLOOR) && clear(tea, -a, d / 2, FLOOR) && clear(tea, a, d / 2, FLOOR));
        assertFalse(clear(tea, 0, -d, FLOOR), "its back wall");
        assertEquals(3, tea.solids(new Elements.Counter()).size(), "a table to a bay");
        assertEquals(6, tea.solids(new Elements.Bench()).size(), "two benches to a table");
        assertEquals(3, tea.waysIn().size());
    }

    @Test
    void theWarehouseShutAllRoundButForItsDoorway() {
        final Structure store = new SongWarehouse().build(STORE);
        assertEquals(List.of(), store.problems());
        final double a = STORE.width() / 2;
        final double d = STORE.depth() / 2;
        assertTrue(clear(store, SongFrame.door(STORE), d, FLOOR), "its doorway");
        assertTrue(clear(store, 0, 0, FLOOR), "inside");
        assertFalse(clear(store, SongFrame.door(STORE) + 200, d, FLOOR), "its front beside the doorway");
        assertFalse(clear(store, 0, -d, FLOOR) || clear(store, -a, 0, FLOOR) || clear(store, a, 0, FLOOR));
    }

    @Test
    void theWineHousesRoomsReachedFromItsHallByItsStair() {
        final Structure wine = new SongWineHouse().build(WINE);
        assertEquals(List.of(), wine.problems());
        assertEquals(2, wine.rooms().size());
        assertEquals(wine.rooms(), wine.reached(), "its hall by its front, its rooms by its stair");
        final double rooms = FLOOR + SongWineHouse.storey(WINE);
        assertEquals(rooms, wine.rooms().get(1).level(), 1e-9);
        // climbed: each step no higher than is stepped, from the hall's floor to the rooms'
        final int steps = SongWineHouse.steps(WINE);
        final double foot = -steps * SongWineHouse.TREAD / 2;
        final double z = -WINE.depth() / 2 + SongWineHouse.FROM_BACK + SongWineHouse.STAIR / 2;
        double at = FLOOR;
        for (int j = 0; j <= steps; j++) {
            final double x = foot + (j + 0.5) * SongWineHouse.TREAD;
            final double next = top(wine, x, z, at);
            assertTrue(next - at <= 20 + 1e-6 && next >= at - 1e-6, "step " + (j + 1) + ": from " + at + " to " + next);
            // the well clear over the stair, a body's height over each step
            final double on = next;
            assertTrue(wine.shape().stream().filter(v -> v.covers(x, z, 20))
                    .noneMatch(v -> v.low() < on + 180 && v.high() > on + 25), "headroom over step " + (j + 1));
            at = next;
        }
        assertEquals(rooms, at, 1e-6, "its head on the rooms' floor");
        // the well railed off above: not walked into from the rooms' floor
        assertFalse(clear(wine, 0, z + SongWineHouse.STAIR / 2 + 3, rooms), "the rail along the well");
        assertTrue(clear(wine, 0, 100, rooms), "the rooms, open across");
        assertThrows(IllegalArgumentException.class, () -> new SongWineHouse().build(new WineHouseSpec<>(2, 300, 600,
                Roofing.TILE)), "too narrow for its stair");
    }

    @Test
    void theStallACounterUnderAnAwningOrAnUmbrella() {
        for (final StallSpec.Shade shade : StallSpec.Shade.values()) {
            final Structure stall = new SongStall().build(new StallSpec<>(200, 150, shade));
            assertEquals(List.of(), stall.problems(), shade.name());
            assertEquals(1, stall.solids(new Elements.Counter()).size());
            assertFalse(stall.solids(new Elements.Canopy()).isEmpty(), shade.name());
            assertTrue(stall.solids(new Elements.Canopy()).stream().allMatch(c -> c.corners().stream()
                    .allMatch(p -> p.up() >= 185)), "its shade over a head");
        }
    }

    @Test
    void theFestiveGatewayGoneUnderBetweenItsMiddlePoles() {
        final Structure gate = new SongFestiveGate().build(new FestiveGate(600, 160, 700));
        assertEquals(List.of(), gate.problems());
        assertEquals(8, gate.solids(new Elements.Pole()).size(), "two rows of four");
        assertEquals(8, gate.solids(new Elements.Finial()).size());
        assertTrue(clear(gate, 0, 80, 0) && clear(gate, 0, 0, 0) && clear(gate, 0, -80, 0), "the way through");
        assertFalse(clear(gate, SongFestiveGate.WAY / 2, 80, 0), "a middle pole");
        final List<Volume> low = gate.shape().stream().filter(v -> v.low() < 200).toList();
        assertEquals(8, low.size(), "only its poles in the way");
    }
}
