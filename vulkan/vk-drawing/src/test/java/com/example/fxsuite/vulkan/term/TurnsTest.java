package com.example.fxsuite.vulkan.term;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Stripes that come round again, as fine as they are looked at: stripe or no stripe where a pixel covers hardly any
 * of a turn; the stripe's true share of the pixel where it covers an edge; and the stripes' average, exactly, where
 * it covers whole turns.
 */
class TurnsTest {

    private static final double SHARE = 0.33;

    @Test
    void whereAPixelCoversHardlyAnyOfATurnItIsStripeOrNoStripe() {
        assertEquals(1, pulse(0.1, 0), 1e-9, "in the first third of a turn");
        assertEquals(0, pulse(0.6, 0), 1e-9, "past it");
        assertEquals(1, pulse(4.2, 0), 1e-9, "in the fifth turn as in the first");
        assertEquals(1, pulse(-3.9, 0), 1e-9, "and counted backwards from nothing: a tenth into a turn");
        assertEquals(0, pulse(-3.5, 0), 1e-9);
    }

    @Test
    void whereAPixelLiesAcrossAStripesEdgeItIsTheShareOfThePixelTheStripeCovers() {
        assertEquals(0.5, pulse(SHARE, 0.1), 1e-9, "half on the stripe, half off");
        assertEquals(0.75, pulse(SHARE - 0.025, 0.1), 1e-9, "three quarters on");
        assertEquals(0.5, pulse(1.0, 0.1), 1e-9, "and the same where the next stripe begins");
    }

    @Test
    void whereAPixelCoversWholeTurnsItIsTheStripesAverageWhereverItFalls() {
        for (final double counted : new double[] {0, 0.17, 0.5, 3.93, -2.4}) {
            assertEquals(SHARE, pulse(counted, 1), 1e-9, "one whole turn, from " + counted);
            assertEquals(SHARE, pulse(counted, 7), 1e-9, "seven whole turns, from " + counted);
        }
    }

    @Test
    void asAPixelCoversMoreItComesEverNearerTheAverageAndStaysWithinWhatAStripeCanAdd() {
        for (final double covered : new double[] {2.5, 10.5, 100.5}) {
            assertEquals(SHARE, pulse(0.3, covered), SHARE * (1 - SHARE) / covered + 1e-9, "covering " + covered);
        }
    }

    /** The pulse at {@code counted} turns, a pixel covering {@code covered} turns: a turn to a centimetre. */
    private double pulse(final double counted, final double covered) {
        final Real pulse = new Turns(new Real(counted), new Real(1)).pulse(SHARE, new Real(covered));
        return pulse.node().worked(new Sample())[0];
    }
}
