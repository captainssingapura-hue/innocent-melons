package com.example.fxsuite.life.body.wear;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How a surface's points follow the body ({@link Weights}), spread along it: each point's made half its own and half
 * its neighbours', so many times; so that neighbours follow the same parts, or nearly, and the surface stretches
 * between them as the body moves, as skin does, rather than tearing where one point took after one muscle beneath and
 * the next after another.
 */
public record Spread() implements StatelessFunctionalObject {

    public static final Spread INSTANCE = new Spread();

    /** {@code w} spread along the surface whose points' neighbours are {@code next}, {@code rounds} times. */
    public Weights[] over(final Weights[] w, final int[][] next, final int rounds) {
        float[][] by = new float[w.length][];
        for (int i = 0; i < w.length; i++) {
            by[i] = w[i].by().clone();
        }
        for (int round = 0; round < rounds; round++) {
            final float[][] now = new float[w.length][];
            for (int i = 0; i < w.length; i++) {
                final float[] m = new float[by[i].length];
                for (final int j : next[i]) {
                    for (int q = 0; q < m.length; q++) {
                        m[q] += by[j][q] / next[i].length;
                    }
                }
                for (int q = 0; q < m.length; q++) {
                    m[q] = 0.5f * by[i][q] + 0.5f * m[q];
                }
                now[i] = m;
            }
            by = now;
        }
        final Weights[] out = new Weights[w.length];
        for (int i = 0; i < w.length; i++) {
            out[i] = new Weights(by[i]);
        }
        return out;
    }
}
