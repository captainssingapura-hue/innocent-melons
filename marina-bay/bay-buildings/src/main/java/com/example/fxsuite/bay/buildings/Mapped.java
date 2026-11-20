package com.example.fxsuite.bay.buildings;

import java.util.ArrayList;
import java.util.List;

/**
 * A building's outline as the map draws it, made a tower's own: about its middle (where the outline's area balances),
 * turned so its way in runs across on its south, cm; with where that middle stands in the bay
 * ({@link com.example.fxsuite.bay.land.MarinaBay}'s frame, cm) and which way it is turned there, as a
 * {@link com.example.fxsuite.avatar.buildings.Standing} turns a thing. For a tower whose owner publishes no plans with their lengths written on them: its outline is the
 * map's, nothing more.
 *
 * <p>The map's outlines are worked out from OpenStreetMap, as the bay's land is: map data (c) OpenStreetMap
 * contributors, available under the Open Database License (ODbL); the points given here are a database derived from it,
 * and are so licensed.
 *
 * @param outline its outline in its own frame, anticlockwise, its way in the edge from its first point to its second
 * @param x       where its middle stands in the bay, across
 * @param z       where its middle stands in the bay, along
 * @param yaw     which way it is turned there, degrees
 */
record Mapped(List<double[]> outline, double x, double z, double yaw) {

    Mapped {
        outline = outline.stream().map(double[]::clone).toList();
    }

    /**
     * From the map's {@code points} (m, the bay's frame, x and z), anticlockwise as it is seen from over it, its way in
     * on the edge from the first to the second.
     */
    static Mapped of(final double[][] points) {
        final int n = points.length;
        double area = 0;
        double mx = 0;
        double mz = 0;
        for (int k = 0; k < n; k++) {
            final double[] p = points[k];
            final double[] q = points[(k + 1) % n];
            final double cross = p[0] * q[1] - q[0] * p[1];
            area += cross;
            mx += (p[0] + q[0]) * cross;
            mz += (p[1] + q[1]) * cross;
        }
        area /= 2;
        if (n < 3 || area <= 0) {
            throw new IllegalArgumentException("an outline, anticlockwise");
        }
        mx /= 6 * area;
        mz /= 6 * area;
        // its way in running across: the tower's across (cos, -sin) along that edge
        final double dx = points[1][0] - points[0][0];
        final double dz = points[1][1] - points[0][1];
        final double length = Math.hypot(dx, dz);
        final double c = dx / length;
        final double s = -dz / length;
        final List<double[]> outline = new ArrayList<>();
        for (final double[] p : points) {
            final double ex = p[0] - mx;
            final double ez = p[1] - mz;
            outline.add(new double[] {Math.round((c * ex - s * ez) * 100), Math.round((s * ex + c * ez) * 100)});
        }
        // its way in's two ends level, as a tower's front must be
        final double front = Math.round((outline.get(0)[1] + outline.get(1)[1]) / 2);
        outline.get(0)[1] = front;
        outline.get(1)[1] = front;
        return new Mapped(outline, mx * 100, mz * 100, Math.toDegrees(Math.atan2(s, c)));
    }
}
