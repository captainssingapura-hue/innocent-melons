package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * A closed skin round a part of a body: rings of points, each as many round, one after another along it, each end
 * closed on a point of its own ({@code start}, {@code end}). Seen smooth, if {@code smooth}, as a limb or a head is;
 * else flat-faced, as a plate is, each of its sides and its ends a face of its own.
 *
 * <p>Made, it is turned so that its skin faces outward: going round each of its triangles one way, the same way for
 * every surface, whichever way its rings run.
 *
 * @param name   what part it is
 * @param paint  what it is coloured as ({@link Paint})
 * @param rings  its rings, in order along it, each as many points round
 * @param start  the point closing it before its first ring
 * @param end    the point closing it after its last
 * @param smooth whether it is seen smooth
 */
public record Surface(String name, Paint paint, List<List<V3>> rings, V3 start, V3 end, boolean smooth) {

    public Surface {
        final List<List<V3>> given = rings.stream().map(List::copyOf).toList();
        if (given.isEmpty() || given.stream().anyMatch(r -> r.size() != given.getFirst().size() || r.size() < 3)) {
            throw new IllegalArgumentException(name + ": rings, each as many round, three or more");
        }
        rings = volume(given, start, end) >= 0 ? given : given.stream().map(r -> {
            final List<V3> back = new ArrayList<>(r);
            java.util.Collections.reverse(back);
            return List.copyOf(back);
        }).toList();
    }

    /** How many points round each ring. */
    public int round() {
        return rings.getFirst().size();
    }

    /** Its points, in the order its triangles name them: its start, every ring in turn, its end. */
    public List<V3> points() {
        final List<V3> all = new ArrayList<>(rings.size() * round() + 2);
        all.add(start);
        rings.forEach(all::addAll);
        all.add(end);
        return all;
    }

    /**
     * Its triangles, each three of its points ({@link #points}), going round the way that faces outward; and which
     * face each is of (for a flat-faced one): its start's end 0, each side the next, its end's the last.
     */
    public List<int[]> triangles() {
        return triangles(rings.size(), round());
    }

    /** Which of its faces each of its triangles is on ({@link #triangles}'s order): for shading a flat-faced one. */
    public List<Integer> faces() {
        final int n = round();
        final List<Integer> faces = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            faces.add(0);
        }
        for (int i = 0; i + 1 < rings.size(); i++) {
            for (int j = 0; j < n; j++) {
                faces.add(1 + j);
                faces.add(1 + j);
            }
        }
        for (int j = 0; j < n; j++) {
            faces.add(n + 1);
        }
        return faces;
    }

    private static List<int[]> triangles(final int count, final int n) {
        final List<int[]> t = new ArrayList<>();
        final int last = 1 + count * n;
        for (int j = 0; j < n; j++) {
            t.add(new int[] {0, 1 + j, 1 + (j + 1) % n});
        }
        for (int i = 0; i + 1 < count; i++) {
            for (int j = 0; j < n; j++) {
                final int a = 1 + i * n + j;
                final int b = 1 + (i + 1) * n + j;
                final int c = 1 + (i + 1) * n + (j + 1) % n;
                final int d = 1 + i * n + (j + 1) % n;
                t.add(new int[] {a, b, c});
                t.add(new int[] {a, c, d});
            }
        }
        final int lastRing = 1 + (count - 1) * n;
        for (int j = 0; j < n; j++) {
            t.add(new int[] {last, lastRing + (j + 1) % n, lastRing + j});
        }
        return t;
    }

    /** How much it holds, going round its triangles as they are: less than nothing if they face inward. */
    public double volume() {
        return volume(rings, start, end);
    }

    private static double volume(final List<List<V3>> rings, final V3 start, final V3 end) {
        final List<V3> all = new ArrayList<>();
        all.add(start);
        rings.forEach(all::addAll);
        all.add(end);
        double v = 0;
        for (final int[] t : triangles(rings.size(), rings.getFirst().size())) {
            v += all.get(t[0]).dot(all.get(t[1]).cross(all.get(t[2])));
        }
        return v / 6;
    }

    /** It, every point as {@code to} places it. */
    public Surface moved(final java.util.function.UnaryOperator<V3> to) {
        return new Surface(name, paint, rings.stream().map(r -> r.stream().map(to).toList()).toList(),
                to.apply(start), to.apply(end), smooth);
    }

    /** How low it reaches. */
    public double lowest() {
        return points().stream().mapToDouble(V3::y).min().orElseThrow();
    }
}
