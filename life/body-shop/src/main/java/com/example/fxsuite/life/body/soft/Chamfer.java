package com.example.fxsuite.life.body.soft;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How far each point of a grid is from the nearest point marked, in steps of the grid, near enough: found by going
 * over the grid twice, forward and back, each point taking the least its neighbours already gone over have, plus the
 * step to them, straight, across a face's diagonal or the cube's (a chamfer). A point marked is nought from itself.
 */
public record Chamfer() implements StatelessFunctionalObject {

    public static final Chamfer INSTANCE = new Chamfer();

    /** Further than any point can be, in steps. */
    private static final float FAR = 1e6f;

    /**
     * How far each point of a grid {@code n} points each way (across, up, ahead; numbered across, then up, then ahead)
     * is from the nearest of those {@code marked} says, in steps; as far as {@link #FAR} if none is.
     */
    public float[] from(final boolean[] marked, final int[] n) {
        final float[] d = new float[marked.length];
        for (int i = 0; i < d.length; i++) {
            d[i] = marked[i] ? 0 : FAR;
        }
        for (final int way : new int[] {1, -1}) {
            for (int k = way > 0 ? 0 : n[2] - 1; k >= 0 && k < n[2]; k += way) {
                for (int j = way > 0 ? 0 : n[1] - 1; j >= 0 && j < n[1]; j += way) {
                    for (int i = way > 0 ? 0 : n[0] - 1; i >= 0 && i < n[0]; i += way) {
                        d[(k * n[1] + j) * n[0] + i] = nearest(d, n, i, j, k, way);
                    }
                }
            }
        }
        return d;
    }

    /** The least of point {@code i}, {@code j}, {@code k}'s own and its neighbours' gone over already, plus the step. */
    private float nearest(final float[] d, final int[] n, final int i, final int j, final int k, final int way) {
        float best = d[(k * n[1] + j) * n[0] + i];
        for (int dk = -1; dk <= 0; dk++) {
            for (int dj = -1; dj <= 1; dj++) {
                for (int di = -1; di <= 1; di++) {
                    if (dk == 0 && (dj > 0 || dj == 0 && di >= 0)) {
                        continue;
                    }
                    final int a = i + way * di;
                    final int b = j + way * dj;
                    final int c = k + way * dk;
                    if (a < 0 || b < 0 || c < 0 || a >= n[0] || b >= n[1] || c >= n[2]) {
                        continue;
                    }
                    best = Math.min(best, d[(c * n[1] + b) * n[0] + a] + (float) Math.sqrt(di * di + dj * dj + dk * dk));
                }
            }
        }
        return best;
    }
}
