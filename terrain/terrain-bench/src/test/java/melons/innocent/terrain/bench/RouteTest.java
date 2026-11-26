package melons.innocent.terrain.bench;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A route through its waypoints, smoothly: through each, across, along and up; as long as from waypoint to waypoint
 * over the map; held at its ends; going the way it goes; never higher between two waypoints than the higher of them
 * nor lower than the lower.
 */
class RouteTest {

    private final Route route = new Route(List.of(new Waypoint(0, 0, 1_000), new Waypoint(0, 30_000, 5_000),
            new Waypoint(40_000, 30_000, 2_000)));

    @Test
    void throughEachWaypointAsLongAsFromOneToTheNextOverTheMap() {
        assertEquals(70_000, route.length(), 1e-9);
        assertEquals(new Waypoint(0, 0, 1_000), route.at(0));
        assertEquals(0, route.at(30_000).x(), 1e-9);
        assertEquals(30_000, route.at(30_000).z(), 1e-9);
        assertEquals(5_000, route.at(30_000).height(), 1e-9);
        assertEquals(route.at(70_000), route.at(1e9), "held at its end");
        assertThrows(IllegalArgumentException.class, () -> new Route(List.of(new Waypoint(0, 0, 0))));
        assertThrows(IllegalArgumentException.class, () -> new Route(List.of(new Waypoint(0, 0, 0),
                new Waypoint(0, 0, 100))));
    }

    @Test
    void goingTheWayItGoesNeverPastItsWaypointsHeights() {
        assertEquals(0, route.heading(10_000), 1, "along the map at first");
        assertEquals(90, route.heading(60_000), 1, "across it at last");
        for (double d = 0; d <= 30_000; d += 500) {
            final double h = route.at(d).height();
            assertTrue(h >= 1_000 - 1e-6 && h <= 5_000 + 1e-6, "between its first two heights at " + d);
        }
        for (double d = 30_000; d <= 70_000; d += 500) {
            final double h = route.at(d).height();
            assertTrue(h >= 2_000 - 1e-6 && h <= 5_000 + 1e-6, "between its last two heights at " + d);
        }
    }
}
