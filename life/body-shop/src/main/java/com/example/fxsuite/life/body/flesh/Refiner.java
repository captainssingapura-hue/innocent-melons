package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A surface made finer where it is asked to be, as the front of a torso is for its abs: each triangle there split in
 * four at its edges' middles, each new point settled onto the shape the surface was found on; a triangle beside them,
 * an edge or two of it split, split as far as it must be to meet them, so that no crack opens between. Its triangles
 * still facing as they did.
 */
public record Refiner() implements StatelessFunctionalObject {

    public static final Refiner INSTANCE = new Refiner();

    /** {@code mesh}, found on {@code field}, made twice as fine where {@code where} holds of a triangle's middle. */
    public Mesher.Mesh refined(final Mesher.Mesh mesh, final Field field, final Predicate<V3> where) {
        final List<Float> points = new ArrayList<>();
        for (final float f : mesh.points()) {
            points.add(f);
        }
        final int[] t = mesh.triangles();
        final Map<Long, Integer> split = new HashMap<>();
        for (int i = 0; i < t.length; i += 3) {
            if (where.test(middle(mesh.points(), t[i], t[i + 1], t[i + 2]))) {
                for (int k = 0; k < 3; k++) {
                    split.putIfAbsent(key(t[i + k], t[i + (k + 1) % 3]), -1);
                }
            }
        }
        final int[] all = java.util.stream.IntStream.range(0, field.elements().size()).toArray();
        for (final Map.Entry<Long, Integer> e : split.entrySet()) {
            final int a = (int) (e.getKey() >> 32);
            final int b = (int) (long) e.getKey();
            final V3 mid = at(points, a).plus(at(points, b)).times(0.5);
            final V3 on = settled(field, all, mid);
            e.setValue(points.size() / 3);
            points.add((float) on.x());
            points.add((float) on.y());
            points.add((float) on.z());
        }
        final List<Integer> out = new ArrayList<>();
        for (int i = 0; i < t.length; i += 3) {
            triangle(t[i], t[i + 1], t[i + 2], split, out);
        }
        final float[] p = new float[points.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = points.get(i);
        }
        return new Mesher.Mesh(p, out.stream().mapToInt(Integer::intValue).toArray());
    }

    /** Triangle {@code a b c} split at its edges that are, into {@code out}, facing as it did. */
    private void triangle(final int a, final int b, final int c, final Map<Long, Integer> split,
                          final List<Integer> out) {
        final int[] v = {a, b, c};
        final int[] m = new int[3];
        int n = 0;
        for (int k = 0; k < 3; k++) {
            m[k] = split.getOrDefault(key(v[k], v[(k + 1) % 3]), -1);
            n += m[k] >= 0 ? 1 : 0;
        }
        if (n == 0) {
            out.addAll(List.of(a, b, c));
        } else if (n == 3) {
            out.addAll(List.of(a, m[0], m[2], m[0], b, m[1], m[2], m[1], c, m[0], m[1], m[2]));
        } else {
            for (int k = 0; k < 3; k++) {
                final int p = v[k];
                final int q = v[(k + 1) % 3];
                final int r = v[(k + 2) % 3];
                if (n == 1 && m[k] >= 0) {
                    out.addAll(List.of(p, m[k], r, m[k], q, r));
                } else if (n == 2 && m[(k + 2) % 3] < 0) {
                    out.addAll(List.of(m[k], q, m[(k + 1) % 3], p, m[k], m[(k + 1) % 3], p, m[(k + 1) % 3], r));
                }
            }
        }
    }

    /** {@code p} moved onto {@code field}'s surface, along its slope: a few steps of Newton's. */
    private V3 settled(final Field field, final int[] all, final V3 p) {
        V3 q = p;
        final double h = 0.02;
        for (int k = 0; k < 3; k++) {
            final double f = field.at(q.x(), q.y(), q.z(), all);
            final V3 g = new V3(field.at(q.x() + h, q.y(), q.z(), all) - field.at(q.x() - h, q.y(), q.z(), all),
                    field.at(q.x(), q.y() + h, q.z(), all) - field.at(q.x(), q.y() - h, q.z(), all),
                    field.at(q.x(), q.y(), q.z() + h, all) - field.at(q.x(), q.y(), q.z() - h, all)).times(0.5 / h);
            final double g2 = g.dot(g);
            if (g2 < 1e-9 || Math.abs(f) > 1) {
                return q;
            }
            q = q.minus(g.times(f / g2));
        }
        return q;
    }

    private long key(final int a, final int b) {
        return ((long) Math.min(a, b) << 32) | Math.max(a, b);
    }

    private V3 at(final List<Float> p, final int i) {
        return new V3(p.get(3 * i), p.get(3 * i + 1), p.get(3 * i + 2));
    }

    private V3 middle(final float[] p, final int a, final int b, final int c) {
        return new V3((p[3 * a] + p[3 * b] + p[3 * c]) / 3, (p[3 * a + 1] + p[3 * b + 1] + p[3 * c + 1]) / 3,
                (p[3 * a + 2] + p[3 * b + 2] + p[3 * c + 2]) / 3);
    }
}
