package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A surface cut ({@link NeckJoin}): its triangles kept where they are on the side kept, each one the cut passes through
 * cut along it, at a point made on each edge it crosses, as far along it as the cut is, so that what is kept ends on
 * the cut itself and nothing kept is moved; each made point known by the two it is between and how far along.
 */
public record NeckCut() implements StatelessFunctionalObject {

    public static final NeckCut INSTANCE = new NeckCut();

    /**
     * What is kept: its triangles; all the surface's points, those made after; for each made point, the two it is
     * between, the one kept first, and how far from that one toward the other.
     *
     * @param kept its triangles
     * @param where every point, the made ones after the surface's own
     * @param made for each made point, the kept point and the other it is between
     * @param along for each made point, how far from the kept one toward the other
     */
    public record Cut(List<int[]> kept, V3[] where, int[][] made, double[] along) {
    }

    /** {@code triangles} of a surface whose points are {@code where}, cut where {@code off} is none, kept less. */
    public Cut cut(final int[] triangles, final V3[] where, final double[] off) {
        final List<int[]> kept = new ArrayList<>();
        final List<V3> made = new ArrayList<>();
        final List<int[]> between = new ArrayList<>();
        final List<Double> along = new ArrayList<>();
        final Map<Long, Integer> onEdge = new HashMap<>();
        for (int i = 0; i < triangles.length; i += 3) {
            final int[] t = {triangles[i], triangles[i + 1], triangles[i + 2]};
            int in = 0;
            for (final int v : t) {
                in += off[v] < 0 ? 1 : 0;
            }
            if (in == 3) {
                kept.add(t);
            } else if (in > 0) {
                int r = 0;
                while (!(off[t[r]] < 0) || in == 2 && !(off[t[(r + 1) % 3]] < 0)) {
                    r++;
                }
                final int a = t[r];
                final int b = t[(r + 1) % 3];
                final int c = t[(r + 2) % 3];
                final Made m = new Made(where, off, made, between, along, onEdge);
                if (in == 1) {
                    kept.add(new int[] {a, m.on(a, b), m.on(a, c)});
                } else {
                    kept.add(new int[] {a, b, m.on(b, c)});
                    kept.add(new int[] {a, m.on(b, c), m.on(a, c)});
                }
            }
        }
        final V3[] all = new V3[where.length + made.size()];
        System.arraycopy(where, 0, all, 0, where.length);
        for (int k = 0; k < made.size(); k++) {
            all[where.length + k] = made.get(k);
        }
        return new Cut(kept, all, between.toArray(new int[0][]),
                along.stream().mapToDouble(Double::doubleValue).toArray());
    }

    /** The points made on edges, each once, numbered after the surface's own. */
    private record Made(V3[] where, double[] off, List<V3> made, List<int[]> between, List<Double> along,
                        Map<Long, Integer> onEdge) {

        /** The point made where the cut crosses from kept {@code in} to {@code out}. */
        int on(final int in, final int out) {
            final long key = (long) Math.min(in, out) << 32 | Math.max(in, out);
            final Integer had = onEdge.get(key);
            if (had != null) {
                return had;
            }
            final double t = off[in] / (off[in] - off[out]);
            made.add(where[in].times(1 - t).plus(where[out].times(t)));
            between.add(new int[] {in, out});
            along.add(t);
            final int at = where.length + made.size() - 1;
            onEdge.put(key, at);
            return at;
        }
    }
}
