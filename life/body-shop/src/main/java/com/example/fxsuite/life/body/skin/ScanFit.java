package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Subdivision;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.function.UnaryOperator;

/**
 * A smooth surface fitted to a scan of one: a base, a cage's points and faces, moved so that, smoothed so many times
 * ({@link Subdivision}), it passes as near as it may through the scan's points, the scan having the same points and
 * faces as it smoothed. So many rounds, each: the base smoothed; how far each of its smoothed points is from the
 * scan's; each base point moved by how far its own, those on its faces, are off, on the whole; a point far off counting
 * for less, the further the less, so that what sticks out of the scan alone, a spike, a flap, a fold, draws the
 * surface hardly at all. How smooth the surface is, how fine its base.
 */
public record ScanFit() implements StatelessFunctionalObject {

    public static final ScanFit INSTANCE = new ScanFit();

    /** How many rounds; how far off a scan's point may be, cm, before it counts for less. */
    private static final int ROUNDS = 15;
    private static final double LOOSE = 0.3;

    /** {@code base} fitted so that, smoothed {@code steps} times, it passes through {@code scan}: its points so. */
    public V3[] fitted(final Subdivision.Level base, final int steps, final V3[] scan) {
        return fitted(base, steps, fine -> scan, ROUNDS);
    }

    /**
     * {@code base} fitted, {@code rounds} times, so that, smoothed {@code steps} times, it passes as near as it may
     * through what {@code onto} finds for its smoothed points as they are each round, one for each, by number: a scan
     * of its own points, or the nearest points of another surface, found afresh as it moves. Its smoothed points so.
     */
    public V3[] fitted(final Subdivision.Level base, final int steps, final UnaryOperator<V3[]> onto,
                       final int rounds) {
        final Subdivision s = Subdivision.INSTANCE;
        final int[] root = roots(base, steps);
        double[] at = base.points().clone();
        for (int round = 0; round < rounds; round++) {
            final Subdivision.Level fine = s.times(new Subdivision.Level(at, base.faces(), base.sharp()), steps);
            at = moved(at, base, fine, onto.apply(points(fine)), root);
        }
        return points(s.times(new Subdivision.Level(at, base.faces(), base.sharp()), steps));
    }

    private V3[] points(final Subdivision.Level l) {
        final V3[] out = new V3[l.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = l.point(i);
        }
        return out;
    }

    /** Which of {@code base}'s faces each face of it smoothed {@code steps} times comes of, by number. */
    private int[] roots(final Subdivision.Level base, final int steps) {
        final Subdivision s = Subdivision.INSTANCE;
        int[] root = new int[base.faces().size()];
        for (int f = 0; f < root.length; f++) {
            root[f] = f;
        }
        Subdivision.Level l = base;
        for (int k = 0; k < steps; k++) {
            final int[] from = s.parents(l);
            final int[] next = new int[from.length];
            for (int f = 0; f < next.length; f++) {
                next[f] = root[from[f]];
            }
            root = next;
            l = s.once(l);
        }
        return root;
    }

    /**
     * The base's points {@code at}, each moved by how far the points of {@code fine} on its faces are off {@code scan},
     * on the whole, each counting for less the further off it is.
     */
    private double[] moved(final double[] at, final Subdivision.Level base, final Subdivision.Level fine,
                           final V3[] scan, final int[] root) {
        final V3[] off = new V3[fine.size()];
        final double[] counts = new double[off.length];
        for (int j = 0; j < off.length; j++) {
            off[j] = scan[j].minus(fine.point(j));
            final double r = off[j].length() / LOOSE;
            counts[j] = 1 / (1 + r * r);
        }
        final double[] by = new double[at.length];
        final double[] all = new double[at.length / 3];
        for (int f = 0; f < fine.faces().size(); f++) {
            for (final int j : fine.faces().get(f)) {
                for (final int i : base.faces().get(root[f])) {
                    by[3 * i] += counts[j] * off[j].x();
                    by[3 * i + 1] += counts[j] * off[j].y();
                    by[3 * i + 2] += counts[j] * off[j].z();
                    all[i] += counts[j];
                }
            }
        }
        final double[] next = at.clone();
        for (int i = 0; i < all.length; i++) {
            for (int c = 0; c < 3 && all[i] > 0; c++) {
                next[3 * i + c] += by[3 * i + c] / all[i];
            }
        }
        return next;
    }
}
