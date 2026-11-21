package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A shape made of level rings stacked one over another, as a limb or a trunk is ({@link Field.Shape}): each ring a
 * closed outline round its middle, level; between two rings, the outline found between theirs, as far from the middle
 * between theirs as they are on the whole, that way; beyond the top or the bottom ring, rounded shut. How far outside
 * it a point is, near enough: how much further from the middle it is, level, than the outline that way.
 */
public final class Stack implements Field.Shape {

    /** How many ways round a ring's reach is found, and kept, to look up between. */
    private static final int WAYS = 256;

    private final List<V3[]> rings;
    private final List<V3> middles;
    private final double[] box;
    private final double[][] reaches;

    /** Its rings, round their middles, top first or bottom first. */
    public Stack(final List<V3[]> rings, final List<V3> middles) {
        final boolean up = middles.getFirst().y() < middles.getLast().y();
        this.rings = new ArrayList<>(rings);
        this.middles = new ArrayList<>(middles);
        if (up) {
            java.util.Collections.reverse(this.rings);
            java.util.Collections.reverse(this.middles);
        }
        box = new double[] {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
        for (int i = 0; i < this.rings.size(); i++) {
            final double r = reachOf(i);
            for (final V3 p : this.rings.get(i)) {
                final double[] v = {p.x(), p.y(), p.z()};
                for (int k = 0; k < 3; k++) {
                    box[k] = Math.min(box[k], v[k] - r);
                    box[k + 3] = Math.max(box[k + 3], v[k] + r);
                }
            }
        }
        reaches = new double[this.rings.size()][WAYS];
        for (int i = 0; i < reaches.length; i++) {
            for (int k = 0; k < WAYS; k++) {
                final double a = 2 * Math.PI * k / WAYS;
                reaches[i][k] = found(i, Math.sin(a), Math.cos(a));
            }
        }
    }

    @Override
    public double at(final double x, final double y, final double z) {
        final int n = middles.size();
        if (y >= middles.getFirst().y()) {
            return cap(0, x, z, y - middles.getFirst().y());
        }
        if (y <= middles.getLast().y()) {
            return cap(n - 1, x, z, middles.getLast().y() - y);
        }
        int lo = 0;
        int hi = n - 1;
        while (hi - lo > 1) {
            final int mid = (lo + hi) >>> 1;
            if (middles.get(mid).y() >= y) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        final V3 a = middles.get(lo);
        final V3 b = middles.get(hi);
        final double f = (a.y() - y) / (a.y() - b.y());
        final double mx = a.x() + (b.x() - a.x()) * f;
        final double mz = a.z() + (b.z() - a.z()) * f;
        final double qx = x - mx;
        final double qz = z - mz;
        final double r = radius(lo, qx, qz) * (1 - f) + radius(hi, qx, qz) * f;
        return Math.hypot(qx, qz) - r;
    }

    @Override
    public double[] box() {
        return box.clone();
    }

    /** Beyond ring {@code i} by {@code h}: rounded shut, half as far round as it reaches. */
    private double cap(final int i, final double x, final double z, final double h) {
        final V3 m = middles.get(i);
        final double qx = x - m.x();
        final double qz = z - m.z();
        final double r = radius(i, qx, qz);
        final double round = 0.5 * r;
        final double out = Math.max(Math.hypot(qx, qz) - (r - round), 0);
        return Math.sqrt(out * out + h * h) - round;
    }

    /** How far ring {@code i}'s outline reaches from its middle the way {@code (qx, qz)} points: its table's. */
    private double radius(final int i, final double qx, final double qz) {
        final double[] r = reaches[i];
        final double a = (Math.atan2(qx, qz) / (2 * Math.PI) + 1) % 1 * WAYS;
        final int k = (int) a;
        final double f = a - k;
        return r[k % WAYS] * (1 - f) + r[(k + 1) % WAYS] * f;
    }

    /** How far ring {@code i}'s outline reaches from its middle the way {@code (dx, dz)} points, found on it. */
    private double found(final int i, final double dx, final double dz) {
        final V3 m = middles.get(i);
        final V3[] ring = rings.get(i);
        double far = -1;
        for (int k = 0; k < ring.length; k++) {
            final double ax = ring[k].x() - m.x();
            final double az = ring[k].z() - m.z();
            final double ex = ring[(k + 1) % ring.length].x() - m.x() - ax;
            final double ez = ring[(k + 1) % ring.length].z() - m.z() - az;
            final double den = dx * ez - dz * ex;
            if (Math.abs(den) > 1e-12) {
                final double s = (ax * dz - az * dx) / den;
                final double t = (ax * ez - az * ex) / den;
                if (s >= 0 && s <= 1 && t > far) {
                    far = t;
                }
            }
        }
        return far > 0 ? far : reachOf(i);
    }

    /** How far ring {@code i} reaches from its middle, on the whole. */
    private double reachOf(final int i) {
        double sum = 0;
        for (final V3 p : rings.get(i)) {
            sum += p.minus(middles.get(i)).length();
        }
        return sum / rings.get(i).length;
    }
}
