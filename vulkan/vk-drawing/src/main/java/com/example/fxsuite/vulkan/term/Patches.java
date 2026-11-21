package com.example.fxsuite.vulkan.term;

/**
 * A surface divided into patches of no two shapes alike, as stones fitted together are, or dried mud cracked. A grid
 * of squares of {@code size} centimetres is laid over the surface, a middle is scattered somewhere within each
 * square, and each point belongs to the patch whose middle is nearest. {@code across} and {@code up} are where the
 * point is on the surface; {@code use} and the number after it tell these middles from any other scatter.
 */
public record Patches(Real across, Real up, double size, int use) {

    /** Further than any middle is. */
    private static final double FAR = 1e9;
    /** A middle keeps this far from its square's edges, in squares, so no patch is a sliver. */
    private static final double INSET = 0.15;

    /** The nearest middle and the next nearest, how far off each is; and which square the nearest is in. */
    private record Nearest(Real first, Real second, Real column, Real row) {
    }

    /**
     * How far inside its patch the point is, in centimetres: nothing on the line between two patches, more toward
     * the patch's middle. Half of how much nearer the patch's own middle is than the next: a little less than the
     * true distance, and less by more away from the line between the two middles, so a joint cut by it is wider in
     * some places than others, as joints between stones are.
     */
    public Real inside() {
        final Nearest nearest = nearest();
        return nearest.second().less(nearest.first()).times(0.5);
    }

    /** A number of its own for each patch, told from other scatters by {@code of}. */
    public Scatter scatter(final int of) {
        final Nearest nearest = nearest();
        return new Scatter(nearest.column(), nearest.row(), of, size);
    }

    /** The nearest of the nine middles round the point: its own square's, and the eight squares' about it. */
    private Nearest nearest() {
        final Real column = across.over(size).floored();
        final Real row = up.over(size).floored();
        Nearest nearest = new Nearest(new Real(FAR), new Real(FAR), column, row);
        for (int beside = -1; beside <= 1; beside++) {
            for (int above = -1; above <= 1; above++) {
                nearest = nearer(nearest, column.plus(new Real(beside)), row.plus(new Real(above)));
            }
        }
        return nearest;
    }

    /** {@code so}, or the middle of the square at {@code column}, {@code row} where that is nearer. */
    private Nearest nearer(final Nearest so, final Real column, final Real row) {
        final Real middleAcross = column.plus(new Real(INSET))
                .plus(new Scatter(column, row, use, size).number().times(1 - 2 * INSET)).times(size);
        final Real middleUp = row.plus(new Real(INSET))
                .plus(new Scatter(column, row, use + 1, size).number().times(1 - 2 * INSET)).times(size);
        final Real offAcross = across.less(middleAcross);
        final Real offUp = up.less(middleUp);
        final Real away = offAcross.times(offAcross).plus(offUp.times(offUp)).root();
        // 1 where this middle is nearer than the nearest so far
        final Real closer = so.first().past(away);
        return new Nearest(so.first().least(away), so.second().least(so.first().most(away)),
                so.column().mixed(column, closer), so.row().mixed(row, closer));
    }
}
