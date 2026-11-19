package com.example.fxsuite.life.body;

import java.util.List;

/**
 * A part taken hold of and moved: turned at its own joint, each way it turns as far as it takes to bring the point
 * held as near as it will come to where it is drawn to, no way further than the joint may go. Swung, or, asked to,
 * twisted about its own length. Solved by small steps, each the least-squares best of its ways at once (damped
 * Gauss-Newton), the body hanging free from its pelvis while it is.
 */
public final class Drag {

    /** How many steps it takes at most; how near is near enough, cm; the nudge each way is measured by, degrees. */
    static final int STEPS = 10;
    static final double NEAR = 0.05;
    static final double NUDGE = 0.25;

    private Drag() {
    }

    /** The ways {@code part} turns when dragged: its swings, or, {@code twist}ing, its twists. */
    static List<Dof> ways(final Part part, final boolean twist) {
        return part.dofs().stream().filter(d -> d.twist() == twist).toList();
    }

    /**
     * {@code pose}, with {@code part} turned at its joint to bring {@code held} (a point in its own frame, as on the
     * right side) as near {@code to} (in the body's frame, hanging free) as it will come.
     */
    public static Pose toward(final Proportions proportions, final Pose pose, final Part part, final V3 held,
                              final V3 to, final boolean twist) {
        final List<Dof> ways = ways(part, twist);
        Pose p = pose;
        if (ways.isEmpty()) {
            return p;
        }
        final int n = ways.size();
        for (int step = 0; step < STEPS; step++) {
            final V3 at = new Rig(proportions, p, false).held(part, held);
            final V3 miss = to.minus(at);
            if (miss.length() < NEAR) {
                break;
            }
            // how far the point moves for a nudge each way
            final V3[] j = new V3[n];
            for (int k = 0; k < n; k++) {
                final Dof d = ways.get(k);
                final double v = p.get(part, d);
                final double h = v + NUDGE <= d.max() ? NUDGE : -NUDGE;
                final V3 moved = new Rig(proportions, p.with(part, d, v + h), false).held(part, held);
                j[k] = moved.minus(at).times(1 / h);
            }
            final double[][] a = new double[n][n];
            final double[] b = new double[n];
            double trace = 0;
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    a[r][c] = j[r].dot(j[c]);
                }
                b[r] = j[r].dot(miss);
                trace += a[r][r];
            }
            final double damping = 0.02 * trace / n + 1e-9;
            for (int r = 0; r < n; r++) {
                a[r][r] += damping;
            }
            final double[] step0 = solve(a, b);
            Pose next = p;
            for (int k = 0; k < n; k++) {
                final Dof d = ways.get(k);
                // no more than a few tens of degrees a step, so it does not leap past
                next = next.with(part, d, p.get(part, d) + Math.clamp(step0[k], -30, 30));
            }
            if (next.equals(p)) {
                break;
            }
            p = next;
        }
        return p;
    }

    /** {@code x} with {@code a x = b}, {@code a} square and small: by elimination. */
    static double[] solve(final double[][] a, final double[] b) {
        final int n = b.length;
        final double[][] m = new double[n][n + 1];
        for (int r = 0; r < n; r++) {
            System.arraycopy(a[r], 0, m[r], 0, n);
            m[r][n] = b[r];
        }
        for (int c = 0; c < n; c++) {
            int best = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(m[r][c]) > Math.abs(m[best][c])) {
                    best = r;
                }
            }
            final double[] t = m[c];
            m[c] = m[best];
            m[best] = t;
            if (Math.abs(m[c][c]) < 1e-12) {
                continue;
            }
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    final double f = m[r][c] / m[c][c];
                    for (int k = c; k <= n; k++) {
                        m[r][k] -= f * m[c][k];
                    }
                }
            }
        }
        final double[] x = new double[n];
        for (int r = 0; r < n; r++) {
            x[r] = Math.abs(m[r][r]) < 1e-12 ? 0 : m[r][n] / m[r][r];
        }
        return x;
    }
}
