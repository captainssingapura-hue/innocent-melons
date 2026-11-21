package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;

/**
 * A ring of a cage joined to the next, twice as many points round, as a wrist's to a palm's: each point of the first
 * facing two of the second, a five-sided face between each two points of the first and the three of the second they
 * face, the second ring read round the way the first is, its point nearest round the first's first facing it; each
 * face round the way a limb's faces go, the first ring nearer the body. Which of the second's points faces the first's
 * first, and which way each is read round, found from the ways out each ring's points were set along, not from where
 * they came to lie: every body's the same faces, whatever its shape.
 */
final class Zip {

    private final Building b;

    Zip(final Building b) {
        this.b = b;
    }

    /**
     * Ring {@code a} joined to ring {@code c}, twice as many points round, read round {@code axis}, their points set
     * out along {@code aWays} and {@code cWays}.
     */
    void join(final int[] a, final int[] c, final V3 axis, final V3[] aWays, final V3[] cWays) {
        final V3 w = axis.cross(V3.X.cross(axis).length() > 1e-6 ? V3.X : V3.Y).unit();
        final V3 u = axis.cross(w).unit();
        final double[] ra = unwrapped(aWays, u, w);
        final double[] rc = unwrapped(cWays, u, w);
        final boolean same = Math.signum(ra[a.length] - ra[0]) == Math.signum(rc[c.length] - rc[0]);
        final int[] cc = same ? c : reversed(c);
        final double[] rcc = unwrapped(same ? cWays : reversed(cWays), u, w);
        int start = 0;
        for (int j = 1; j < cc.length; j++) {
            start = Math.abs(Math.IEEEremainder(rcc[j] - ra[0], 2 * Math.PI))
                    < Math.abs(Math.IEEEremainder(rcc[start] - ra[0], 2 * Math.PI)) ? j : start;
        }
        final int n = a.length;
        for (int k = 0; k < n; k++) {
            b.face(cc[(start + 2 * k) % cc.length], cc[(start + 2 * k + 1) % cc.length],
                    cc[(start + 2 * k + 2) % cc.length], a[(k + 1) % n], a[k]);
        }
    }

    /** How far round each of {@code ways} is, on from the one before, its first again at the end. */
    private double[] unwrapped(final V3[] ways, final V3 u, final V3 w) {
        final double[] r = new double[ways.length + 1];
        for (int k = 0; k <= ways.length; k++) {
            final V3 v = ways[k % ways.length];
            r[k] = Math.atan2(v.dot(u), v.dot(w));
            if (k > 0) {
                r[k] = r[k - 1] + Math.IEEEremainder(r[k] - r[k - 1], 2 * Math.PI);
            }
        }
        return r;
    }

    private int[] reversed(final int[] ring) {
        final int[] r = new int[ring.length];
        for (int k = 0; k < ring.length; k++) {
            r[k] = ring[ring.length - 1 - k];
        }
        return r;
    }

    private V3[] reversed(final V3[] ring) {
        final V3[] r = new V3[ring.length];
        for (int k = 0; k < ring.length; k++) {
            r[k] = ring[ring.length - 1 - k];
        }
        return r;
    }
}
