package com.example.fxsuite.life.body;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A shape reshaped about its anchor: moved, stretched, turned and swollen, its surface where that puts it. */
class ReshapedTest {

    private final Field.Shape ball = Field.Ellipsoid.of(V3.ZERO, new V3(2, 0, 0), new V3(0, 2, 0), new V3(0, 0, 2));
    private final V3 same = new V3(1, 1, 1);

    @Test
    void movedItsSurfaceIsMovedWithIt() {
        final Reshaped r = new Reshaped(ball, V3.ZERO, new V3(0, 0, 3), Rot.NONE, same, 0);
        assertEquals(0, r.at(0, 0, 5), 1e-9, "its front three centimetres further ahead");
        assertEquals(-2, r.at(0, 0, 3), 1e-9, "its middle where it was moved to");
    }

    @Test
    void madeBiggerAboutItsAnchorItReachesAsManyTimesFurther() {
        final Reshaped r = new Reshaped(ball, new V3(-2, 0, 0), V3.ZERO, Rot.NONE, new V3(2, 2, 2), 0);
        assertEquals(0, r.at(6, 0, 0), 1e-9, "its far side twice as far from its anchor, its near side where it was");
        assertEquals(0, r.at(-2, 0, 0), 1e-9, "its side at its anchor not moved");
    }

    @Test
    void turnedItsLongWayGoesWithTheTurn() {
        final Field.Shape rod = Field.Ellipsoid.of(V3.ZERO, new V3(1, 0, 0), new V3(0, 1, 0), new V3(0, 0, 3));
        final Reshaped r = new Reshaped(rod, V3.ZERO, V3.ZERO, Rot.aboutY(90), same, 0);
        assertEquals(0, r.at(3, 0, 0), 1e-9, "its end, once ahead, now to the right");
        assertTrue(r.at(0, 0, 2.5) > 0, "nothing of it ahead any more");
    }

    @Test
    void swollenItsSurfaceIsFurtherOutAllRound() {
        final Reshaped r = new Reshaped(ball, V3.ZERO, V3.ZERO, Rot.NONE, same, 0.5);
        assertEquals(0, r.at(0, 2.5, 0), 1e-9, "half a centimetre further out");
    }
}
