package melons.innocent.road.surface;

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
 * Roads' surfaces, each kind as its specification asks: its strips across, its pavement falling from its line to its
 * edges, its lowest edges just over the ground; the lines painted on it where its strips meet; its kerbs and its
 * barrier standing over the pavement; nothing wrong with any of it as a thing built; and what is not a road's refused.
 */
class SurfacesTest {

    private final RoadSpec<Civilization, CommonEras.Contemporary> road = new RoadSpec<>(350, 100, 10_000);

    @Test
    void aRoadTwoLanesAShoulderBesideEachFallingFromItsCrown() {
        final Section section = new RoadSurface().section(road);
        assertEquals(900, section.wide(), 1e-9);
        assertEquals(Section.LOWEST, section.top(-450), 1e-9, "its edges just over the ground");
        assertEquals(Section.LOWEST + 0.025 * 450, section.top(0), 1e-9, "its crown on its line");
        final Structure built = new RoadSurface().build(road);
        assertEquals(List.of(), built.problems());
        assertEquals(2 * 10, built.solids(new Elements.TrafficLane()).size(), "two lanes, in 10 m pieces");
        assertEquals(2 * 10, built.solids(new Elements.Shoulder()).size());
        // the centre line, 3 m strokes every 6 m, and an unbroken edge line along each lane's outer edge
        assertEquals(17 + 2, built.solids(new Elements.Marking()).size());
        assertTrue(built.solids(new Elements.Marking()).stream().allMatch(s -> s.tint().isPresent()), "in white");
    }

    @Test
    void aStreetItsLanesBetweenKerbsStandingOverThePavement() {
        final Structure built = new StreetSurface().build(new StreetSpec<>(2, 325, 5_000));
        assertEquals(List.of(), built.problems());
        final List<Solid> kerbs = built.solids(new Elements.Kerb());
        assertEquals(2 * 5, kerbs.size());
        final Section section = new StreetSurface().section(new StreetSpec<>(2, 325, 5_000));
        final double edge = section.top(section.edge(1));
        assertTrue(kerbs.stream().allMatch(k -> Math.abs(top(k) - edge - Pieces.KERB) < 1e-9), "15 cm over it");
        // lane lines between the lanes going each way, the centre line, and an edge line along each kerb
        assertEquals(2 * 6 + 9 + 2, built.solids(new Elements.Marking()).size());
    }

    @Test
    void aCountryLaneOneLaneNothingPaintedItsCrownOnItsLine() {
        final Structure built = new LaneSurface().build(new LaneSpec<>(300, 2_000));
        assertEquals(List.of(), built.problems());
        assertTrue(built.solids(new Elements.Marking()).isEmpty());
        assertEquals(2 * 2, built.solids(new Elements.TrafficLane()).size(), "parted at its crown");
    }

    @Test
    void anExpresswayItsCarriagewaysEitherSideOfABarrierEachFallingAwayFromIt() {
        final ExpresswaySpec<Civilization, CommonEras.Contemporary> s = new ExpresswaySpec<>(3, 370, 300, 10_000);
        final Section section = new ExpresswaySurface().section(s);
        assertEquals(2 * (300 + 3 * 370 + 60) + 60, section.wide(), 1e-9);
        assertEquals(Section.LOWEST, section.top(section.edge(0)), 1e-9);
        final Structure built = new ExpresswaySurface().build(s);
        assertEquals(List.of(), built.problems());
        final List<Solid> barrier = built.solids(new Elements.Barrier());
        assertEquals(10, barrier.size());
        assertTrue(barrier.stream().allMatch(b -> top(b) - section.top(0) >= Pieces.BARRIER - 1e-9));
        // two lane lines each way, 3 m strokes every 9 m, and an edge line either side of each carriageway
        assertEquals(2 * 2 * 12 + 4, built.solids(new Elements.Marking()).size());
        assertEquals(List.of(new Structure.WayIn(-1_500 + 300 + 185, 5_000, 0)), built.waysIn(),
                "come onto at its far end, in its first lane");
    }

    @Test
    void whatIsNotARoadRefused() {
        assertThrows(IllegalArgumentException.class, () -> new RoadSpec<>(200, 100, 10_000));
        assertThrows(IllegalArgumentException.class, () -> new RoadSpec<>(350, 100, 500));
        assertThrows(IllegalArgumentException.class, () -> new StreetSpec<>(4, 325, 5_000));
        assertThrows(IllegalArgumentException.class, () -> new LaneSpec<>(500, 5_000));
        assertThrows(IllegalArgumentException.class, () -> new ExpresswaySpec<>(1, 370, 300, 5_000));
        assertThrows(IllegalArgumentException.class, () -> new Strip(Strip.Kind.LANE, 0));
    }

    private double top(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
