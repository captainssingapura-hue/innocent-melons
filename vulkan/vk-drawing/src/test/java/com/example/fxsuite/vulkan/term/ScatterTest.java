package com.example.fxsuite.vulkan.term;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A number scattered over cells: one for each cell and another for its neighbour, the same every time; spread evenly
 * from nothing to one; settling to the middle as a pixel comes to cover several cells; and the few of them the share
 * they are said to be.
 */
class ScatterTest {

    @Test
    void aCellHasOneNumberEveryTimeAndItsNeighbourAnother() {
        assertEquals(number(3, 7, 1), number(3, 7, 1));
        assertNotEquals(number(3, 7, 1), number(4, 7, 1), "the next cell across");
        assertNotEquals(number(3, 7, 1), number(3, 8, 1), "the next cell up");
        assertNotEquals(number(3, 7, 1), number(3, 7, 2), "the same cell, for another use");
        assertNotEquals(number(-3, 7, 1), number(3, 7, 1), "a cell counted backwards from nothing");
    }

    @Test
    void overManyCellsTheNumbersAreSpreadEvenlyFromNothingToOne() {
        final int[] tenths = new int[10];
        double sum = 0;
        for (int across = -50; across < 50; across++) {
            for (int up = -50; up < 50; up++) {
                final double number = number(across, up, 1);
                assertTrue(number >= 0 && number < 1, "from nothing up to, not reaching, one: " + number);
                tenths[(int) (number * 10)]++;
                sum += number;
            }
        }
        assertEquals(0.5, sum / 10_000, 0.01, "about the middle between them");
        for (final int tenth : tenths) {
            assertEquals(1000, tenth, 120, "about as many in each tenth");
        }
    }

    @Test
    void asAPixelCoversSeveralCellsACellsNumberSettlesToTheMiddle() {
        final double own = number(3, 7, 1);
        assertEquals(own, settling(0.2), 1e-12, "a pixel well within a cell of 2 cm: the cell's own");
        assertEquals((own + 0.5) / 2, settling(2.5), 1e-12, "covering a cell and a quarter: half way to the middle");
        assertEquals(0.5, settling(4), 1e-12, "covering two cells: the middle");
        assertEquals(0.5, settling(400), 1e-12);
    }

    @Test
    void theFewAreTheShareOfTheCellsTheyAreSaidToBeAndSettleToThatShare() {
        int few = 0;
        for (int across = 0; across < 100; across++) {
            for (int up = 0; up < 100; up++) {
                few += (int) scatter(across, up, 1).some(0.12, new Real(0)).node().worked(new Sample())[0];
            }
        }
        assertEquals(1200, few, 100, "about twelve in a hundred");
        assertEquals(0.12, scatter(3, 7, 1).some(0.12, new Real(400)).node().worked(new Sample())[0], 1e-12);
    }

    @Test
    void theShaderIsGivenAFunctionThatWorksItAsJavaDoes() {
        final Writing writing = new Writing();
        final String name = writing.named(scatter(3, 7, 1).number().node());
        assertEquals("float t0 = scattered(3.0, 7.0, 1.0);", writing.lines().getFirst());
        assertEquals("t0", name);
        assertEquals("float scattered(float across, float up, float use) {", writing.declared().getFirst());
        assertTrue(writing.declared().contains("    return float(mixed >> 8) / 16777216.0;"));
        writing.named(scatter(4, 7, 1).number().node());
        assertEquals(9, writing.declared().size(), "declared once, however often it is used");
    }

    private double number(final int across, final int up, final int use) {
        return scatter(across, up, use).number().node().worked(new Sample())[0];
    }

    private double settling(final double covers) {
        return scatter(3, 7, 1).settling(new Real(covers)).node().worked(new Sample())[0];
    }

    /** A scatter over cells of 2 cm, at the cell {@code across}, {@code up}. */
    private Scatter scatter(final int across, final int up, final int use) {
        return new Scatter(new Real(across), new Real(up), use, 2);
    }
}
