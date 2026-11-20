package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A body's outline as it stands at rest: its skin, every part's, as triangles in the body's frame, to find where a
 * line out from inside it leaves it. What a garment fitted to its contour is fitted to ({@link Garment}).
 */
public final class Envelope {

    /** A part's skin: its triangles, each three corners, and the box round them all. */
    private record Skinned(List<V3[]> triangles, V3 low, V3 high) {
    }

    private final Rig rest;
    private final Map<Part, List<Skinned>> skins = new EnumMap<>(Part.class);

    /** The human skin of {@code proportions}, standing at rest. */
    public Envelope(final Proportions proportions) {
        rest = new Rig(proportions, Pose.REST);
        for (final Part p : Part.values()) {
            final Rig.Frame f = rest.frame(p);
            final List<Skinned> list = new ArrayList<>();
            for (final Surface s : Skin.shapes(p, Muscles.Bulge.REST, Puppet.TWIST, Skin.HUMAN)) {
                final List<V3> at = s.points().stream().map(f::place).toList();
                final List<V3[]> tris = new ArrayList<>();
                double[] lo = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
                double[] hi = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
                for (final V3 v : at) {
                    lo = new double[] {Math.min(lo[0], v.x()), Math.min(lo[1], v.y()), Math.min(lo[2], v.z())};
                    hi = new double[] {Math.max(hi[0], v.x()), Math.max(hi[1], v.y()), Math.max(hi[2], v.z())};
                }
                for (final int[] t : s.triangles()) {
                    tris.add(new V3[] {at.get(t[0]), at.get(t[1]), at.get(t[2])});
                }
                list.add(new Skinned(tris, new V3(lo[0], lo[1], lo[2]), new V3(hi[0], hi[1], hi[2])));
            }
            skins.put(p, list);
        }
    }

    /** How it stands. */
    public Rig rest() {
        return rest;
    }

    /**
     * How far along {@code way} (a unit direction) from {@code from} the skin of {@code parts} is left for the last
     * time, among the crossings {@code keep} keeps: the outermost; less than nothing if it crosses none.
     */
    public double out(final V3 from, final V3 way, final Set<Part> parts, final Predicate<V3> keep) {
        double far = -1;
        for (final Part p : parts) {
            for (final Skinned s : skins.get(p)) {
                if (!crosses(from, way, s.low(), s.high())) {
                    continue;
                }
                for (final V3[] t : s.triangles()) {
                    final double d = hit(from, way, t[0], t[1], t[2]);
                    if (d > far && keep.test(from.plus(way.times(d)))) {
                        far = d;
                    }
                }
            }
        }
        return far;
    }

    /**
     * How thick the skin of {@code parts} is round {@code at}, square to {@code along}: as far out as it reaches any
     * way round, cm; nothing if it is not round it.
     */
    public double thickness(final V3 at, final V3 along, final Set<Part> parts) {
        final V3 u = along.unit();
        final V3 a = u.cross(Math.abs(u.z()) < 0.9 ? V3.Z : V3.X).unit();
        final V3 b = a.cross(u).unit();
        double most = 0;
        for (int k = 0; k < 16; k++) {
            final double phi = 2 * Math.PI * k / 16;
            final double d = out(at, a.times(Math.cos(phi)).plus(b.times(Math.sin(phi))), parts, v -> true);
            most = Math.max(most, d);
        }
        return most;
    }

    /** Whether the line from {@code o} along {@code d} passes through the box from {@code lo} to {@code hi}. */
    private static boolean crosses(final V3 o, final V3 d, final V3 lo, final V3 hi) {
        double near = 0;
        double far = Double.MAX_VALUE;
        final double[] oo = {o.x(), o.y(), o.z()};
        final double[] dd = {d.x(), d.y(), d.z()};
        final double[] l = {lo.x() - 0.1, lo.y() - 0.1, lo.z() - 0.1};
        final double[] h = {hi.x() + 0.1, hi.y() + 0.1, hi.z() + 0.1};
        for (int k = 0; k < 3; k++) {
            if (Math.abs(dd[k]) < 1e-12) {
                if (oo[k] < l[k] || oo[k] > h[k]) {
                    return false;
                }
                continue;
            }
            double a = (l[k] - oo[k]) / dd[k];
            double b = (h[k] - oo[k]) / dd[k];
            if (a > b) {
                final double t = a;
                a = b;
                b = t;
            }
            near = Math.max(near, a);
            far = Math.min(far, b);
            if (near > far) {
                return false;
            }
        }
        return true;
    }

    /** How far along the line from {@code o} along {@code d} the triangle {@code a b c} is met; less than nothing if not. */
    private static double hit(final V3 o, final V3 d, final V3 a, final V3 b, final V3 c) {
        final V3 e1 = b.minus(a);
        final V3 e2 = c.minus(a);
        final V3 p = d.cross(e2);
        final double det = e1.dot(p);
        if (Math.abs(det) < 1e-12) {
            return -1;
        }
        final double inv = 1 / det;
        final V3 s = o.minus(a);
        final double u = s.dot(p) * inv;
        if (u < 0 || u > 1) {
            return -1;
        }
        final V3 q = s.cross(e1);
        final double v = d.dot(q) * inv;
        if (v < 0 || u + v > 1) {
            return -1;
        }
        final double t = e2.dot(q) * inv;
        return t > 0 ? t : -1;
    }
}
