package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Keeping a body's centre of gravity where it is asked to be, as a person shifts their weight: its feet that were on
 * the ground kept where they were, each touching it by the same points; its pelvis moved (across, up or down, ahead
 * or back, never turned) and its standing legs bent at the hip, the knee and the ankle to follow, until its centre of
 * gravity is over the point asked for. Whatever else it holds as it is told to. Solved by small steps, each the
 * least-squares best of every way at once (damped Gauss-Newton), each way no further than its joint goes; a foot
 * lifted, or being moved by hand, is left free.
 */
public final class Balance {

    /** How many steps it takes at most; how much more its centre of gravity counts than a foot's point. */
    static final int STEPS = 14;
    static final double CENTRE = 3;
    /** How much every way is held to where it was: the pelvis's height more than the rest. */
    static final double HOLD = 0.02;
    static final double HOLD_HEIGHT = 0.15;
    /** The nudge each way is measured by: degrees, or cm for the pelvis. */
    static final double NUDGE = 0.25;

    /** The ways a standing leg turns to keep its foot where it was. */
    static final List<Dof> LEG = List.of(Dof.HIP_FORWARD, Dof.HIP_SIDEWAYS, Dof.KNEE, Dof.ANKLE, Dof.ANKLE_ROLL);

    private Balance() {
    }

    /** Which of a side's parts make its leg: what, being moved by hand, frees its foot. */
    static List<Part> leg(final int side) {
        return side > 0 ? List.of(Part.RIGHT_THIGH, Part.RIGHT_SHIN, Part.RIGHT_FOOT, Part.RIGHT_TOES)
                : List.of(Part.LEFT_THIGH, Part.LEFT_SHIN, Part.LEFT_FOOT, Part.LEFT_TOES);
    }

    /** One way the solve may move it: a leg's joint, or the pelvis along x, y or z. */
    private record Way(Part part, Dof dof, int axis) {

        double get(final Pose p) {
            return part == null ? (axis == 0 ? p.root().x() : axis == 1 ? p.root().y() : p.root().z())
                    : p.get(part, dof);
        }

        Pose set(final Pose p, final double v) {
            if (part != null) {
                return p.with(part, dof, v);
            }
            final V3 r = p.root();
            return p.rooted(axis == 0 ? new V3(v, r.y(), r.z()) : axis == 1 ? new V3(r.x(), v, r.z())
                    : new V3(r.x(), r.y(), v));
        }
    }

    /**
     * {@code now}, held as told, its centre of gravity brought over {@code over} (its x, its z; on the ground): its
     * feet that were on the ground in {@code before} kept there, but for a leg {@code moving} has a part of.
     */
    public static Pose keep(final Proportions proportions, final Pose before, final Pose now, final V3 over,
                            final Set<Part> moving) {
        // where the feet stood, the body hanging free from its pelvis: the points of each touching the ground
        final Rig was = new Rig(proportions, before);
        final Rig wasFree = new Rig(proportions, before, false);
        final List<Integer> planted = new ArrayList<>();
        final List<Way> ways = new ArrayList<>();
        for (int axis = 0; axis < 3; axis++) {
            ways.add(new Way(null, null, axis));
        }
        for (final int side : new int[] {1, -1}) {
            if (leg(side).stream().anyMatch(moving::contains)) {
                continue;
            }
            final int from = side > 0 ? 0 : 5;
            boolean down = false;
            for (int k = from; k < from + 5; k++) {
                if (was.contacts().get(k).y() < Rig.TOUCHING) {
                    planted.add(k);
                    down = true;
                }
            }
            if (down) {
                for (final Dof d : LEG) {
                    ways.add(new Way(side > 0 ? Part.RIGHT_THIGH : Part.LEFT_THIGH, d, 0));
                }
            }
        }
        if (planted.isEmpty()) {
            return now;
        }
        // the leg's ways named by the part each belongs to
        final List<Way> named = ways.stream().map(w -> w.part() == null ? w
                : new Way(owner(w.part(), w.dof()), w.dof(), 0)).toList();
        final List<V3> stood = planted.stream().map(k -> wasFree.contacts().get(k)).toList();

        Pose p = now;
        final double[] held = named.stream().mapToDouble(w -> w.get(now)).toArray();
        for (int step = 0; step < STEPS; step++) {
            final double[] miss = misses(proportions, p, planted, stood, over, named, held);
            final int n = named.size();
            final double[][] j = new double[n][];
            for (int k = 0; k < n; k++) {
                final Way w = named.get(k);
                final double v = w.get(p);
                final double h = w.part() == null || v + NUDGE <= w.dof().max() ? NUDGE : -NUDGE;
                final double[] moved = misses(proportions, w.set(p, v + h), planted, stood, over, named, held);
                j[k] = new double[miss.length];
                for (int r = 0; r < miss.length; r++) {
                    j[k][r] = (moved[r] - miss[r]) / h;
                }
            }
            final double[][] a = new double[n][n];
            final double[] b = new double[n];
            double trace = 0;
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    double s = 0;
                    for (int m = 0; m < miss.length; m++) {
                        s += j[r][m] * j[c][m];
                    }
                    a[r][c] = s;
                }
                double s = 0;
                for (int m = 0; m < miss.length; m++) {
                    s -= j[r][m] * miss[m];
                }
                b[r] = s;
                trace += a[r][r];
            }
            for (int r = 0; r < n; r++) {
                a[r][r] += 1e-3 * trace / n + 1e-9;
            }
            final double[] d = Drag.solve(a, b);
            Pose next = p;
            for (int k = 0; k < n; k++) {
                final Way w = named.get(k);
                next = w.set(next, w.get(p) + Math.clamp(d[k], -20, 20));
            }
            if (next.equals(p)) {
                break;
            }
            p = next;
        }
        return p;
    }

    /** The part that turns {@code dof}, of the leg whose thigh is {@code thigh}. */
    private static Part owner(final Part thigh, final Dof dof) {
        final boolean right = thigh == Part.RIGHT_THIGH;
        return switch (dof) {
            case KNEE -> right ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            case ANKLE, ANKLE_ROLL -> right ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            default -> thigh;
        };
    }

    /**
     * How far {@code p} is from what is asked of it, each a number to be brought to nothing: each planted point from
     * where it stood; its centre of gravity, across and ahead, from where it is asked to be; each way, a little, from
     * where it was told to be.
     */
    private static double[] misses(final Proportions proportions, final Pose p, final List<Integer> planted,
                                   final List<V3> stood, final V3 over, final List<Way> ways, final double[] held) {
        final Rig free = new Rig(proportions, p, false);
        final List<V3> at = free.contacts();
        final double[] m = new double[planted.size() * 3 + 2 + ways.size()];
        int i = 0;
        for (int k = 0; k < planted.size(); k++) {
            final V3 miss = at.get(planted.get(k)).minus(stood.get(k));
            m[i++] = miss.x();
            m[i++] = miss.y();
            m[i++] = miss.z();
        }
        final V3 c = free.centreOfMass();
        m[i++] = CENTRE * (c.x() - over.x());
        m[i++] = CENTRE * (c.z() - over.z());
        for (int k = 0; k < ways.size(); k++) {
            final Way w = ways.get(k);
            final double hold = w.part() == null && w.axis() == 1 ? HOLD_HEIGHT : HOLD;
            m[i++] = hold * (w.get(p) - held[k]);
        }
        return m;
    }
}
