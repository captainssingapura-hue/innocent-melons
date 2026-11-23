package melons.innocent.terrain.bench;

import hue.captains.singapura.tao.ontology.Scalar;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.List;

/**
 * A route over the land: through its waypoints in order, smoothly, how far across, how far along and how high each a
 * monotone curve over how far along the route it has come, the distance from waypoint to waypoint taken over the map;
 * so it passes through every waypoint, turns without a kink, and never rises past a waypoint's height between two
 * lower ones nor sinks past it between two higher. Two waypoints at least, no two in a row over the same spot.
 *
 * @param waypoints what it goes through, in order
 */
public record Route(List<Waypoint> waypoints) implements Scalar {

    /** How far either side of a point its way is taken, for which way it goes there, in centimetres. */
    private static final double SPAN = 50;

    public Route {
        waypoints = List.copyOf(waypoints);
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("a route through two waypoints at least, not " + waypoints.size());
        }
        for (int k = 1; k < waypoints.size(); k++) {
            if (!(waypoints.get(k).over(waypoints.get(k - 1)) > 0)) {
                throw new IllegalArgumentException("a route going on from " + waypoints.get(k) + ", not staying");
            }
        }
    }

    /** How long it is, from waypoint to waypoint over the map. */
    public double length() {
        double length = 0;
        for (int k = 1; k < waypoints.size(); k++) {
            length += waypoints.get(k).over(waypoints.get(k - 1));
        }
        return length;
    }

    /** Where it is {@code distance} along, held at its ends. */
    public Waypoint at(final double distance) {
        final double s = Math.clamp(distance, 0, length());
        return new Waypoint(curve(0).at(s), curve(1).at(s), curve(2).at(s));
    }

    /** Which way it goes {@code distance} along, over the map: how far round from looking along it, in degrees. */
    public double heading(final double distance) {
        final double s = Math.clamp(distance, SPAN, Math.max(SPAN, length() - SPAN));
        final Waypoint before = at(s - SPAN);
        final Waypoint after = at(s + SPAN);
        return Math.toDegrees(Math.atan2(after.x() - before.x(), after.z() - before.z()));
    }

    /** How far across ({@code which} 0), along (1) or high (2) it is, over how far along it has come. */
    private Monotone curve(final int which) {
        final List<Knot> knots = new ArrayList<>();
        double s = 0;
        for (int k = 0; k < waypoints.size(); k++) {
            if (k > 0) {
                s += waypoints.get(k).over(waypoints.get(k - 1));
            }
            final Waypoint w = waypoints.get(k);
            knots.add(new Knot(s, which == 0 ? w.x() : which == 1 ? w.z() : w.height()));
        }
        return new Monotone(new Knots(knots));
    }
}
