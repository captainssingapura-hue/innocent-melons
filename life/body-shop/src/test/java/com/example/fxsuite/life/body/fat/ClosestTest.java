package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * The point of a triangle nearest a point: straight under it, within the triangle, as much of each corner as it is;
 * on a side, between its two corners; at a corner, past it.
 */
class ClosestTest {

    private final V3 a = new V3(0, 0, 0);
    private final V3 b = new V3(3, 0, 0);
    private final V3 c = new V3(0, 3, 0);

    @Test
    void straightUnderWithinTheTriangle() {
        assertArrayEquals(new double[] {1.0 / 3, 1.0 / 3, 1.0 / 3}, Closest.INSTANCE.weights(a, b, c,
                new V3(1, 1, 5)), 1e-9);
    }

    @Test
    void onASideBetweenItsCorners() {
        assertArrayEquals(new double[] {0, 0.5, 0.5}, Closest.INSTANCE.weights(a, b, c, new V3(4, 4, 1)), 1e-9);
        assertArrayEquals(new double[] {2.0 / 3, 1.0 / 3, 0}, Closest.INSTANCE.weights(a, b, c, new V3(1, -2, 0)),
                1e-9);
    }

    @Test
    void atACornerPastIt() {
        assertArrayEquals(new double[] {1, 0, 0}, Closest.INSTANCE.weights(a, b, c, new V3(-1, -1, 2)), 1e-9);
        assertArrayEquals(new double[] {0, 0, 1}, Closest.INSTANCE.weights(a, b, c, new V3(-1, 5, 0)), 1e-9);
    }
}
