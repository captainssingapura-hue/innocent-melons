package com.example.fxsuite.life.body;

/**
 * A value round a cycle, from a few keys: each a point of the cycle (per cent, from 0 up to but short of 100) and the
 * value there, smoothly between them (Catmull-Rom), the last key running on round into the first. How a joint turns
 * through a stride.
 */
public final class Curve {

    private final double[] at;
    private final double[] value;

    /** Keys as pairs: per cent, value; per cent rising, the first at or past 0, the last short of 100. */
    public Curve(final double... keys) {
        if (keys.length < 4 || keys.length % 2 != 0) {
            throw new IllegalArgumentException("a curve takes two keys at least, each a point and a value");
        }
        final int n = keys.length / 2;
        at = new double[n];
        value = new double[n];
        for (int k = 0; k < n; k++) {
            at[k] = keys[2 * k] / 100;
            value[k] = keys[2 * k + 1];
            if (at[k] < 0 || at[k] >= 1 || k > 0 && at[k] <= at[k - 1]) {
                throw new IllegalArgumentException("a curve's keys rise from 0 short of 100");
            }
        }
    }

    /** Its value at {@code c} of the way round, any number: only what is past a whole turn counts. */
    public double at(final double c) {
        final double t = c - Math.floor(c);
        final int n = at.length;
        // the key at or before t, round the cycle
        int k = n - 1;
        for (int i = 0; i < n; i++) {
            if (at[i] <= t) {
                k = i;
            }
        }
        final int next = (k + 1) % n;
        final double from = at[k];
        double to = at[next];
        double here = t;
        if (to <= from) {
            to += 1;
        }
        if (here < from) {
            here += 1;
        }
        final double u = (here - from) / (to - from);
        final double p0 = value[(k - 1 + n) % n];
        final double p1 = value[k];
        final double p2 = value[next];
        final double p3 = value[(k + 2) % n];
        // uneven spacing: the slopes from the keys round each, so a short gap does not overshoot
        final double before = span(k - 1, k);
        final double gap = to - from;
        final double after = span(next, next + 1);
        final double m1 = (p2 - p0) / (before + gap) * gap;
        final double m2 = (p3 - p1) / (gap + after) * gap;
        final double u2 = u * u;
        final double u3 = u2 * u;
        return (2 * u3 - 3 * u2 + 1) * p1 + (u3 - 2 * u2 + u) * m1 + (-2 * u3 + 3 * u2) * p2 + (u3 - u2) * m2;
    }

    /** How far round from key {@code a} to key {@code b}, both round the cycle. */
    private double span(final int a, final int b) {
        final int n = at.length;
        final double from = at[(a % n + n) % n];
        double to = at[(b % n + n) % n];
        if (to <= from) {
            to += 1;
        }
        return to - from;
    }
}
