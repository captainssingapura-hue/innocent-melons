package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/**
 * The deep fascia round a region's muscles: a sleeve under tension that wraps them and presses them together, as the
 * neck's investing layer does. A stack of rings from the region's lowest face to its highest, each level, each first
 * laid a little outside the muscles at its height; each sweep drawn tighter, each point toward its neighbours round
 * it and the same point of the rings above and below, as a stretched sheet is; and where a muscle's surface lies
 * outside it, the two share the difference, the muscle pressed in and the sleeve pressed out, so that it holds them
 * and they hold it up; and kept outside the bones. Its rings stay level: only across and ahead do its points move.
 */
public final class Fascia {

    /** How many points round each ring. */
    private static final int ROUND = 72;
    /** How far outside the muscles a ring is first laid, cm. */
    private static final double MARGIN = 0.4;
    /** How much a sweep draws a point toward its neighbours round, and toward those above and below. */
    private static final double ROUND_PULL = 0.3;
    private static final double UP_PULL = 0.15;
    /** How much of the difference the muscle gives, where the two press; the sleeve gives the rest. */
    private static final double MUSCLE_GIVES = 0.5;
    /** How far a sweep draws each point of the sleeve in, square to it, cm: how tightly it holds what is in it. */
    private static final double SQUEEZE = 0.01;

    private final double lowest;
    private final double step;
    private final double[] x;
    private final double[] z;
    private final double[] middleX;
    private final double[] middleZ;
    /** Whether each ring has any muscle in it: one with none takes no part. */
    private final boolean[] filled;
    /** What the points pressing on each point of the sleeve ask of it, this sweep, and how many ask. */
    private final double[] pushX;
    private final double[] pushZ;
    private final int[] pushes;

    /**
     * A sleeve over {@code region}'s height, rings {@code step} apart, laid round the points {@code at} (three numbers
     * each) of the muscles' surfaces that {@code counted} says are in the region.
     */
    public Fascia(final SoftRegion region, final double step, final double[] at, final boolean[] counted) {
        this.lowest = region.lo().y();
        this.step = step;
        final int rings = (int) Math.round((region.hi().y() - lowest) / step) + 1;
        x = new double[rings * ROUND];
        z = new double[rings * ROUND];
        middleX = new double[rings];
        middleZ = new double[rings];
        filled = new boolean[rings];
        pushX = new double[x.length];
        pushZ = new double[x.length];
        pushes = new int[x.length];
        final int[] many = new int[rings];
        for (int p = 0; p < counted.length; p++) {
            if (counted[p]) {
                final int r = ring(at[3 * p + 1]);
                middleX[r] += at[3 * p];
                middleZ[r] += at[3 * p + 2];
                many[r]++;
            }
        }
        final double[] reach = new double[rings * ROUND];
        for (int r = 0; r < rings; r++) {
            filled[r] = many[r] > 0;
            middleX[r] = many[r] == 0 ? (region.lo().x() + region.hi().x()) / 2 : middleX[r] / many[r];
            middleZ[r] = many[r] == 0 ? (region.lo().z() + region.hi().z()) / 2 : middleZ[r] / many[r];
        }
        for (int p = 0; p < counted.length; p++) {
            if (counted[p]) {
                final int r = ring(at[3 * p + 1]);
                final double dx = at[3 * p] - middleX[r];
                final double dz = at[3 * p + 2] - middleZ[r];
                final int a = Math.floorMod((int) Math.round(Math.atan2(dz, dx) / (2 * Math.PI) * ROUND), ROUND);
                reach[r * ROUND + a] = Math.max(reach[r * ROUND + a], Math.hypot(dx, dz));
            }
        }
        for (int r = 0; r < rings; r++) {
            for (int a = 0; a < ROUND; a++) {
                double far = 0;
                for (int d = -2; d <= 2; d++) {
                    far = Math.max(far, reach[r * ROUND + Math.floorMod(a + d, ROUND)]);
                }
                final double angle = 2 * Math.PI * a / ROUND;
                x[r * ROUND + a] = middleX[r] + Math.cos(angle) * (far + MARGIN);
                z[r * ROUND + a] = middleZ[r] + Math.sin(angle) * (far + MARGIN);
            }
        }
    }

