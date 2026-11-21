package com.example.fxsuite.vulkan.term;

import java.util.List;

/**
 * A number scattered evenly from nothing to one, one for each cell of something made of cells: each brick of a wall,
 * each speck of a brick. {@code across} and {@code up} say which cell a point is in, as whole numbers; {@code use}
 * tells this scatter from another over the same cells; {@code size} is how big a cell is, in centimetres, across its
 * smaller way.
 */
public record Scatter(Real across, Real up, int use, double size) {

    /** What many cells' numbers come to between them: the middle. */
    private static final double MIDDLE = 0.5;

    public Scatter {
        if (!(size > 0)) {
            throw new IllegalArgumentException("a cell has some size: " + size);
        }
    }

    /** The cell's number, whatever a pixel covers: from 0 up to, not reaching, 1. */
    public Real number() {
        return new Real(new Applied(Chance.SCATTERED,
                List.of(across.node(), up.node(), new Constant(new double[] {use}))));
    }

    /**
     * The cell's number as a pixel covering {@code covers} centimetres shows it: its own where the pixel is well
     * within the cell; settling toward the middle as the pixel comes to cover several cells, whose numbers, evenly
     * scattered, come to the middle between them. So what is scattered fades far off, and never sparkles.
     */
    public Real settling(final Real covers) {
        return number().mixed(new Real(MIDDLE), new Blurred(covers, size).share());
    }

    /**
     * Whether the cell is one of the few: 1 in the cells whose numbers fall in the top {@code share} of them all, 0
     * in the rest; and, as a pixel comes to cover several cells, settling to {@code share} itself, which is how much
     * of what the pixel covers the few make up.
     */
    public Real some(final double share, final Real covers) {
        return number().past(new Real(1 - share)).mixed(new Real(share), new Blurred(covers, size).share());
    }
}
