package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A sheet of cloth as it is made: its points, as the body is held to be bound, how each follows the body, and its
 * triangles, facing out. Its other side made as its mirror, the points on the middle shared; cut along a plane, the
 * cut's edge where the plane crosses it; bound to the body at last, to be moved with it ({@link Flesh}).
 */
public final class Sheet {

    private final List<V3> points = new ArrayList<>();
    private final List<Weights> weights = new ArrayList<>();
    private final List<int[]> triangles = new ArrayList<>();

    /** A point, following the body as {@code w} says: its number. */
    public int add(final V3 p, final Weights w) {
        points.add(p);
        weights.add(w);
        return points.size() - 1;
    }

    public void triangle(final int a, final int b, final int c) {
        triangles.add(new int[] {a, b, c});
    }

    /** Four points round, as a triangle's are. */
    public void quad(final int a, final int b, final int c, final int d) {
        triangle(a, b, c);
        triangle(a, c, d);
    }

    public int size() {
        return points.size();
    }

    public V3 point(final int i) {
        return points.get(i);
    }

    /** How point {@code i} follows the body. */
    public Weights weightsAt(final int i) {
        return weights.get(i);
    }

    public int triangleCount() {
        return triangles.size();
    }

    public int[] triangleAt(final int i) {
        return triangles.get(i).clone();
    }

    /** It and its mirror across the body's middle, the points on the middle ({@code x} nothing) shared. */
    public Sheet mirrored() {
        final Sheet s = new Sheet();
        final int[] other = new int[size()];
        for (int i = 0; i < size(); i++) {
            s.add(points.get(i), weights.get(i));
        }
        for (int i = 0; i < size(); i++) {
            final V3 p = points.get(i);
            other[i] = p.x() == 0 ? i : s.add(new V3(-p.x(), p.y(), p.z()), weights.get(i).mirrored());
        }
        for (final int[] t : triangles) {
            s.triangle(t[0], t[1], t[2]);
            s.triangle(other[t[0]], other[t[2]], other[t[1]]);
        }
        return s;
    }

    /**
     * What of it lies where {@code at} and the way {@code keep} say: on that side of the plane through {@code at}
     * square to {@code keep}; the triangles it crosses cut along it, the points on the cut shared between them.
     */
    public Sheet cut(final V3 at, final V3 keep) {
        final double[] d = new double[size()];
        for (int i = 0; i < size(); i++) {
            d[i] = points.get(i).minus(at).dot(keep);
        }
        final Sheet s = new Sheet();
        final int[] kept = new int[size()];
        for (int i = 0; i < size(); i++) {
            kept[i] = d[i] >= 0 ? s.add(points.get(i), weights.get(i)) : -1;
        }
        final Map<Long, Integer> edges = new HashMap<>();
        for (final int[] t : triangles) {
            final List<Integer> ring = new ArrayList<>();
            for (int e = 0; e < 3; e++) {
                final int i = t[e];
                final int j = t[(e + 1) % 3];
                if (kept[i] >= 0) {
                    ring.add(kept[i]);
                }
                if (d[i] >= 0 != d[j] >= 0) {
                    final long key = Math.min(i, j) * (long) Integer.MAX_VALUE + Math.max(i, j);
                    ring.add(edges.computeIfAbsent(key, k -> {
                        final double f = d[i] / (d[i] - d[j]);
                        return s.add(points.get(i).plus(points.get(j).minus(points.get(i)).times(f)),
                                weights.get(i).toward(weights.get(j), f));
                    }));
                }
            }
            for (int k = 1; k + 1 < ring.size(); k++) {
                s.triangle(ring.get(0), ring.get(k), ring.get(k + 1));
            }
        }
        return s;
    }

    /** Bound to the body where {@code bind} holds it, to be moved with it. */
    public Flesh flesh(final Rig bind) {
        final List<Part> parts = new ArrayList<>();
        for (final Weights w : weights) {
            for (final Part p : Part.values()) {
                if (w.by()[p.ordinal()] > 1e-4f && !parts.contains(p)) {
                    parts.add(p);
                }
            }
        }
        final float[] at = new float[3 * size()];
        final int[] which = new int[Flesh.PARTS * size()];
        final float[] shares = new float[Flesh.PARTS * size()];
        for (int i = 0; i < size(); i++) {
            at[3 * i] = (float) points.get(i).x();
            at[3 * i + 1] = (float) points.get(i).y();
            at[3 * i + 2] = (float) points.get(i).z();
            weights.get(i).strongest(parts, which, shares, Flesh.PARTS * i);
        }
        final int[] t = new int[3 * triangles.size()];
        for (int i = 0; i < triangles.size(); i++) {
            System.arraycopy(triangles.get(i), 0, t, 3 * i, 3);
        }
        return new Flesh(at, normals(at, t), t, parts, parts.stream().map(bind::frame).toList(), which, shares);
    }

    /** Each point's way out: its triangles', by their size. */
    private float[] normals(final float[] p, final int[] t) {
        final float[] n = new float[p.length];
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            final V3 f = b.minus(a).cross(c.minus(a));
            for (int k = 0; k < 3; k++) {
                n[3 * t[i + k]] += (float) f.x();
                n[3 * t[i + k] + 1] += (float) f.y();
                n[3 * t[i + k] + 2] += (float) f.z();
            }
        }
        for (int i = 0; i < n.length; i += 3) {
            final double l = Math.sqrt(n[i] * n[i] + n[i + 1] * n[i + 1] + n[i + 2] * n[i + 2]);
            for (int k = 0; k < 3; k++) {
                n[i + k] = l > 0 ? (float) (n[i + k] / l) : 0;
            }
        }
        return n;
    }
}
