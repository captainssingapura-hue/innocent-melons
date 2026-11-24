package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ramps: a spiral one, its road following its deck round and up, laid in pieces each stepped up onto from the one
 * before; each turn held up on beams from its columns to its core, the room under each turn left over the one under
 * it, its columns standing on the ground; the same on two rings of columns; a straight one climbing from the ground on
 * its piers, the lowest a plinth, its columns on the ground, its road at its top as high as asked; and what is not a
 * ramp refused.
 */
class RampsTest {

    private final SpiralRampSpec<Civilization, CommonEras.Contemporary> spiral = new SpiralRampSpec<>(2, 350, 100,
            1_600, 800, 2.5, Carried.CORE);

    @Test
    void aSpiralRampItsRoadFollowingItsDeckRoundAndUp() {
        final Spiral built = new GenericSpiralRamp().spiral(spiral);
        final Helix helix = built.helix();
        assertTrue(helix.grade() * helix.piece() <= Helix.RISE + 1e-9, "each piece stepped up onto");
        final Structure structure = built.structure();
        assertEquals(List.of(), structure.problems());
        final double wide = built.section().wide();
        for (final Solid lane : structure.solids(new Elements.TrafficLane())) {
            for (final Point p : lane.corners()) {
                final double r = Math.hypot(p.across(), p.along());
                assertTrue(r >= 1_600 - wide / 2 - 1e-6 && r <= 1_600 + wide / 2 + 1e-6, "round its middle: " + r);
            }
        }
        final double top = structure.waysIn().getFirst().level();
        assertEquals(2.5 * 800, top, 1e-6, "come onto at its top, two and a half turns up");
    }

    @Test
    void eachTurnHeldUpOverTheOneUnderIt() {
        final Spiral built = new GenericSpiralRamp().spiral(spiral);
        final Structure structure = built.structure();
        assertEquals(8, built.stations().size(), "seven round, and one where its top ends");
        final List<Solid> columns = structure.solids(new Elements.Column());
        assertEquals(8, columns.size(), "a column outside its outer edge at each station");
        assertTrue(columns.stream().allMatch(c -> low(c) == 0), "standing on the ground");
        for (final Solid c : columns) {
            final double r = Math.hypot(middle(c, Point::across), middle(c, Point::along));
            assertTrue(r > 1_600 + built.deck().half(), "clear of its outer edge");
        }
        assertTrue(built.core() > 0 && built.core() < 1_600 - built.deck().half(), "its core inside its inner edge");
        assertTrue(!structure.solids(new Elements.BearingWall()).isEmpty());
        final List<Solid> beams = structure.solids(new Elements.Beam());
        for (final Solid upper : beams) {
            for (final Solid lane : structure.solids(new Elements.TrafficLane())) {
                if (over(upper, lane)) {
                    assertTrue(low(upper) - high(lane) >= 450, "room under each turn over the one under it");
                }
            }
        }
        final List<Solid> rings = new GenericSpiralRamp().build(new SpiralRampSpec<>(2, 350, 100, 1_600, 800, 2.5,
                Carried.COLUMNS)).solids(new Elements.Column());
        assertEquals(2 * 8, rings.size(), "or on two rings of columns, one inside its inner edge");
    }

    @Test
    void aStraightRampClimbingFromTheGroundOnItsPiers() {
        final RampSpec<Civilization, CommonEras.Contemporary> s = new RampSpec<>(1, 350, 100, 800, 0.05, 2_000,
                Piers.SINGLE);
        final Structure built = new GenericRamp().build(s);
        assertEquals(List.of(), built.problems());
        assertEquals(16_000, s.length(), 1e-9);
        final List<Solid> columns = built.solids(new Elements.Column());
        assertTrue(columns.stream().allMatch(c -> low(c) == 0), "on the ground");
        assertEquals(7 - 1, columns.size() + plinths(built),
                "a pier between each two of its eight spans, but the first, where its deck lies on the ground");
        assertTrue(plinths(built) >= 1, "the lowest a plinth");
        assertEquals(800, built.waysIn().getFirst().level(), 1e-6, "its road at its top as high as asked");
        final Solid lowest = built.solids(new Elements.TrafficLane()).stream()
                .min((a, b) -> Double.compare(low(a), low(b))).orElseThrow();
        assertEquals(0, low(lowest), 1e-9, "its road starting on the ground");
    }

    @Test
    void whatIsNotARampRefused() {
        assertThrows(IllegalArgumentException.class, () -> new SpiralRampSpec<>(2, 350, 100, 1_400, 1_200, 2,
                Carried.CORE), "too steep");
        assertThrows(IllegalArgumentException.class, () -> new SpiralRampSpec<>(3, 350, 100, 1_600, 800, 2,
                Carried.CORE));
        assertThrows(IllegalArgumentException.class, () -> new SpiralRampSpec<>(2, 350, 100, 1_600, 800, 2, null));
        assertThrows(IllegalArgumentException.class, () -> new RampSpec<>(1, 350, 100, 800, 0.12, 2_000,
                Piers.SINGLE));
    }

    private long plinths(final Structure built) {
        return built.solids(new Elements.Abutment()).stream().filter(a -> a.name().equals("plinth")).count();
    }

    /** Whether {@code a} lies over {@code b}, their middles seen from above within a metre of each other. */
    private boolean over(final Solid a, final Solid b) {
        return Math.hypot(middle(a, Point::across) - middle(b, Point::across),
                middle(a, Point::along) - middle(b, Point::along)) < 100 && low(a) > high(b);
    }

    private double middle(final Solid s, final java.util.function.ToDoubleFunction<Point> way) {
        return s.corners().stream().mapToDouble(way).average().orElseThrow();
    }

    private double low(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).min().orElseThrow();
    }

    private double high(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
