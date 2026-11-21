package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Torso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A torso's widths at any height ({@link Torso}): how far it reaches either side, ahead and behind its middle, and how
 * far ahead its middle is, smooth between the heights it is given at, never overshooting them (Fritsch and Carlson's
 * curve); and so where its skin is, round an oval of them, at any height and angle round from straight ahead, to the
 * right. In the drawn figure's centimetres, as it is held to be bound.
 */
public final class Profile {

    private final double[] y;
    private final double[][] widths = new double[4][];
    private final double[][] slopes = new double[4][];

    /** {@code torso}'s profile, on the body {@code bound} holds. */
    public Profile(final Torso torso, final Rig bound) {
        final List<Torso.Key> keys = new ArrayList<>(torso.keys());
        keys.sort(Comparator.comparingDouble(k -> k.height().y(bound)));
        final int n = keys.size();
        y = new double[n];
        for (int c = 0; c < 4; c++) {
            widths[c] = new double[n];
        }
        for (int i = 0; i < n; i++) {
            final Torso.Key k = keys.get(i);
            y[i] = k.height().y(bound);
            widths[0][i] = k.side();
            widths[1][i] = k.ahead();
            widths[2][i] = k.behind();
            widths[3][i] = k.middle();
        }
        for (int c = 0; c < 4; c++) {
            slopes[c] = slopes(widths[c]);
        }
    }

    /** Where the skin is at height {@code h}, {@code degrees} round from straight ahead, out {@code out} further. */
    public V3 at(final double h, final double degrees, final double out) {
        final double t = Math.toRadians(degrees);
        final double side = width(0, h);
        final double deep = Math.cos(t) >= 0 ? width(1, h) : width(2, h);
        final double sx = Math.sin(t) / side;
        final double sz = Math.cos(t) / deep;
        final double r = 1 / Math.sqrt(sx * sx + sz * sz) + out;
        return new V3(Math.sin(t) * r, h, width(3, h) + Math.cos(t) * r);
    }

    /** How far its middle is ahead at height {@code h}. */
    public double middle(final double h) {
        return width(3, h);
    }

    /** Width {@code c} (0 to either side, 1 ahead, 2 behind, 3 the middle ahead) at height {@code h}. */
    private double width(final int c, final double h) {
        final double[] v = widths[c];
        final double[] m = slopes[c];
        final double at = Math.clamp(h, y[0], y[y.length - 1]);
        int i = 0;
        while (i < y.length - 2 && at > y[i + 1]) {
            i++;
        }
        final double d = y[i + 1] - y[i];
        final double t = (at - y[i]) / d;
        final double t2 = t * t;
        final double t3 = t2 * t;
        return (2 * t3 - 3 * t2 + 1) * v[i] + (t3 - 2 * t2 + t) * d * m[i] + (-2 * t3 + 3 * t2) * v[i + 1]
                + (t3 - t2) * d * m[i + 1];
    }

    /** The slopes at the heights of a curve through {@code v} that does not overshoot. */
    private double[] slopes(final double[] v) {
        final int n = y.length;
        final double[] d = new double[n - 1];
        for (int i = 0; i + 1 < n; i++) {
            d[i] = (v[i + 1] - v[i]) / (y[i + 1] - y[i]);
        }
        final double[] m = new double[n];
        m[0] = d[0];
        m[n - 1] = d[n - 2];
        for (int i = 1; i + 1 < n; i++) {
            if (d[i - 1] * d[i] <= 0) {
                m[i] = 0;
            } else {
                final double h0 = y[i] - y[i - 1];
                final double h1 = y[i + 1] - y[i];
                final double w1 = 2 * h1 + h0;
                final double w2 = h1 + 2 * h0;
                m[i] = (w1 + w2) / (w1 / d[i - 1] + w2 / d[i]);
            }
        }
        return m;
    }
}
