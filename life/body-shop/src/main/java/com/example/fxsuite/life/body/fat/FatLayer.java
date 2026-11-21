package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.cage.Fitting;
import com.example.fxsuite.life.body.cage.Subdivision;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A body's fat, every body's the same points and faces: its cage found on its fat's outside ({@link FatCage}), moved so
 * that its smoothed surface passes through them ({@link Fitting}), smoothed {@link #STEPS} times
 * ({@link Subdivision}); then each point of it set on the fat's outside, in along its way out ({@link FatTarget}), so
 * that the muscles' shapes show through it as far as the fat is thin; its points eased toward evenly apart along the
 * surface and set on it again, twice. Each point following the body as what is under it does, bound to the body where
 * it was found, to move with it.
 */
public record FatLayer() implements StatelessFunctionalObject {

    public static final FatLayer INSTANCE = new FatLayer();

    /** How many times its cage is smoothed; how far out a point looks back in from, and how far in at most, cm. */
    private static final int STEPS = 3;
    private static final double BACK = 3;
    private static final int EASING = 2;
    /** How many times narrow grooves are filled; how many times what still lies too near beneath is pushed out. */
    private static final int FILL = 4;
    private static final int CLEARING = 3;
    /** How far, at most, a triangle under a muscle's edge is pushed out, cm. */
    private static final double SLIVER = 0.4;
    /** How far a point may stand out from its neighbours' middle, or sink, before it is set back among them, cm. */
    private static final double SPIKE = 0.6;

    /** How far a point may be set from where its smoothed cage put it, cm: past that, it stays. */
    private static final double FAR = 4;

    /** The fat as found: its cage, its surface bound to the body, and where its points found no fat outside. */
    public record Fat(Cage cage, Flesh flesh, List<V3> missed) {
    }

    /** The fat on {@code target}. */
    public Fat of(final FatTarget target) {
        final FatCage.Built built = FatCage.INSTANCE.of(target);
        final Cage fitted = Fitting.INSTANCE.fitted(built.cage(), 1);
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level start = s.of(fitted, 1);
        final Subdivision.Level shape = s.times(start, STEPS);
        final Subdivision.Level where = s.times(new Subdivision.Level(channels(built.regions()), start.faces(),
                start.sharp()), STEPS);
        final int[] t = s.mesh(shape).triangles();
        final FatTarget.Region[] region = regions(where);
        final V3[] at = new V3[shape.size()];
        for (int i = 0; i < at.length; i++) {
            at[i] = shape.point(i);
        }
        final V3[] smooth = at.clone();
        final Weights[] w = new Weights[at.length];
        boolean[] missed = onto(target, at, smooth, w, t, region, BACK);
        final int[][] next = neighbours(at.length, t);
        for (int round = 0; round < EASING; round++) {
            ease(at, t, next);
            missed = onto(target, at, smooth, w, t, region, BACK / 2);
        }
        for (int round = 0; round < EASING; round++) {
            despike(at, t, next);
        }
        fill(at, t, next);
        for (int round = 0; round < CLEARING; round++) {
            clear(target, at, t, region);
        }
        final Sheet sheet = new Sheet();
        final List<V3> misses = new ArrayList<>();
        for (int i = 0; i < at.length; i++) {
            sheet.add(at[i], w[i]);
            if (missed[i]) {
                misses.add(at[i]);
            }
        }
        for (int i = 0; i < t.length; i += 3) {
            sheet.triangle(t[i], t[i + 1], t[i + 2]);
        }
        return new Fat(fitted, sheet.flesh(target.bind()), misses);
    }

    /**
     * Every point of {@code at} set on the fat's outside, in along its way out from {@code back} out, no further than
     * {@link #FAR} from where its smoothed cage put it ({@code smooth}), how it follows the body into {@code w}; which
     * found none and stayed, following what is nearest under them.
     */
    private boolean[] onto(final FatTarget target, final V3[] at, final V3[] smooth, final Weights[] w, final int[] t,
                           final FatTarget.Region[] region, final double back) {
        final V3[] out = normals(at, t);
        final boolean[] missed = new boolean[at.length];
        IntStream.range(0, at.length).parallel().forEach(i -> {
            final int side = at[i].x() < 0 ? -1 : 1;
            java.util.Optional<FatTarget.Hit> hit = java.util.Optional.empty();
            // from further out each time the line starts inside the body, met as soon as it starts
            for (double b = back; b <= 4 * back && hit.isEmpty(); b *= 2) {
                final V3 from = at[i].plus(out[i].times(b));
                hit = target.onto(from, out[i].times(-1), region[i], side, 2 * b)
                        .filter(h -> h.at().minus(from).length() > 1e-6);
            }
            hit = hit.filter(h -> h.at().minus(smooth[i]).length() <= FAR);
            if (hit.isPresent()) {
                at[i] = hit.get().at();
                w[i] = hit.get().weights();
            } else {
                missed[i] = true;
                w[i] = w[i] != null ? w[i] : target.under(at[i]);
            }
        });
        return missed;
    }

    /**
     * Narrow grooves filled, as fat fills the valleys between muscles: {@link #FILL} times, every point that lies below
     * its neighbours' middle moved half way out to it, along its way out; none moved in, so that nothing beneath shows.
     */
    private void fill(final V3[] at, final int[] t, final int[][] next) {
        for (int round = 0; round < FILL; round++) {
            final V3[] out = normals(at, t);
            final V3[] moved = new V3[at.length];
            for (int i = 0; i < at.length; i++) {
                V3 m = V3.ZERO;
                for (final int j : next[i]) {
                    m = m.plus(at[j].times(1.0 / next[i].length));
                }
                final double up = m.minus(at[i]).dot(out[i]);
                moved[i] = up > 0 ? at[i].plus(out[i].times(up / 2)) : at[i];
            }
            System.arraycopy(moved, 0, at, 0, at.length);
        }
    }

    /**
     * What lies too near beneath pushed out: each triangle's middle that is nearer the muscles or the bones than the fat
     * is thick there pushing its corners out along their ways out by as much as it falls short, the most any of its
     * triangles asks; so that a triangle between two points on the fat's outside does not cut under a muscle's edge.
     * Only so far as such a sliver asks, {@link #SLIVER}: a triangle further under than that is not under a muscle's
     * edge but near what is not its own, the other thigh by the crotch, and is left.
     */
    private void clear(final FatTarget target, final V3[] at, final int[] t, final FatTarget.Region[] region) {
        final V3[] out = normals(at, t);
        final double[] push = new double[at.length];
        final double[] wanting = IntStream.range(0, t.length / 3).parallel().mapToDouble(k -> {
            final V3 c = at[t[3 * k]].plus(at[t[3 * k + 1]]).plus(at[t[3 * k + 2]]).times(1.0 / 3);
            return -target.clearance(c, region[t[3 * k]], c.x() < 0 ? -1 : 1);
        }).toArray();
        for (int k = 0; k < wanting.length; k++) {
            for (int c = 0; c < 3; c++) {
                push[t[3 * k + c]] = wanting[k] < SLIVER ? Math.max(push[t[3 * k + c]], wanting[k])
                        : push[t[3 * k + c]];
            }
        }
        for (int i = 0; i < at.length; i++) {
            at[i] = push[i] > 0.01 ? at[i].plus(out[i].times(push[i])) : at[i];
        }
    }

    /**
     * A point standing out from its neighbours, or sunk among them, further than {@link #SPIKE}, set at their middle:
     * found on something its neighbours were not, as a line in slips past an edge; what the body is changes more
     * gently than that between points so near.
     */
    private void despike(final V3[] at, final int[] t, final int[][] next) {
        final V3[] out = normals(at, t);
        final V3[] moved = at.clone();
        for (int i = 0; i < at.length; i++) {
            V3 m = V3.ZERO;
            for (final int j : next[i]) {
                m = m.plus(at[j].times(1.0 / next[i].length));
            }
            if (Math.abs(at[i].minus(m).dot(out[i])) > SPIKE) {
                moved[i] = m;
            }
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }

    /** Every point moved half way toward its neighbours' middle, along the surface only. */
    private void ease(final V3[] at, final int[] t, final int[][] next) {
        final V3[] out = normals(at, t);
        final V3[] moved = new V3[at.length];
        for (int i = 0; i < at.length; i++) {
            V3 m = V3.ZERO;
            for (final int j : next[i]) {
                m = m.plus(at[j].times(1.0 / next[i].length));
            }
            final V3 d = m.minus(at[i]);
            moved[i] = at[i].plus(d.minus(out[i].times(d.dot(out[i]))).times(0.5));
        }
        System.arraycopy(moved, 0, at, 0, at.length);
    }

    /** What each point is on, one number each for the trunk, an arm and a leg, as the cage's points are. */
    private double[] channels(final FatTarget.Region[] regions) {
        final double[] c = new double[3 * regions.length];
        for (int i = 0; i < regions.length; i++) {
            c[3 * i + regions[i].ordinal()] = 1;
        }
        return c;
    }

    /** What each point of {@code where} is on most. */
    private FatTarget.Region[] regions(final Subdivision.Level where) {
        final FatTarget.Region[] r = new FatTarget.Region[where.size()];
        for (int i = 0; i < r.length; i++) {
            final V3 c = where.point(i);
            r[i] = c.x() >= c.y() && c.x() >= c.z() ? FatTarget.Region.TRUNK
                    : c.y() >= c.z() ? FatTarget.Region.ARM : FatTarget.Region.LEG;
        }
        return r;
    }

    /** Each point's way out: its triangles', by their size. */
    private V3[] normals(final V3[] at, final int[] t) {
        final V3[] n = new V3[at.length];
        java.util.Arrays.fill(n, V3.ZERO);
        for (int i = 0; i < t.length; i += 3) {
            final V3 f = at[t[i + 1]].minus(at[t[i]]).cross(at[t[i + 2]].minus(at[t[i]]));
            for (int k = 0; k < 3; k++) {
                n[t[i + k]] = n[t[i + k]].plus(f);
            }
        }
        for (int i = 0; i < n.length; i++) {
            n[i] = n[i].length() > 0 ? n[i].unit() : V3.Y;
        }
        return n;
    }

    /** Each point's neighbours, by the triangles' sides. */
    private int[][] neighbours(final int size, final int[] t) {
        final List<java.util.Set<Integer>> by = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            by.add(new java.util.TreeSet<>());
        }
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                by.get(t[i + k]).add(t[i + (k + 1) % 3]);
                by.get(t[i + (k + 1) % 3]).add(t[i + k]);
            }
        }
        return by.stream().map(x -> x.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new);
    }
}
