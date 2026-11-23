package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/**
 * A region of the body to be simulated soft (finite elements), a box in the body's frame as it is bound: what lies in it
 * moves, what lies out of it holds, a muscle cut by its faces fixed where it is cut.
 *
 * @param lo its lowest corner, cm
 * @param hi its highest corner, cm
 */
public record SoftRegion(V3 lo, V3 hi) {

    /** Whether {@code p} is in it. */
    public boolean has(final V3 p) {
        return p.x() >= lo.x() && p.y() >= lo.y() && p.z() >= lo.z() && p.x() <= hi.x() && p.y() <= hi.y()
                && p.z() <= hi.z();
    }

    /**
     * How far along from {@code a}, in it, to {@code b}, out of it, the line leaves it: a share of the way, where it
     * crosses the face it goes out through.
     */
    public double leaves(final V3 a, final V3 b) {
        double t = 1;
        final double[] from = {a.x(), a.y(), a.z()};
        final double[] to = {b.x(), b.y(), b.z()};
        final double[] low = {lo.x(), lo.y(), lo.z()};
        final double[] high = {hi.x(), hi.y(), hi.z()};
        for (int c = 0; c < 3; c++) {
            final double d = to[c] - from[c];
            if (to[c] > high[c] && d > 0) {
                t = Math.min(t, (high[c] - from[c]) / d);
            } else if (to[c] < low[c] && d < 0) {
                t = Math.min(t, (low[c] - from[c]) / d);
            }
        }
        return Math.max(0, t);
    }
}
