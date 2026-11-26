package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * A shape found on a grid, its small shapes smoothed away and its large shape kept: each value the average of those
 * round it, so many either way along each axis in turn, so many times over, which blurs it near enough as a bell
 * would; those past the grid's edge taken as its edge's.
 */
public record Blur() implements StatelessFunctionalObject {

    public static final Blur INSTANCE = new Blur();

    /** {@code g}, each value averaged {@code reach} either way along each axis in turn, {@code passes} times over. */
    public Field.Sampled smoothed(final Field.Sampled g, final int reach, final int passes) {
        float[] v = g.values();
        for (int pass = 0; pass < passes; pass++) {
            for (int axis = 0; axis < 3; axis++) {
                v = along(v, g.n(), axis, reach);
            }
        }
        return new Field.Sampled(g.lo(), g.cell(), g.n(), v, g.box());
    }

    /** {@code v}, on a grid {@code n} across, each value the average of those {@code reach} either side on
     * {@code axis}. */
    private float[] along(final float[] v, final int[] n, final int axis, final int reach) {
        final float[] out = new float[v.length];
        final int step = axis == 0 ? 1 : axis == 1 ? n[0] : n[0] * n[1];
        java.util.stream.IntStream.range(0, v.length).parallel().forEach(at -> {
            final int c = axis == 0 ? at % n[0] : axis == 1 ? at / n[0] % n[1] : at / (n[0] * n[1]);
            double sum = 0;
            for (int d = -reach; d <= reach; d++) {
                final int e = Math.clamp(c + d, 0, n[axis] - 1);
                sum += v[at + (e - c) * step];
            }
            out[at] = (float) (sum / (2 * reach + 1));
        });
        return out;
    }
}