    /**
     * The sleeve's shape: its rings, each so many points round, every point by ring then round, three numbers each,
     * in the body's frame; and which rings have any muscle in them, those with none not part of it.
     *
     * @param rings  how many rings
     * @param round  how many points round each
     * @param points every point, three numbers each
     * @param filled whether each ring has any muscle in it
     */
    public record Sleeve(int rings, int round, float[] points, boolean[] filled) {
    }

    /** Its shape now. */
    public Sleeve sleeve() {
        final float[] p = new float[3 * x.length];
        for (int r = 0; r < rings(); r++) {
            for (int a = 0; a < ROUND; a++) {
                final int i = r * ROUND + a;
                p[3 * i] = (float) x[i];
                p[3 * i + 1] = (float) (lowest + r * step);
                p[3 * i + 2] = (float) z[i];
            }
        }
        return new Sleeve(rings(), ROUND, p, filled.clone());
    }

    /** How many rings. */
    public int rings() {
        return middleX.length;
    }

    /** How many points round each ring. */
    public int round() {
        return ROUND;
    }

    /** Point {@code a} of ring {@code r}. */
    public V3 point(final int r, final int a) {
        return new V3(x[r * ROUND + a], lowest + r * step, z[r * ROUND + a]);
    }

    /** The ring nearest height {@code y}. */
    private int ring(final double y) {
        return Math.clamp((int) Math.round((y - lowest) / step), 0, middleX.length - 1);
    }

    /**
     * One sweep's drawing tighter, every point at once: toward its neighbours round it and above and below, as a
     * stretched sheet is; and in, square to the ring, by {@link #SQUEEZE}, as a sleeve holding what is in it is.
     */
    void tightened() {
        final double[] nx = x.clone();
        final double[] nz = z.clone();
        for (int r = 0; r < rings(); r++) {
            if (!filled[r]) {
                continue;
            }
            for (int a = 0; a < ROUND; a++) {
                final int i = r * ROUND + a;
                final int prev = r * ROUND + Math.floorMod(a - 1, ROUND);
                final int next = r * ROUND + (a + 1) % ROUND;
                nx[i] += ROUND_PULL * ((x[prev] + x[next]) / 2 - x[i]);
                nz[i] += ROUND_PULL * ((z[prev] + z[next]) / 2 - z[i]);
                // drawn in, square to the ring, toward its middle
                double inX = -(z[next] - z[prev]);
                double inZ = x[next] - x[prev];
                final double length = Math.hypot(inX, inZ);
                if (length > 1e-12) {
                    final double toward = inX * (middleX[r] - x[i]) + inZ * (middleZ[r] - z[i]) < 0 ? -1 : 1;
                    nx[i] += SQUEEZE * toward * inX / length;
                    nz[i] += SQUEEZE * toward * inZ / length;
                }
                if (r > 0 && r + 1 < rings() && filled[r - 1] && filled[r + 1]) {
                    nx[i] += UP_PULL * ((x[i - ROUND] + x[i + ROUND]) / 2 - x[i]);
                    nz[i] += UP_PULL * ((z[i - ROUND] + z[i + ROUND]) / 2 - z[i]);
                }
            }
        }
        System.arraycopy(nx, 0, x, 0, x.length);
        System.arraycopy(nz, 0, z, 0, z.length);
    }

