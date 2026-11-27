package com.example.fxsuite.life.body.vulkan.lineup;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the line-up has on show: never nobody, the picked one one of them, none shorter or taller than may be. Each as
 * tall as the model to begin with, drawn as large as made; one made taller drawn larger, the others as they were. The
 * line's middle midway between its ends, a taller figure given more room from its neighbours.
 */
class LineupShownTest {

    private static final double CLOSE = 1e-9;

    @Test
    void itRefusesNobodyAPickOfNoneAndAFigureTooShortOrTooTall() {
        assertThrows(IllegalArgumentException.class, () -> new LineupShown(List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> new LineupShown(List.of(188.0), 1));
        assertThrows(IllegalArgumentException.class, () -> new LineupShown(List.of(188.0, 100.0), 0));
        assertThrows(IllegalArgumentException.class, () -> new LineupShown(2).standing(260));
    }

    @Test
    void eachStandsAsTallAsTheModelAtFirstAndOneMadeTallerIsDrawnLargerTheRestAsTheyWere() {
        final LineupShown first = new LineupShown(3);
        assertEquals(List.of(188.0, 188.0, 188.0), first.tall());
        assertEquals(1, first.scale(2), CLOSE);
        final LineupShown taller = first.picking(1).standing(235);
        assertEquals(1.25, taller.scale(1), CLOSE);
        assertEquals(1, taller.scale(0), CLOSE);
        assertEquals(1, taller.picked());
    }

    @Test
    void theLinesMiddleIsMidwayAndATallerFigureHasMoreRoom() {
        final LineupShown even = new LineupShown(3);
        assertEquals(0, even.along(1), CLOSE);
        assertEquals(-even.along(0), even.along(2), CLOSE);
        final LineupShown taller = even.picking(2).standing(235);
        assertEquals(0, taller.along(0) + taller.along(2), CLOSE);
        assertTrue(taller.along(2) - taller.along(1) > taller.along(1) - taller.along(0),
                "the taller one stands further from its neighbour");
    }
}
