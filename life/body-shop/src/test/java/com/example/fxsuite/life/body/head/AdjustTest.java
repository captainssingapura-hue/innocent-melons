package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A piece adjusted about its root: left as it was by a plain adjustment; a pair's left piece adjusted as its right
 * one is, seen in a mirror, moved out and turned out each to its own side.
 */
class AdjustTest {

    private final V3 same = new V3(1, 1, 1);

    @Test
    void aPlainAdjustmentLeavesThePieceAsItWas() {
        final Field.Shape ball = ball(new V3(5, 0, 0), 1);
        assertSame(ball, new Adjust(V3.ZERO, 0, 0, 1, same, 0).on(ball, new V3(5, 0, 0), false));
    }

    @Test
    void movedOutEachPieceGoesOutToItsOwnSide() {
        final Adjust out = new Adjust(new V3(1, 0, 0), 0, 0, 1, same, 0);
        assertEquals(-1, out.on(ball(new V3(5, 0, 0), 1), new V3(5, 0, 0), false).at(6, 0, 0), 1e-9, "the right");
        assertEquals(-1, out.on(ball(new V3(-5, 0, 0), 1), new V3(-5, 0, 0), true).at(-6, 0, 0), 1e-9, "the left");
    }

    @Test
    void turnedOutEachPieceBackGoesOutToItsOwnSide() {
        final Adjust turned = new Adjust(V3.ZERO, 0, 90, 1, same, 0);
        assertTrue(turned.on(ear(5), new V3(5, 0, 0), false).at(8.5, 0, 0) < 0, "the right ear's back to the right");
        assertTrue(turned.on(ear(-5), new V3(-5, 0, 0), true).at(-8.5, 0, 0) < 0, "the left ear's back to the left");
    }

    @Test
    void tiltedUpItsTipRises() {
        final Field.Shape nose = Field.Ellipsoid.of(new V3(0, 0, 2), new V3(0.5, 0, 0), new V3(0, 0.5, 0),
                new V3(0, 0, 2));
        final Field.Shape up = new Adjust(V3.ZERO, 30, 0, 1, same, 0).on(nose, V3.ZERO, false);
        assertTrue(up.at(0, 1.5, 2.6) < 0, "its tip up, where it was not");
        assertTrue(nose.at(0, 1.5, 2.6) > 0, "as it was, not there");
    }

    /** A flat piece reaching back four centimetres from its root at {@code x}. */
    private Field.Shape ear(final double x) {
        return Field.Ellipsoid.of(new V3(x, 0, -2), new V3(0.3, 0, 0), new V3(0, 1, 0), new V3(0, 0, 2));
    }

    private Field.Shape ball(final V3 c, final double r) {
        return Field.Ellipsoid.of(c, new V3(r, 0, 0), new V3(0, r, 0), new V3(0, 0, r));
    }
}
