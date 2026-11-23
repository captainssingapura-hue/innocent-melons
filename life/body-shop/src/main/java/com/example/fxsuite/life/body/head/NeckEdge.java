package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/**
 * Where a surface cut round the neck ends ({@link NeckJoin}): the edge of what is kept of it, the loop of edges each of
 * one kept triangle at the cut, the one round most, seen from above, its outside, not a wall within it; its points in
 * the order they run round it, the way the angle about its middle rises seen from above, from its front, each as far
 * round as it is along it, a share of the way round; and a point anywhere round it, between the two it falls between.
 * Two edges' points as far round each match, front to front, however unlike their middles.
 */
public record NeckEdge() implements StatelessFunctionalObject {

    public static final NeckEdge INSTANCE = new NeckEdge();

    /** How near the cut, on the whole, a loop of the edge lies to be the cut's, cm. */
    private static final double AT = 3;

    /**
     * An edge round the neck: its points' numbers, in order round it from its front; where each is, in the head's
     * frame; how far round each is, a share of the way round, along it; its middle.
     *
     * @param at its points' numbers in the surface, in order round
     * @param where where each is, in the head's frame
     * @param round how far round each is, a share of the way round, from none at its front, rising
     * @param middle its middle
     */
    public record Edge(int[] at, V3[] where, double[] round, V3 middle) {

        /** Where it is {@code s} of the way round, between the two points it falls between. */
        public V3 at(final double s) {
            final int n = round.length;
            final double a = s - Math.floor(s);
            for (int k = 0; k < n; k++) {
                final double to = k + 1 < n ? round[k + 1] : 1;
                if (a >= round[k] && a <= to) {
                    final double t = to - round[k] < 1e-12 ? 0 : (a - round[k]) / (to - round[k]);
                    return where[k].times(1 - t).plus(where[(k + 1) % n].times(t));
                }
            }
            return where[0];
        }

        /** The point nearest {@code s} of the way round, by its place in the edge. */
        public int nearest(final double s) {
            int best = 0;
            for (int k = 1; k < round.length; k++) {
                if (gap(round[k], s) < gap(round[best], s)) {
                    best = k;
                }
            }
            return best;
        }

        private double gap(final double a, final double b) {
            final double d = Math.abs(a - b) % 1;
            return Math.min(d, 1 - d);
        }
    }

    /**
     * The edge of {@code kept}, triangles of a surface whose points in the head's frame are {@code where}, at the cut:
     * of its loops lying, on the whole, within {@link #AT} of it, {@code off} saying how far each point is, the one
     * round most, seen from above.
     */
    public Edge of(final List<int[]> kept, final V3[] where, final ToDoubleFunction<V3> off) {
        final Map<Long, Integer> count = new HashMap<>();
        for (final int[] t : kept) {
            for (int e = 0; e < 3; e++) {
                count.merge(key(t[e], t[(e + 1) % 3]), 1, Integer::sum);
            }
        }
        final Map<Integer, Integer> next = new HashMap<>();
        for (final int[] t : kept) {
            for (int e = 0; e < 3; e++) {
                if (count.get(key(t[e], t[(e + 1) % 3])) == 1) {
                    next.put(t[e], t[(e + 1) % 3]);
                }
            }
        }
        List<Integer> best = List.of();
        double widest = 0;
        while (!next.isEmpty()) {
            final List<Integer> loop = loop(next);
            double sum = 0;
            double area = 0;
            for (int k = 0; k < loop.size(); k++) {
                sum += Math.abs(off.applyAsDouble(where[loop.get(k)]));
                final V3 a = where[loop.get(k)];
                final V3 b = where[loop.get((k + 1) % loop.size())];
                area += a.x() * b.z() - b.x() * a.z();
            }
            if (sum / loop.size() < AT && Math.abs(area) > widest) {
                widest = Math.abs(area);
                best = loop;
            }
        }
        return ordered(best, where);
    }

    /** One loop taken out of {@code next}, each point to the next round it. */
    private List<Integer> loop(final Map<Integer, Integer> next) {
        final List<Integer> loop = new ArrayList<>();
        final int start = next.keySet().iterator().next();
        int at = start;
        do {
            loop.add(at);
            final Integer to = next.remove(at);
            if (to == null) {
                break;
            }
            at = to;
        } while (at != start);
        return loop;
    }

    /**
     * {@code loop}'s points in the order they run round it, turned to run the way the angle about its middle rises,
     * seen from above, from its front, the one furthest ahead; how far round each is along it, a share of the way.
     */
    private Edge ordered(final List<Integer> loop, final V3[] where) {
        V3 middle = V3.ZERO;
        for (final int i : loop) {
            middle = middle.plus(where[i].times(1.0 / loop.size()));
        }
        final V3 c = middle;
        final List<Integer> run = new ArrayList<>(loop);
        double turn = 0;
        for (int k = 0; k < run.size(); k++) {
            turn += step(angle(where[run.get(k)], c), angle(where[run.get((k + 1) % run.size())], c));
        }
        if (turn < 0) {
            Collections.reverse(run);
        }
        int first = 0;
        for (int k = 1; k < run.size(); k++) {
            if (where[run.get(k)].z() > where[run.get(first)].z()) {
                first = k;
            }
        }
        Collections.rotate(run, -first);
        final int[] at = run.stream().mapToInt(Integer::intValue).toArray();
        final V3[] w = new V3[at.length];
        final double[] r = new double[at.length];
        double along = 0;
        for (int k = 0; k < at.length; k++) {
            w[k] = where[at[k]];
            along += k == 0 ? 0 : w[k].minus(w[k - 1]).length();
            r[k] = along;
        }
        final double whole = along + w[0].minus(w[at.length - 1]).length();
        for (int k = 0; k < r.length; k++) {
            r[k] /= whole;
        }
        return new Edge(at, w, r, c);
    }

    /** How far round from {@code a} to {@code b}, the short way, radians. */
    private double step(final double a, final double b) {
        double d = b - a;
        while (d > Math.PI) {
            d -= 2 * Math.PI;
        }
        while (d < -Math.PI) {
            d += 2 * Math.PI;
        }
        return d;
    }

    /** How far round {@code p} is about {@code c}, seen from above, radians. */
    private double angle(final V3 p, final V3 c) {
        return Math.atan2(p.z() - c.z(), p.x() - c.x());
    }

    private long key(final int a, final int b) {
        return (long) Math.min(a, b) << 32 | Math.max(a, b);
    }
}
