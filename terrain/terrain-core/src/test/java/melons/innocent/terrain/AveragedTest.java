package melons.innocent.terrain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A ground averaged over cells: an even slope as it is; a groove half a cell wide averaged in, not lost, as deep as
 * its share of the weights; a grid asked for all at once as high at each point as asked alone; nothing averaged over
 * cells of no size.
 */
class AveragedTest {

    private static final Extent EXTENT = new Extent(-10_000, -10_000, 10_000, 10_000);

    private final Ground slope = new Ground() {
        public Extent extent() {
            return EXTENT;
        }

        public double height(final double x, final double z) {
            return 0.2 * x - 0.1 * z + 50;
        }
    };

    /** A groove along x = 0, 4 m deep and 2 m wide. */
    private final Ground groove = new Ground() {
        public Extent extent() {
            return EXTENT;
        }

        public double height(final double x, final double z) {
            return Math.abs(x) < 100 ? -400 : Math.sin(x / 70) * 30 + Math.cos(z / 90) * 20;
        }
    };

    @Test
    void anEvenSlopeAsItIsAGrooveAveragedIn() {
        assertEquals(slope.height(300, 700), new Averaged(slope, 800).height(300, 700), 1e-9);
        // a cell 4 m across: the groove under the middle column of three, half the weight each way, so -400 by half
        final double[] off = {-1, 0, 1};
        double rest = 0;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                if (a != 1) {
                    rest += (a == 1 ? 0.5 : 0.25) * (b == 1 ? 0.5 : 0.25) * groove.height(off[a] * 200, off[b] * 200);
                }
            }
        }
        assertEquals(-200 + rest, new Averaged(groove, 400).height(0, 0), 1e-9);
        assertEquals(-400, new Averaged(groove, 0).height(0, 0), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> new Averaged(groove, -1));
    }

    @Test
    void aGridAllAtOnceAsHighAtEachPointAsAskedAlone() {
        final Averaged averaged = new Averaged(groove, 400);
        final double[] grid = averaged.heights(-1_000, -600, 400, 6, 4);
        final double[] alone = new double[grid.length];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 6; i++) {
                alone[j * 6 + i] = averaged.height(-1_000 + i * 400, -600 + j * 400);
            }
        }
        assertArrayEquals(alone, grid, 1e-9);
    }
}
