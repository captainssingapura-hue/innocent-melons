package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.List;

/**
 * A cage as it is put together ({@link Cage}): points added one at a time, each its name; faces between them; creases;
 * what was put together on the right then mirrored onto the left, each point its fellow, each face turned round so that
 * it still faces out.
 */
public final class Building {

    private final List<V3> points = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private final List<int[]> faces = new ArrayList<>();
    private final List<Crease> creases = new ArrayList<>();
    private final List<Integer> fellows = new ArrayList<>();

    /** Point {@code p}, called {@code said}, added; its number. */
    public int add(final V3 p, final String said) {
        points.add(p);
        names.add(said);
        fellows.add(-1);
        return points.size() - 1;
    }

    /** Point {@code i}. */
    public V3 point(final int i) {
        return points.get(i);
    }

    /** How many points so far. */
    public int size() {
        return points.size();
    }

    /** How many faces so far. */
    public int faceCount() {
        return faces.size();
    }

    /** A face through {@code corners}, round the way it faces out. */
    public void face(final int... corners) {
        faces.add(corners.clone());
    }

    /** A crease. */
    public void crease(final Crease c) {
        creases.add(c);
    }

    /** Points {@code a} and {@code b} each other's fellow across the middle; a point in the middle its own. */
    public void fellows(final int a, final int b) {
        fellows.set(a, b);
        fellows.set(b, a);
    }

    /**
     * The points from {@code from} on and the faces from {@code facesFrom} on, put together on the right, mirrored onto
     * the left: each point that has no fellow yet given one, across the middle, called as on the left.
     */
    public void mirror(final int from, final int facesFrom) {
        final int to = points.size();
        for (int i = from; i < to; i++) {
            if (fellows.get(i) < 0) {
                final V3 p = points.get(i);
                fellows(i, add(new V3(-p.x(), p.y(), p.z()), names.get(i).replace("right", "left")));
            }
        }
        final int faceTo = faces.size();
        for (int f = facesFrom; f < faceTo; f++) {
            final int[] c = faces.get(f);
            final int[] m = new int[c.length];
            for (int k = 0; k < c.length; k++) {
                m[k] = fellows.get(c[c.length - 1 - k]);
            }
            faces.add(m);
        }
    }

    /**
     * The faces from {@code from} on each turned round, if need be, to go round the way the faces before them go:
     * from each face known, every face across one of its edges made to cross that edge the other way, and on from it;
     * so that a surface put together piece by piece faces out all the one way, whatever its shape.
     */
    public void agree(final int from) {
        final java.util.Map<Long, List<Integer>> by = new java.util.HashMap<>();
        for (int f = 0; f < faces.size(); f++) {
            final int[] c = faces.get(f);
            for (int k = 0; k < c.length; k++) {
                by.computeIfAbsent(edge(c[k], c[(k + 1) % c.length]), x -> new ArrayList<>()).add(f);
            }
        }
        final boolean[] known = new boolean[faces.size()];
        final java.util.ArrayDeque<Integer> next = new java.util.ArrayDeque<>();
        for (int f = 0; f < from; f++) {
            known[f] = true;
            next.add(f);
        }
        while (!next.isEmpty()) {
            final int[] c = faces.get(next.poll());
            for (int k = 0; k < c.length; k++) {
                final int a = c[k];
                final int b = c[(k + 1) % c.length];
                for (final int g : by.get(edge(a, b))) {
                    if (!known[g]) {
                        known[g] = true;
                        if (goes(faces.get(g), a, b)) {
                            faces.set(g, reversed(faces.get(g)));
                        }
                        next.add(g);
                    }
                }
            }
        }
    }

    private long edge(final int a, final int b) {
        return ((long) Math.min(a, b) << 32) | Math.max(a, b);
    }

    /** Whether face {@code c} goes from {@code a} straight to {@code b}. */
    private boolean goes(final int[] c, final int a, final int b) {
        for (int k = 0; k < c.length; k++) {
            if (c[k] == a && c[(k + 1) % c.length] == b) {
                return true;
            }
        }
        return false;
    }

    private int[] reversed(final int[] c) {
        final int[] r = new int[c.length];
        for (int k = 0; k < c.length; k++) {
            r[k] = c[c.length - 1 - k];
        }
        return r;
    }

    /** Point {@code i}'s fellow across the middle. */
    public int fellow(final int i) {
        return fellows.get(i);
    }

    /** The cage put together. */
    public Cage cage() {
        return new Cage(points, names, faces, creases);
    }
}
