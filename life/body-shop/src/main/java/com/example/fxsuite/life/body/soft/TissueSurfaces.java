package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A tissue's surface as a region's cells give it ({@link TissueMap}): every face between a cell of it and one of
 * anything else, or the region's edge, two triangles going round the way out of it; then smoothed, each point drawn
 * toward its neighbours and back a little less, by turns (Taubin's way), so the cells' steps are rounded off without
 * the whole shrinking.
 */
public record TissueSurfaces() implements StatelessFunctionalObject {

    public static final TissueSurfaces INSTANCE = new TissueSurfaces();

    /** How far a smoothing step draws a point to its neighbours, then back. */
    private static final double TOWARD = 0.5;
    private static final double BACK = -0.53;
    /** The six ways out of a cell: which way, and its face's four corners, by their bits, going round the way out. */
    private static final int[][] WAYS = {{0, -1}, {0, 1}, {1, -1}, {1, 1}, {2, -1}, {2, 1}};

    /** The surface of what {@code map}'s cells label {@code what}, smoothed {@code smooth} times. */
    public Mesher.Mesh of(final TissueMap map, final int what, final int smooth) {
        final int[] n = map.counts();
        final Map<Long, Integer> number = new HashMap<>();
        final List<Float> points = new ArrayList<>();
        final List<Integer> triangles = new ArrayList<>();
        for (int c = 0; c < n[0] * n[1] * n[2]; c++) {
            if (map.label(c) != what) {
                continue;
            }
            final int[] at = {c % n[0], c / n[0] % n[1], c / (n[0] * n[1])};
            for (final int[] way : WAYS) {
                final int[] next = at.clone();
                next[way[0]] += way[1];
                if (inside(next, n) && map.label((next[2] * n[1] + next[1]) * n[0] + next[0]) == what) {
                    continue;
                }
                face(map, at, way[0], way[1], number, points, triangles);
            }
        }
        final float[] p = new float[points.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = points.get(i);
        }
        final int[] t = triangles.stream().mapToInt(Integer::intValue).toArray();
        for (int s = 0; s < smooth; s++) {
            stepped(p, t, TOWARD);
            stepped(p, t, BACK);
        }
        return new Mesher.Mesh(p, t);
    }

    private boolean inside(final int[] c, final int[] n) {
        return c[0] >= 0 && c[1] >= 0 && c[2] >= 0 && c[0] < n[0] && c[1] < n[1] && c[2] < n[2];
    }

    /** Cell {@code at}'s face on side {@code side} of way {@code axis}: two triangles going round the way out. */
    private void face(final TissueMap map, final int[] at, final int axis, final int side, final Map<Long, Integer> number,
                      final List<Float> points, final List<Integer> triangles) {
        final int u = (axis + 1) % 3;
        final int v = (axis + 2) % 3;
        final int[] corner = new int[4];
        final int[][] steps = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};
        for (int k = 0; k < 4; k++) {
            final int[] g = at.clone();
            g[axis] += side > 0 ? 1 : 0;
            g[u] += steps[k][0];
            g[v] += steps[k][1];
            corner[k] = point(map, g, number, points);
        }
        // u then v goes round the way out of the high side of the axis
        final int[] order = side > 0 ? new int[] {0, 1, 2, 3} : new int[] {0, 3, 2, 1};
        triangles.add(corner[order[0]]);
        triangles.add(corner[order[1]]);
        triangles.add(corner[order[2]]);
        triangles.add(corner[order[0]]);
        triangles.add(corner[order[2]]);
        triangles.add(corner[order[3]]);
    }

    /** The point at grid corner {@code g}, numbered as it is first met. */
    private int point(final TissueMap map, final int[] g, final Map<Long, Integer> number, final List<Float> points) {
        final long key = ((long) g[0] << 42) | ((long) g[1] << 21) | g[2];
        return number.computeIfAbsent(key, x -> {
            final double[] lo = map.lowest();
            for (int c = 0; c < 3; c++) {
                points.add((float) (lo[c] + g[c] * map.cell()));
            }
            return points.size() / 3 - 1;
        });
    }

    /** Every point of {@code p} moved {@code by} of the way to the middle of its neighbours by {@code t}, at once. */
    private void stepped(final float[] p, final int[] t, final double by) {
        final double[] sum = new double[p.length];
        final int[] many = new int[p.length / 3];
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                final int a = t[i + k];
                final int b = t[i + (k + 1) % 3];
                for (int c = 0; c < 3; c++) {
                    sum[3 * a + c] += p[3 * b + c];
                    sum[3 * b + c] += p[3 * a + c];
                }
                many[a]++;
                many[b]++;
            }
        }
        for (int i = 0; i < many.length; i++) {
            if (many[i] > 0) {
                for (int c = 0; c < 3; c++) {
                    p[3 * i + c] += (float) (by * (sum[3 * i + c] / many[i] - p[3 * i + c]));
                }
            }
        }
    }
}
