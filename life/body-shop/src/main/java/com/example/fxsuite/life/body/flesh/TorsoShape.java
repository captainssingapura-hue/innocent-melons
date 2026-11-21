package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Moved;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A torso's shape found ({@link Torso}): level rings from its crotch to under its jaw, a grid cell apart and as many
 * round, each reaching as far as its widths say at that height, smooth between its heights (a curve that does not
 * overshoot them, Fritsch and Carlson's), round an oval of them, and as far again as its features swell or groove it
 * there; never nearer its bones than a little beyond them, the skin eased over them. In the drawn figure's frame as it
 * is held to be bound, then made to the body's size.
 */
public record TorsoShape() implements StatelessFunctionalObject {

    public static final TorsoShape INSTANCE = new TorsoShape();

    /** How far beyond the bones the skin keeps at least, and over how much it eases off them, cm. */
    private static final double COVER = 0.0;
    private static final double EASE = 0.8;
    /** How far out a ring looks for the bones, and the grid the bones are looked for on: cm, cm, degrees. */
    private static final double FAR = 26;
    private static final double TALL = 1.5;
    private static final int ROUND = 36;

    /** A torso resolved: its heights' widths, smooth, its features placed, its bones' reach on a grid. */
    private record Resolved(double[] y, double[][] widths, double[][] slopes, List<double[]> features, double low,
                            double[][] bones) {
    }

    /**
     * {@code torso}'s rings, over bones as far a woman's as {@code female} says, made {@code scale} times,
     * {@code cell} apart.
     */
    public Sculptor.Rows rows(final Torso torso, final double female, final double scale, final double cell) {
        final Rig bound = new Rig(Proportions.DRAWN, Fleshing.INSTANCE.bind(), false);
        final Resolved r = resolved(torso, bound, female);
        double widest = 0;
        for (final double w : r.widths()[0]) {
            widest = Math.max(widest, w);
        }
        final int around = (int) Math.max(24, Math.min(360, Math.round(2 * Math.PI * widest * scale / cell)));
        final double step = cell / scale;
        final List<V3[]> rows = new ArrayList<>();
        final List<V3> middles = new ArrayList<>();
        final double top = r.y()[r.y().length - 1];
        for (double y = r.y()[0]; y <= top + 1e-9; y += step) {
            final V3 m = new V3(0, y, smooth(r, 3, y));
            final V3[] row = new V3[around];
            for (int j = 0; j < around; j++) {
                final double a = 360.0 * j / around;
                final double t = Math.toRadians(a);
                row[j] = m.plus(new V3(Math.sin(t), 0, Math.cos(t)).times(reach(r, a, y))).times(scale);
            }
            rows.add(row);
            middles.add(m.times(scale));
        }
        return new Sculptor.Rows(rows, middles);
    }

    /** {@code torso} resolved on the body {@code bound} holds, over bones as far a woman's as {@code female} says. */
    private Resolved resolved(final Torso torso, final Rig bound, final double female) {
        final List<Torso.Key> keys = new ArrayList<>(torso.keys());
        keys.sort(Comparator.comparingDouble(k -> k.height().y(bound)));
        final int n = keys.size();
        final double[] y = new double[n];
        final double[][] w = new double[4][n];
        for (int i = 0; i < n; i++) {
            final Torso.Key k = keys.get(i);
            y[i] = k.height().y(bound);
            w[0][i] = k.side();
            w[1][i] = k.ahead();
            w[2][i] = k.behind();
            w[3][i] = k.middle();
        }
        final double[][] m = new double[4][];
        for (int c = 0; c < 4; c++) {
            m[c] = slopes(y, w[c]);
        }
        final List<double[]> features = new ArrayList<>();
        for (final Torso.Feature f : torso.features()) {
            final double amount = f.amount() * (f.muscle() ? torso.definition() : 1);
            features.add(new double[] {f.kind().ordinal(), f.from(), f.low().y(bound), f.to(), f.high().y(bound),
                f.soft(), f.tall(), amount, f.both() ? 1 : 0});
        }
        final Resolved shape = new Resolved(y, w, m, features, y[0] - 2, null);
        return new Resolved(y, w, m, features, y[0] - 2, bones(shape, bound, female));
    }

    /** How far the bones a torso is laid over reach from its middle, on a grid of heights and angles: the trunk's,
     * the shoulders' and the thighs', both sides. */
    private double[][] bones(final Resolved r, final Rig bound, final double female) {
        final List<Field.Element> under = new ArrayList<>();
        for (final Part p : List.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST, Part.NECK, Part.RIGHT_GIRDLE,
                Part.LEFT_GIRDLE, Part.RIGHT_THIGH, Part.LEFT_THIGH)) {
            final Rig.Frame f = bound.frame(p);
            final Part right = p.side() < 0 ? p.other() : p;
            under.add(new Field.Element(p.said(), new Moved(PartBones.INSTANCE.shape(right, female), f.turn(), f.at(),
                    p.side() < 0, 0), Field.Op.ADD, 0));
        }
        final Field.Shape bones = Field.Compound.of(under);
        final int rows = (int) Math.ceil((r.y()[r.y().length - 1] + 2 - r.low()) / TALL) + 1;
        final double[][] reach = new double[rows][ROUND];
        for (int i = 0; i < rows; i++) {
            final double y = r.low() + i * TALL;
            final V3 m = new V3(0, y, smooth(r, 3, y));
            for (int j = 0; j < ROUND; j++) {
                final double t = Math.toRadians(360.0 * j / ROUND);
                final V3 way = new V3(Math.sin(t), 0, Math.cos(t));
                // in from outside, by as far as the bones are at least from where it is
                double d = FAR;
                while (d > 0) {
                    final V3 p = m.plus(way.times(d));
                    final double out = bones.at(p.x(), p.y(), p.z());
                    if (out < 0) {
                        reach[i][j] = d;
                        break;
                    }
                    d -= Math.max(out, 0.05);
                }
            }
        }
        return hull(reach);
    }

    /**
     * The bones' reach made a smooth hull over them, as skin lies over ribs and a spine: the furthest of each point's
     * neighbours, then blurred, a few times, so that no rib or process stands out of it on its own.
     */
    private double[][] hull(final double[][] reach) {
        final int rows = reach.length;
        double[][] g = new double[rows][ROUND];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < ROUND; j++) {
                double most = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        most = Math.max(most, reach[Math.clamp(i + di, 0, rows - 1)][(j + dj + ROUND) % ROUND]);
                    }
                }
                g[i][j] = most;
            }
        }
        for (int pass = 0; pass < 10; pass++) {
            final double[][] b = new double[rows][ROUND];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < ROUND; j++) {
                    double sum = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            sum += g[Math.clamp(i + di, 0, rows - 1)][(j + dj + ROUND) % ROUND];
                        }
                    }
                    b[i][j] = sum / 9;
                }
            }
            g = b;
        }
        return g;
    }

    /** How far the skin reaches from the middle at angle {@code a} and height {@code y}. */
    private double reach(final Resolved r, final double a, final double y) {
        final double t = Math.toRadians(a);
        final double side = smooth(r, 0, y);
        final double deep = Math.cos(t) >= 0 ? smooth(r, 1, y) : smooth(r, 2, y);
        final double sx = Math.sin(t) / side;
        final double sz = Math.cos(t) / deep;
        final double base = 1 / Math.sqrt(sx * sx + sz * sz);
        double out = base;
        for (final double[] f : r.features()) {
            out += feature(f, a, y, base) + (f[8] > 0 ? feature(f, -a, y, base) : 0);
        }
        final double bone = bone(r, a, y) + COVER;
        final double h = Math.max(EASE - Math.abs(out - bone), 0) / EASE;
        return Math.max(out, bone) + h * h * EASE * 0.25;
    }

    /** How far the bones reach at angle {@code a} and height {@code y}: the grid's, between its points. */
    private double bone(final Resolved r, final double a, final double y) {
        final double[][] g = r.bones();
        final double u = Math.clamp((y - r.low()) / TALL, 0, g.length - 1.0001);
        final double v = ((a % 360 + 360) % 360) / 360 * ROUND;
        final int i = (int) u;
        final int j = (int) v % ROUND;
        final double fu = u - i;
        final double fv = v - Math.floor(v);
        final int k = (j + 1) % ROUND;
        final double lo = g[i][j] * (1 - fv) + g[i][k] * fv;
        final double hi = g[i + 1][j] * (1 - fv) + g[i + 1][k] * fv;
        return lo * (1 - fu) + hi * fu;
    }

    /**
     * How far feature {@code f} swells the skin at angle {@code a} and height {@code y}, the skin {@code r} from the
     * middle there: along its line, or over its patch, falling away smoothly.
     */
    private double feature(final double[] f, final double a, final double y, final double r) {
        final double arc = Math.PI / 180 * r;
        if (f[0] == Torso.Kind.LINE.ordinal()) {
            final double px = wrapped(a - f[1]) * arc;
            final double py = y - f[2];
            final double dx = wrapped(f[3] - f[1]) * arc;
            final double dy = f[4] - f[2];
            final double t = Math.clamp((px * dx + py * dy) / Math.max(dx * dx + dy * dy, 1e-9), 0, 1);
            final double ex = px - dx * t;
            final double ey = py - dy * t;
            return f[7] * Math.exp(-(ex * ex + ey * ey) / (f[5] * f[5]));
        }
        if (f[0] == Torso.Kind.DOME.ordinal()) {
            return dome(f, a, y, arc);
        }
        final double mid = (f[1] + f[3]) / 2;
        final double half = Math.abs(wrapped(f[3] - f[1])) / 2;
        final double round = Math.max(0, Math.abs(wrapped(a - mid)) - half) * arc;
        final double up = Math.max(0, Math.max(f[2] - y, y - f[4]));
        return f[7] * Math.exp(-round * round / (f[5] * f[5]) - up * up / (f[6] * f[6]));
    }

    /**
     * How far dome {@code f} swells the skin: as far as it says at its middle, {@code f[5]} cm round either side and
     * {@code f[6]} below; above, its slope as much again and more; one curve all round, so that it is smooth across.
     */
    private double dome(final double[] f, final double a, final double y, final double arc) {
        final double u = wrapped(a - f[1]) * arc / f[5];
        final double dy = y - f[2];
        final double v = dy / (dy < 0 ? f[6] : 1.7 * f[6]);
        final double q = 1 - u * u - v * v;
        return q <= 0 ? 0 : f[7] * Math.pow(q, 1.5);
    }

    /** {@code a} degrees, between -180 and 180. */
    private double wrapped(final double a) {
        return ((a % 360) + 540) % 360 - 180;
    }

    /** Width {@code c} (0 to either side, 1 ahead, 2 behind, 3 the middle ahead) at height {@code y}, smooth. */
    private double smooth(final Resolved r, final int c, final double y) {
        final double[] x = r.y();
        final double[] v = r.widths()[c];
        final double[] m = r.slopes()[c];
        final double at = Math.clamp(y, x[0], x[x.length - 1]);
        int i = 0;
        while (i < x.length - 2 && at > x[i + 1]) {
            i++;
        }
        final double h = x[i + 1] - x[i];
        final double t = (at - x[i]) / h;
        final double t2 = t * t;
        final double t3 = t2 * t;
        return (2 * t3 - 3 * t2 + 1) * v[i] + (t3 - 2 * t2 + t) * h * m[i] + (-2 * t3 + 3 * t2) * v[i + 1]
                + (t3 - t2) * h * m[i + 1];
    }

    /** The slopes at {@code x} of a curve through {@code v} that does not overshoot (Fritsch and Carlson's). */
    private double[] slopes(final double[] x, final double[] v) {
        final int n = x.length;
        final double[] d = new double[n - 1];
        for (int i = 0; i + 1 < n; i++) {
            d[i] = (v[i + 1] - v[i]) / (x[i + 1] - x[i]);
        }
        final double[] m = new double[n];
        m[0] = d[0];
        m[n - 1] = d[n - 2];
        for (int i = 1; i + 1 < n; i++) {
            if (d[i - 1] * d[i] <= 0) {
                m[i] = 0;
            } else {
                final double h0 = x[i] - x[i - 1];
                final double h1 = x[i + 1] - x[i];
                final double w1 = 2 * h1 + h0;
                final double w2 = h1 + 2 * h0;
                m[i] = (w1 + w2) / (w1 / d[i - 1] + w2 / d[i]);
            }
        }
        return m;
    }
}
