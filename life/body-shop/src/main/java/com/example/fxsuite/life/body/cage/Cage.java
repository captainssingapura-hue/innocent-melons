package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.List;

/**
 * A surface as a cage: a few points, the faces between them, each its points round the way it faces out, and creases
 * along runs of its edges, each as sharp as it says ({@link Crease}). Not the surface itself, but what it is smoothed
 * from, finer at each step ({@link Subdivision}): an edge not creased smoothed away, one creased kept as sharp as its
 * crease for so many steps, then smoothed too. Each point a name, to be told by.
 */
public final class Cage {

    private final List<V3> points;
    private final List<String> names;
    private final List<int[]> faces;
    private final List<Crease> creases;

    /** {@code points}, each its name in {@code names}; its {@code faces}, each its points round; its creases. */
    public Cage(final List<V3> points, final List<String> names, final List<int[]> faces,
                final List<Crease> creases) {
        if (names.size() != points.size()) {
            throw new IllegalArgumentException(points.size() + " points but " + names.size() + " names");
        }
        this.points = List.copyOf(points);
        this.names = List.copyOf(names);
        final List<int[]> f = new ArrayList<>();
        faces.forEach(x -> f.add(x.clone()));
        this.faces = List.copyOf(f);
        this.creases = List.copyOf(creases);
    }

    /** Its points. */
    public List<V3> points() {
        return points;
    }

    /** Point {@code i}'s name. */
    public String name(final int i) {
        return names.get(i);
    }

    /** How many points it has. */
    public int size() {
        return points.size();
    }

    /** Its faces, each its points round the way it faces out (each a copy). */
    public List<int[]> faces() {
        final List<int[]> f = new ArrayList<>();
        faces.forEach(x -> f.add(x.clone()));
        return f;
    }

    /** Its creases. */
    public List<Crease> creases() {
        return creases;
    }

    /** The same cage, its points moved to {@code to}, one for one. */
    public Cage moved(final List<V3> to) {
        return new Cage(to, names, faces, creases);
    }

    /** Its edges, each once, the lower-numbered point first. */
    public List<int[]> edges() {
        final java.util.Set<Long> seen = new java.util.HashSet<>();
        final List<int[]> out = new ArrayList<>();
        for (final int[] f : faces) {
            for (int i = 0; i < f.length; i++) {
                final int a = Math.min(f[i], f[(i + 1) % f.length]);
                final int b = Math.max(f[i], f[(i + 1) % f.length]);
                if (seen.add(((long) a << 32) | b)) {
                    out.add(new int[] {a, b});
                }
            }
        }
        return out;
    }
}
