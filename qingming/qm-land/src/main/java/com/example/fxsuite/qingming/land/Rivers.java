package com.example.fxsuite.qingming.land;

import com.example.fxsuite.avatar.sim.Channel;
import com.example.fxsuite.avatar.sim.Vec2;

import java.util.ArrayList;
import java.util.List;

/**
 * The Bian River through the reach ({@link Reach}): its middle a smooth line through the points the scroll's plan is
 * read as, from beyond the east edge, under the bridge at the origin running west, bending away south before the
 * gate, out over the south edge; its channel cut into the plain. In centimetres.
 */
public final class Rivers {

    private Rivers() {
    }

    /** Its middle, the points it passes, east to west then south (m); square across the bridge at the origin. */
    private static final double[][] THROUGH = {
            {520, 60}, {350, 40}, {200, 15}, {80, 2}, {0, 0}, {-80, -4}, {-170, -28}, {-240, -85}, {-280, -165},
            {-295, -270}};
    /** Its bed so wide, level; its banks so far across from the bed's edge to the plain's top; so deep under it. */
    public static final double BED = 1_600;
    public static final double BANK = 300;
    public static final double DEPTH = 400;
    /** Its water so deep over its bed: the 6 chi (0.31 m each) the Song history has it held at for loaded boats. */
    public static final double WATER = 186;
    /** Its width between its banks' tops: as wide as the bridge's span. */
    public static final double TOP = BED + 2 * BANK;
    /** A towpath's middle so far from the river's: past the bank's top, half its width and a little more. */
    static final double TOWPATH = TOP / 2 + 230 + 70;

    /** Its middle, a point every 5 m along it: a smooth curve through {@link #THROUGH} (Catmull-Rom). */
    public static List<Vec2> line() {
        final List<Vec2> line = new ArrayList<>();
        for (int k = 0; k + 1 < THROUGH.length; k++) {
            final double[] p0 = THROUGH[Math.max(0, k - 1)];
            final double[] p1 = THROUGH[k];
            final double[] p2 = THROUGH[k + 1];
            final double[] p3 = THROUGH[Math.min(THROUGH.length - 1, k + 2)];
            final int steps = (int) Math.ceil(Math.hypot(p2[0] - p1[0], p2[1] - p1[1]) / 5);
            for (int s = 0; s < steps; s++) {
                final double t = (double) s / steps;
                line.add(new Vec2(spline(p0[0], p1[0], p2[0], p3[0], t) * 100,
                        spline(p0[1], p1[1], p2[1], p3[1], t) * 100));
            }
        }
        final double[] last = THROUGH[THROUGH.length - 1];
        line.add(new Vec2(last[0] * 100, last[1] * 100));
        return line;
    }

    private static double spline(final double p0, final double p1, final double p2, final double p3, final double t) {
        return 0.5 * (2 * p1 + (p2 - p0) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t
                + (3 * p1 - p0 - 3 * p2 + p3) * t * t * t);
    }

    /** Its channel, cut into the plain along its middle. */
    public static Channel channel() {
        return new Channel(line(), BED, BANK, DEPTH);
    }

    /**
     * Along its bank on its right going downstream ({@code right}), or its left, so far from its middle: a point every
     * 5 m, each square off the river from the one it is beside.
     */
    public static List<Vec2> beside(final boolean right, final double off) {
        final List<Vec2> line = line();
        final List<Vec2> beside = new ArrayList<>();
        for (int k = 0; k < line.size(); k++) {
            final Vec2 a = line.get(Math.max(0, k - 1));
            final Vec2 b = line.get(Math.min(line.size() - 1, k + 1));
            final double dx = b.x() - a.x();
            final double dz = b.z() - a.z();
            final double l = Math.hypot(dx, dz);
            // going downstream, its left (-dz, dx), its right (dz, -dx)
            final double side = right ? 1 : -1;
            beside.add(new Vec2(line.get(k).x() + side * dz / l * off, line.get(k).z() - side * dx / l * off));
        }
        return beside;
    }
}