    /**
     * Where a muscle's surface point at {@code px}, {@code py}, {@code pz} lies outside the ring at its height: how far
     * it should move, across and ahead, into {@code move}; and the ring's edge it lies outside asked to move out by the
     * rest, done when the sweep is ({@link #yielded}). Whether it lay outside.
     */
    boolean pressed(final double px, final double py, final double pz, final double[] move) {
        final int r = ring(py);
        if (!filled[r] || inside(r, px, pz)) {
            return false;
        }
        int best = -1;
        double bestFar = Double.MAX_VALUE;
        double bestT = 0;
        double qx = 0;
        double qz = 0;
        for (int a = 0; a < ROUND; a++) {
            final int i = r * ROUND + a;
            final int j = r * ROUND + (a + 1) % ROUND;
            final double ex = x[j] - x[i];
            final double ez = z[j] - z[i];
            final double l2 = ex * ex + ez * ez;
            final double t = l2 < 1e-18 ? 0 : Math.clamp(((px - x[i]) * ex + (pz - z[i]) * ez) / l2, 0, 1);
            final double cx = x[i] + t * ex;
            final double cz = z[i] + t * ez;
            final double far = (px - cx) * (px - cx) + (pz - cz) * (pz - cz);
            if (far < bestFar) {
                bestFar = far;
                best = a;
                bestT = t;
                qx = cx;
                qz = cz;
            }
        }
        final double dx = qx - px;
        final double dz = qz - pz;
        move[0] = MUSCLE_GIVES * dx;
        move[1] = MUSCLE_GIVES * dz;
        final int i = r * ROUND + best;
        final int j = r * ROUND + (best + 1) % ROUND;
        pushX[i] -= (1 - MUSCLE_GIVES) * dx * (1 - bestT);
        pushZ[i] -= (1 - MUSCLE_GIVES) * dz * (1 - bestT);
        pushes[i]++;
        pushX[j] -= (1 - MUSCLE_GIVES) * dx * bestT;
        pushZ[j] -= (1 - MUSCLE_GIVES) * dz * bestT;
        pushes[j]++;
        return true;
    }

    /** The sleeve moved out as the muscles pressing on it asked, each point by the mean of what was asked of it. */
    void yielded() {
        for (int i = 0; i < x.length; i++) {
            if (pushes[i] > 0) {
                x[i] += pushX[i] / pushes[i];
                z[i] += pushZ[i] / pushes[i];
            }
        }
        java.util.Arrays.fill(pushX, 0);
        java.util.Arrays.fill(pushZ, 0);
        java.util.Arrays.fill(pushes, 0);
    }

    /** Whether {@code px}, {@code pz} is inside ring {@code r}: by how often a line out from it crosses it. */
    private boolean inside(final int r, final double px, final double pz) {
        boolean in = false;
        for (int a = 0; a < ROUND; a++) {
            final int i = r * ROUND + a;
            final int j = r * ROUND + (a + 1) % ROUND;
            if ((z[i] > pz) != (z[j] > pz) && px < x[i] + (pz - z[i]) / (z[j] - z[i]) * (x[j] - x[i])) {
                in = !in;
            }
        }
        return in;
    }

    /** Every point of the sleeve kept outside the bones by {@code clear}, moved out across and ahead. */
    void keptOutOf(final Walls walls, final double clear) {
        for (int r = 0; r < rings(); r++) {
            final double y = lowest + r * step;
            for (int a = 0; a < ROUND; a++) {
                final int i = r * ROUND + a;
                final double d = walls.distance(x[i], y, z[i]);
                if (d < clear) {
                    final V3 out = walls.out(x[i], y, z[i]);
                    x[i] += out.x() * (clear - d);
                    z[i] += out.z() * (clear - d);
                }
            }
        }
    }

    /** How much room the sleeve holds, cm3: its rings' areas, each over its step. */
    public double room() {
        double sum = 0;
        for (int r = 0; r < rings(); r++) {
            double area = 0;
            for (int a = 0; a < ROUND; a++) {
                final int i = r * ROUND + a;
                final int j = r * ROUND + (a + 1) % ROUND;
                area += x[i] * z[j] - x[j] * z[i];
            }
            sum += filled[r] ? Math.abs(area) / 2 * step : 0;
        }
        return sum;
    }
}
