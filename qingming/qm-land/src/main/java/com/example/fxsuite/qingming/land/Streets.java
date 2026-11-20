package com.example.fxsuite.qingming.land;

import com.example.fxsuite.avatar.sim.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The reach's roads as a road map's text ({@code RoadMap.parse}), in metres: the road from the country over the bridge
 * and along the street to the gate, the stretch over the river a bridge; a towpath along each bank either side of the
 * bridge, each stopping short of it and of the world's edges. Of earth, all of them.
 */
final class Streets {

    private Streets() {
    }

    /** The road: in from the east on the south bank, square onto the bridge, over it, round west along the street. */
    private static final double[][] ROAD = {
            {500, -60}, {300, -48}, {150, -40}, {60, -36}, {10, -32}, {0, -26}, {0, -16}};
    private static final double[][] STREET = {
            {0, 16}, {0, 24}, {-12, 34}, {-100, 42}, {-300, 42}, {-450, 36}, {Reach.WALL / 100, 30}, {-598, 30}};
    /** How far from the bridge's middle a towpath stops, and from the world's edges. */
    private static final double SHORT = 2_500;
    private static final double EDGE = 200;

    /** Its kinds; the road; its towpaths. */
    static String text() {
        final StringBuilder text = new StringBuilder("""
                kind road width=8m surface=earth grade=8% curve=1/10m corners slope=1:2 camber=2% transition=6m vertical=30m
                kind towpath width=4.6m surface=earth grade=8% curve=1/8m corners slope=1:2 camber=0% transition=6m vertical=20m
                road "the road over the bridge" kind=road
                """);
        point(text, "from", ROAD[0][0], ROAD[0][1], "");
        for (int k = 1; k < ROAD.length; k++) {
            point(text, "to", ROAD[k][0], ROAD[k][1], "");
        }
        point(text, "to", STREET[0][0], STREET[0][1], " bridge");
        point(text, "to", STREET[1][0], STREET[1][1], " ground");
        for (int k = 2; k < STREET.length; k++) {
            point(text, "to", STREET[k][0], STREET[k][1], "");
        }
        int n = 0;
        for (final boolean right : new boolean[] {true, false}) {
            for (final List<Vec2> stretch : stretches(Rivers.beside(right, Rivers.TOWPATH))) {
                text.append("road \"towpath ").append(++n).append("\" kind=towpath\n");
                for (int k = 0; k < stretch.size(); k += 2) {
                    point(text, k == 0 ? "from" : "to", stretch.get(k).x() / 100, stretch.get(k).z() / 100, "");
                }
            }
        }
        return text.toString();
    }

    /** {@code line} cut where it comes near the bridge or the world's edges: its stretches, each two points or more. */
    private static List<List<Vec2>> stretches(final List<Vec2> line) {
        final List<List<Vec2>> stretches = new ArrayList<>();
        List<Vec2> stretch = new ArrayList<>();
        for (final Vec2 p : line) {
            final boolean keep = Math.hypot(p.x(), p.z()) > SHORT && p.x() > Reach.WEST + EDGE
                    && p.x() < Reach.EAST - EDGE && p.z() > Reach.SOUTH + EDGE && p.z() < Reach.NORTH - EDGE;
            if (keep) {
                stretch.add(p);
            } else if (!stretch.isEmpty()) {
                stretches.add(stretch);
                stretch = new ArrayList<>();
            }
        }
        stretches.add(stretch);
        return stretches.stream().filter(s -> s.size() >= 4).toList();
    }

    private static void point(final StringBuilder text, final String word, final double x, final double z,
                              final String more) {
        text.append(String.format(Locale.ROOT, "  %s %.1f,%.1f%s%n", word, x, z, more));
    }
}
