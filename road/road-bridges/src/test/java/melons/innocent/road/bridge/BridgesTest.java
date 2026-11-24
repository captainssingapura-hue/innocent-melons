package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bridges: a viaduct on level ground, its spans equal, its columns all alike, from the ground up to their caps, its
 * road on its deck between its parapets; the same over a valley, each column reaching the ground under it, the
 * tallest where the valley is deepest; a flyover, the room asked for left under it over what it crosses, its piers
 * clear of that; and what is not a bridge refused.
 */
class BridgesTest {

    private final ViaductSpec<Civilization, CommonEras.Contemporary> viaduct = new ViaductSpec<>(3, 370, 300, 30_000,
            1_000, 3_000, Piers.TWIN);

    @Test
    void aViaductOnLevelGroundItsColumnsAllAlike() {
        final Structure built = new GenericViaduct().build(viaduct);
        assertEquals(List.of(), built.problems());
        final List<Solid> columns = built.solids(new Elements.Column());
        assertEquals(9 * 2, columns.size(), "ten spans of 30 m, a pier between each two, on two columns");
        assertTrue(columns.stream().allMatch(c -> foot(c) == 0 && Math.abs(top(c) - (1_000 - 200 - 150)) < 1e-9),
                "from the ground up to the caps, every one alike");
        assertEquals(9, built.solids(new Elements.Beam()).size(), "a cap to each pier");
        assertEquals(2, built.solids(new Elements.Abutment()).size());
        assertEquals(30, built.solids(new Elements.Decking()).size(), "the deck in 10 m pieces");
        assertEquals(2 * 30, built.solids(new Elements.Parapet()).size());
        assertTrue(built.solids(new Elements.TrafficLane()).stream().allMatch(l -> foot(l) == 1_000),
                "its road on its deck, 10 m up");
        assertEquals(9, new GenericViaduct().build(new ViaductSpec<>(3, 370, 300, 30_000, 1_000, 3_000,
                Piers.SINGLE)).solids(new Elements.Column()).size(), "or on one each");
    }

    @Test
    void aViaductOverAValleyEachColumnReachingTheGroundUnderIt() {
        final Ground valley = new Ground() {
            @Override
            public Extent extent() {
                return new Extent(-100_000, -100_000, 100_000, 100_000);
            }

            @Override
            public double height(final double x, final double z) {
                return 800 * Math.pow(z / 15_000, 2) - 800;
            }
        };
        final Structure built = new GenericViaduct(valley).build(viaduct);
        final List<Solid> columns = built.solids(new Elements.Column());
        for (final Solid c : columns) {
            assertEquals(valley.height(middle(c, Point::across), middle(c, Point::along)), foot(c), 1e-9,
                    "on the ground under it");
        }
        final double middle = columns.stream().filter(c -> Math.abs(middle(c, Point::along)) < 200)
                .mapToDouble(c -> top(c) - foot(c)).max().orElseThrow();
        assertTrue(columns.stream().allMatch(c -> top(c) - foot(c) <= middle + 1e-9),
                "the tallest where the valley is deepest");
        assertTrue(built.solids(new Elements.TrafficLane()).stream().allMatch(l -> foot(l) == -800 + 1_000),
                "its road 10 m over the ground under its middle");
    }

    @Test
    void aFlyoverItsRoomLeftUnderItOverWhatItCrosses() {
        final FlyoverSpec<Civilization, CommonEras.Contemporary> s = new FlyoverSpec<>(350, 100, 550, 2_000);
        final Bridge bridge = new GenericFlyover().bridge(s);
        assertEquals(550, bridge.course().at(0).up() - bridge.depth(), 1e-9, "5.5 m under its deck");
        final double middle = bridge.course().length() / 2;
        assertTrue(bridge.piers().stream().allMatch(p -> Math.abs(p - middle) - Bridge.SINGLE_ALONG / 2 >= 1_000),
                "its piers clear of what it crosses");
        final Structure built = new GenericFlyover().build(s);
        assertEquals(List.of(), built.problems());
        assertEquals(2, built.solids(new Elements.Column()).size(), "two piers, a column each");
    }

    @Test
    void whatIsNotABridgeRefused() {
        assertThrows(IllegalArgumentException.class, () -> new ViaductSpec<>(3, 370, 300, 30_000, 300, 3_000,
                Piers.TWIN));
        assertThrows(IllegalArgumentException.class, () -> new ViaductSpec<>(3, 370, 300, 30_000, 1_000, 9_000,
                Piers.TWIN));
        assertThrows(IllegalArgumentException.class, () -> new ViaductSpec<>(3, 370, 300, 30_000, 1_000, 3_000,
                null));
        assertThrows(IllegalArgumentException.class, () -> new FlyoverSpec<>(350, 100, 300, 2_000));
        assertThrows(IllegalArgumentException.class, () -> new FlyoverSpec<>(350, 100, 550, 9_000));
    }

    /** The middle of {@code s} each way as {@code way} reads its corners. */
    private double middle(final Solid s, final java.util.function.ToDoubleFunction<Point> way) {
        return (s.corners().stream().mapToDouble(way).min().orElseThrow()
                + s.corners().stream().mapToDouble(way).max().orElseThrow()) / 2;
    }

    private double foot(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).min().orElseThrow();
    }

    private double top(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
