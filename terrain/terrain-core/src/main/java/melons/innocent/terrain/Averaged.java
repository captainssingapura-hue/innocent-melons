package melons.innocent.terrain;

/**
 * A ground averaged over cells {@code cell} across: how high it is at a spot is the weighted mean of how high the
 * ground is, worked out as finely as it is, at nine points a half cell apart round the spot, the spot itself counting
 * four times, the four beside it twice each and the four corners once each (one, two, one each way). So drawn with
 * points a cell apart, nothing finer than a cell is told apart, and what is finer is averaged in, not lost: the land
 * as seen from so high that a cell is what the eye can tell apart. Averaging and slope go together, so how steeply it
 * slopes is the mean of how steeply the ground slopes round the spot, and relief seen by its shading is kept. Asked
 * for a grid a cell apart all at once, it works the ground out once on a grid twice as fine and shares each point of
 * that among the cells round it, so a grid costs about four times as many heights as it has points, not nine; each
 * height the same as asked alone. Its extent is the ground's.
 *
 * @param ground what is averaged
 * @param cell   how far across each cell is, in centimetres; nothing, and it is the ground itself
 */
public record Averaged(Ground ground, double cell) implements Ground {

    /** How much each of the three points each way counts: one, two, one, of four. */
    private static final double[] WEIGHTS = {0.25, 0.5, 0.25};

    public Averaged {
        if (!(cell >= 0) || !Double.isFinite(cell)) {
            throw new IllegalArgumentException("a ground averaged over cells of some size, not " + cell);
        }
    }

    @Override
    public Extent extent() {
        return ground.extent();
    }

    @Override
    public double height(final double x, final double z) {
        if (cell == 0) {
            return ground.height(x, z);
        }
        double sum = 0;
        for (int i = 0; i < WEIGHTS.length; i++) {
            for (int j = 0; j < WEIGHTS.length; j++) {
                sum += WEIGHTS[i] * WEIGHTS[j] * ground.height(x + (i - 1) * cell / 2, z + (j - 1) * cell / 2);
            }
        }
        return sum;
    }

    /** A grid a cell apart worked out from the ground on a grid twice as fine, each of its points shared. */
    @Override
    public double[] heights(final double x0, final double z0, final double spacing, final int across,
                            final int along) {
        if (cell == 0 || spacing != cell) {
            return Ground.super.heights(x0, z0, spacing, across, along);
        }
        final int fineAcross = 2 * across + 1;
        final double[] fine = ground.heights(x0 - cell / 2, z0 - cell / 2, cell / 2, fineAcross, 2 * along + 1);
        final double[] heights = new double[across * along];
        for (int j = 0; j < along; j++) {
            for (int i = 0; i < across; i++) {
                double sum = 0;
                for (int a = 0; a < WEIGHTS.length; a++) {
                    for (int b = 0; b < WEIGHTS.length; b++) {
                        sum += WEIGHTS[a] * WEIGHTS[b] * fine[(2 * j + b) * fineAcross + 2 * i + a];
                    }
                }
                heights[j * across + i] = sum;
            }
        }
        return heights;
    }
}
