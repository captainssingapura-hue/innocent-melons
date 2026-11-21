package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** What is round each point of a surface of triangles: its way out, its triangles' by their size; its neighbours. */
public record Around() implements StatelessFunctionalObject {

    public static final Around INSTANCE = new Around();

    /** Each point of {@code at}'s way out, triangles {@code t} facing out. */
    public V3[] normals(final V3[] at, final int[] t) {
        final V3[] n = new V3[at.length];
        Arrays.fill(n, V3.ZERO);
        for (int i = 0; i < t.length; i += 3) {
            final V3 f = at[t[i + 1]].minus(at[t[i]]).cross(at[t[i + 2]].minus(at[t[i]]));
            for (int k = 0; k < 3; k++) {
                n[t[i + k]] = n[t[i + k]].plus(f);
            }
        }
        for (int i = 0; i < n.length; i++) {
            n[i] = n[i].length() > 0 ? n[i].unit() : V3.Y;
        }
        return n;
    }

    /** Each of {@code size} points' neighbours, by the sides of triangles {@code t}. */
    public int[][] neighbours(final int size, final int[] t) {
        final List<Set<Integer>> by = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            by.add(new TreeSet<>());
        }
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                by.get(t[i + k]).add(t[i + (k + 1) % 3]);
                by.get(t[i + (k + 1) % 3]).add(t[i + k]);
            }
        }
        return by.stream().map(x -> x.stream().mapToInt(Integer::intValue).toArray()).toArray(int[][]::new);
    }

    /** The middle of {@code at}'s points {@code of}. */
    public V3 middle(final V3[] at, final int[] of) {
        V3 m = V3.ZERO;
        for (final int j : of) {
            m = m.plus(at[j].times(1.0 / of.length));
        }
        return m;
    }
}
