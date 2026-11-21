package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How points follow the body spread along a surface: a row of points, half following the thigh and half the shin,
 * spread, changing from one to the other gradually, not at once; each still following its parts wholly.
 */
class SpreadTest {

    @Test
    void aSharpChangeBetweenNeighboursIsSpreadAlongTheRow() {
        final int n = 20;
        final Weights[] w = new Weights[n];
        final int[][] next = new int[n][];
        for (int i = 0; i < n; i++) {
            final float[] by = new float[Part.values().length];
            by[(i < n / 2 ? Part.RIGHT_THIGH : Part.RIGHT_SHIN).ordinal()] = 1;
            w[i] = new Weights(by);
            next[i] = i == 0 ? new int[] {1} : i == n - 1 ? new int[] {n - 2} : new int[] {i - 1, i + 1};
        }
        final Weights[] spread = Spread.INSTANCE.over(w, next, 20);
        for (int i = 0; i + 1 < n; i++) {
            final double jump = Math.abs(thigh(spread[i]) - thigh(spread[i + 1]));
            assertTrue(jump < 0.25, "between " + i + " and " + (i + 1) + ": " + jump);
            double sum = 0;
            for (final float f : spread[i].by()) {
                sum += f;
            }
            assertEquals(1, sum, 1e-5);
        }
        assertTrue(thigh(spread[0]) > 0.9 && thigh(spread[n - 1]) < 0.1, "the ends still their own");
    }

    private double thigh(final Weights w) {
        return w.by()[Part.RIGHT_THIGH.ordinal()];
    }
}
