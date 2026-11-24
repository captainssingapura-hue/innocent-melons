package com.example.fxsuite.math.form;

import com.example.fxsuite.math.graph.Point.Spatial;

import java.util.List;

/**
 * A line run through points in turn, straight from each to the next: where it is a share of its length along, and how
 * long it is. A side of a face, a path along a form's edges, traced.
 */
public final class Run {

    /** How far either side of a point, as a share of its length, the way it runs there is taken over. */
    private static final double STEP = 0.01;

    private final List<Spatial> points;
    private final double[] along;

    /** Through {@code points}, at least one: a run of one point stays at it. */
    public Run(final List<Spatial> points) {
        if (points.isEmpty()) {
            throw new IllegalArgumentException("a run through no point");
        }
        this.points = List.copyOf(points);
        this.along = new double[points.size()];
        for (int i = 1; i < points.size(); i++) {
            along[i] = along[i - 1] + points.get(i).distance(points.get(i - 1));
        }
    }

    /** Its points, in turn. */
    public List<Spatial> points() {
        return points;
    }

    /** How long it is. */
    public double length() {
        return along[along.length - 1];
    }

    /** Where it is {@code t} of its length along, 0 at its first point and 1 at its last; held at its ends. */
    public Spatial at(final double t) {
        final double length = length();
        if (length <= 0 || t <= 0) {
            return points.getFirst();
        }
        if (t >= 1) {
            return points.getLast();
        }
        final double want = t * length;
        int lo = 0;
        int hi = along.length - 1;
        while (hi - lo > 1) {
            final int mid = (lo + hi) >>> 1;
            if (along[mid] <= want) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        final double span = along[hi] - along[lo];
        return span <= 0 ? points.get(lo) : points.get(lo).toward(points.get(hi), (want - along[lo]) / span);
    }

    /** The way it runs {@code t} of its length along, a unit long; none for a run of one point. */
    public Spatial way(final double t) {
        final Spatial ahead = at(Math.min(1, t + STEP));
        final Spatial behind = at(Math.max(0, t - STEP));
        return ahead.minus(behind).unit();
    }
}
