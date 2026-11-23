package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Which of a grid's points are inside a surface open at its foot, a head's blank open at its neck ({@link MeshSolid}):
 * those from which a line straight up crosses the surface an odd number of times. Up, so that the open neck, facing
 * down, lets no line out unseen, and an eye's or the mouth's opening, facing ahead, scarcely any, where a flood from
 * outside would come in through any of them and leave the head a shell. What is straight under the opening is in, as
 * if it were shut there; the blank's grid starts at its foot, nothing under it. Each line a little off the grid's own,
 * not to pass through a corner or an edge of two triangles at once; each line's crossings found among only the
 * triangles over it.
 */
public record Crossings() implements StatelessFunctionalObject {

    public static final Crossings INSTANCE = new Crossings();

    /** How far off the grid's own each line goes, in cells, across and ahead. */
    private static final double OFF_X = 1.37e-3;
    private static final double OFF_Z = 2.11e-3;

    /**
     * Whether each point of the grid from {@code lo}, {@code cell} apart, {@code n} points each way, numbered across,
     * then up, then ahead, is inside {@code m}.
     */
    public boolean[] inside(final Mesher.Mesh m, final double[] lo, final double cell, final int[] n) {
        final List<List<Integer>> over = over(m, lo, cell, n);
        final boolean[] in = new boolean[n[0] * n[1] * n[2]];
        IntStream.range(0, n[2]).parallel().forEach(k -> {
            for (int i = 0; i < n[0]; i++) {
                final double[] up = crossings(m, over.get(k * n[0] + i), lo[0] + (i + OFF_X) * cell,
                        lo[2] + (k + OFF_Z) * cell);
                int above = up.length;
                int next = 0;
                for (int j = 0; j < n[1]; j++) {
                    final double y = lo[1] + j * cell;
                    while (next < up.length && up[next] <= y) {
                        next++;
                        above--;
                    }
                    in[(k * n[1] + j) * n[0] + i] = above % 2 == 1;
                }
            }
        });
        return in;
    }

    /** For each line up the grid, numbered across then ahead, the triangles of {@code m} over it. */
    private List<List<Integer>> over(final Mesher.Mesh m, final double[] lo, final double cell, final int[] n) {
        final List<List<Integer>> over = new ArrayList<>();
        for (int c = 0; c < n[0] * n[2]; c++) {
            over.add(new ArrayList<>());
        }
        final float[] p = m.points();
        final int[] t = m.triangles();
        for (int tri = 0; tri < t.length / 3; tri++) {
            double x0 = Double.MAX_VALUE;
            double x1 = -Double.MAX_VALUE;
            double z0 = Double.MAX_VALUE;
            double z1 = -Double.MAX_VALUE;
            for (int c = 0; c < 3; c++) {
                x0 = Math.min(x0, p[3 * t[3 * tri + c]]);
                x1 = Math.max(x1, p[3 * t[3 * tri + c]]);
                z0 = Math.min(z0, p[3 * t[3 * tri + c] + 2]);
                z1 = Math.max(z1, p[3 * t[3 * tri + c] + 2]);
            }
            final int i0 = Math.max(0, (int) Math.floor((x0 - lo[0]) / cell));
            final int i1 = Math.min(n[0] - 1, (int) Math.ceil((x1 - lo[0]) / cell));
            final int k0 = Math.max(0, (int) Math.floor((z0 - lo[2]) / cell));
            final int k1 = Math.min(n[2] - 1, (int) Math.ceil((z1 - lo[2]) / cell));
            for (int k = k0; k <= k1; k++) {
                for (int i = i0; i <= i1; i++) {
                    over.get(k * n[0] + i).add(tri);
                }
            }
        }
        return over;
    }

    /**
     * How high the line up at ({@code x}, {@code z}) crosses each of {@code tris}, of {@code m}, it does, lowest first.
     */
    private double[] crossings(final Mesher.Mesh m, final List<Integer> tris, final double x, final double z) {
        final float[] p = m.points();
        final int[] t = m.triangles();
        final double[] ys = new double[tris.size()];
        int found = 0;
        for (final int tri : tris) {
            final int a = 3 * t[3 * tri];
            final int b = 3 * t[3 * tri + 1];
            final int c = 3 * t[3 * tri + 2];
            final double d = (p[b] - p[a]) * (p[c + 2] - p[a + 2]) - (p[c] - p[a]) * (p[b + 2] - p[a + 2]);
            if (Math.abs(d) < 1e-12) {
                continue;
            }
            final double u = ((x - p[a]) * (p[c + 2] - p[a + 2]) - (p[c] - p[a]) * (z - p[a + 2])) / d;
            final double v = ((p[b] - p[a]) * (z - p[a + 2]) - (x - p[a]) * (p[b + 2] - p[a + 2])) / d;
            if (u >= 0 && v >= 0 && u + v <= 1) {
                ys[found++] = p[a + 1] + u * (p[b + 1] - p[a + 1]) + v * (p[c + 1] - p[a + 1]);
            }
        }
        final double[] up = Arrays.copyOf(ys, found);
        Arrays.sort(up);
        return up;
    }
}
