package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.Course;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An elevated road: its straight ramp's end where its viaduct begins, its spiral's top where its viaduct ends, each
 * deck's edges meeting the next's, as high, so nothing is open between them; the spiral's lower turns passing under
 * the viaduct's far end with room over them, no pier of the viaduct in their way; nothing wrong with it as a thing
 * built, come onto at the foot of its straight ramp on the ground; and what is not one refused.
 */
class ElevatedRoadTest {

    private final ElevatedRoadSpec<Civilization, CommonEras.Contemporary> road = new ElevatedRoadSpec<>(2, 350, 100,
            1_100, 0.06, 12_000, 3_000, Piers.SINGLE, 1_800, 1.5, Carried.CORE);
    private final GenericElevatedRoad built = new GenericElevatedRoad();

    @Test
    void itsRampsMeetItsViaductWithoutAGap() {
        final Bridge viaduct = built.viaduct(road);
        final Course ramp = built.ramp(road).course();
        final Course spiral = built.spiral(road).line();
        final double half = viaduct.deck().half();
        final double end = viaduct.course().length();
        for (final double x : List.of(-half, 0.0, half)) {
            for (final double y : List.of(-GenericElevatedRoad.DEPTH, 0.0)) {
                same(ramp.beside(ramp.length(), x, y), viaduct.course().beside(0, x, y), "the ramp's end its start");
                same(spiral.beside(spiral.length(), -x, y), viaduct.course().beside(end, x, y),
                        "the spiral's top its end, heading back along it");
            }
        }
        assertEquals(0, Math.cos(spiral.heading(spiral.length())) + 1, 1e-9, "heading back along it");
    }

    @Test
    void theSpiralsLowerTurnsPassUnderTheViaductsFarEnd() {
        final Bridge viaduct = built.viaduct(road);
        final Spiral spiral = built.spiral(road);
        final double under = viaduct.deck().under(0);
        final Structure structure = built.build(road);
        for (final Solid lane : structure.solids(new Elements.TrafficLane())) {
            if (high(lane) < under - 1e-6 && Math.abs(middle(lane, Point::across)) < viaduct.deck().half()
                    && Math.abs(middle(lane, Point::along)) < viaduct.course().length() / 2) {
                assertTrue(under - high(lane) >= 450, "room over a lower turn under the viaduct: " + high(lane));
            }
        }
        final double last = viaduct.piers().stream().mapToDouble(Double::doubleValue).max().orElseThrow();
        final double nearest = spiral.helix().radius() + spiral.deck().half() + Spiral.GAP + Spiral.COLUMN;
        assertTrue(viaduct.course().length() - last - Bridge.CAP_ALONG / 2 >= nearest,
                "its last pier clear of the spiral");
        assertEquals(List.of(), structure.problems());
        final Structure.WayIn in = structure.waysIn().getFirst();
        assertEquals(0, in.level(), 1e-9, "come onto on the ground");
        assertEquals(-12_000 / 2.0 - 1_100 / 0.06, in.along(), 1e-6, "at the foot of its straight ramp");
    }

    @Test
    void whatIsNotOneRefused() {
        assertThrows(IllegalArgumentException.class, () -> new ElevatedRoadSpec<>(2, 350, 100, 1_100, 0.06, 4_000,
                3_000, Piers.SINGLE, 1_800, 1.5, Carried.CORE), "too short to clear its spiral");
        assertThrows(IllegalArgumentException.class, () -> new ElevatedRoadSpec<>(2, 350, 100, 1_100, 0.06, 12_000,
                3_000, Piers.SINGLE, 1_800, 2, Carried.CORE), "turns too close over each other");
    }

    private void same(final Point a, final Point b, final String what) {
        assertEquals(b.across(), a.across(), 1e-6, what);
        assertEquals(b.along(), a.along(), 1e-6, what);
        assertEquals(b.up(), a.up(), 1e-6, what);
    }

    private double middle(final Solid s, final java.util.function.ToDoubleFunction<Point> way) {
        return s.corners().stream().mapToDouble(way).average().orElseThrow();
    }

    private double high(final Solid s) {
        return s.corners().stream().mapToDouble(Point::up).max().orElseThrow();
    }
}
