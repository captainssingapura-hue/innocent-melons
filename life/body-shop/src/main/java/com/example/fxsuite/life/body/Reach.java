package com.example.fxsuite.life.body;

import java.util.List;

/**
 * An arm reaching: its joints turned to bring a point of its hand to a point, each kept as near as it may be to how it
 * is held easily, so of the many ways to reach a point, the easy one. Solved by small steps, each the least-squares
 * best of every way at once, a miss weighed against how far each joint is from easy (damped Gauss-Newton).
 */
public final class Reach {

    /** A way a joint turns, how it is held easily, and how much being away from that costs, cm² a degree². */
    public record Way(Part part, Dof dof, double easy, double cost) {
    }

    static final int STEPS = 8;
    static final double NUDGE = 0.5;

    private Reach() {
    }

    /** The right arm's ways, or the left's: its shoulder blade forward, its upper arm's three, its elbow. */
    public static List<Way> arm(final int side, final double forward, final double sideways, final double elbow) {
        final Part girdle = side > 0 ? Part.RIGHT_GIRDLE : Part.LEFT_GIRDLE;
        final Part upper = side > 0 ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
        final Part fore = side > 0 ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM;
        return List.of(new Way(girdle, Dof.PROTRACT, 0, 0.02), new Way(upper, Dof.RAISE_FORWARD, forward, 0.002),
                new Way(upper, Dof.RAISE_SIDEWAYS, sideways, 0.003), new Way(upper, Dof.ARM_ROTATION, 0, 0.006),
                new Way(fore, Dof.ELBOW, elbow, 0.001));
    }

    /**
     * {@code pose} with its {@code ways} turned to bring {@code held}, a point of {@code part} in its own frame, to
     * {@code to} in the body's (standing on the ground as {@code pose} has it); starting from how {@code pose} holds them.
     */
    public static Pose toward(final Proportions proportions, final Pose pose, final List<Way> ways, final Part part,
                              final V3 held, final V3 to) {
        Pose p = pose;
        final int n = ways.size();
        for (int step = 0; step < STEPS; step++) {
            final V3 at = new Rig(proportions, p).held(part, held);
            final V3 miss = to.minus(at);
            final V3[] j = new V3[n];
            for (int k = 0; k < n; k++) {
                final Way w = ways.get(k);
                final double v = p.get(w.part(), w.dof());
                final double h = v + NUDGE <= w.dof().max() ? NUDGE : -NUDGE;
                j[k] = new Rig(proportions, p.with(w.part(), w.dof(), v + h)).held(part, held).minus(at)
                        .times(1 / h);
            }
            final double[][] a = new double[n][n];
            final double[] b = new double[n];
            for (int r = 0; r < n; r++) {
                final Way w = ways.get(r);
                for (int c = 0; c < n; c++) {
                    a[r][c] = j[r].dot(j[c]);
                }
                a[r][r] += w.cost() + 1e-4;
                b[r] = j[r].dot(miss) + w.cost() * (w.easy() - p.get(w.part(), w.dof()));
            }
            final double[] d = Drag.solve(a, b);
            Pose next = p;
            double moved = 0;
            for (int k = 0; k < n; k++) {
                final Way w = ways.get(k);
                final double step0 = Math.clamp(d[k], -25, 25);
                next = next.with(w.part(), w.dof(), p.get(w.part(), w.dof()) + step0);
                moved = Math.max(moved, Math.abs(step0));
            }
            p = next;
            if (moved < 0.05) {
                break;
            }
        }
        return p;
    }
}
