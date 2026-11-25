package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A head made a man's size region by region: its eye stays where it was up and ahead; the vault made higher by its
 * own scale and the face by its own, bending between without a step; the face below the cheekbones as broad as they
 * are made, the vault as it is long; and every point taken back to where it was.
 */
class HeadWarpTest {

    private final V3 eye = new V3(3, 3, 10);
    private final HeadWarp w = new HeadWarp(eye, new HeadWarp.Levels(1.5, -1),
            new HeadWarp.Scales(0.8, 0.9, 1.02, 1.05));

    @Test
    void theEyeStaysWhereItWasUpAndAhead() {
        final V3 e = w.apply(eye);
        assertEquals(eye.y(), e.y(), 1e-9);
        assertEquals(eye.z(), e.z(), 1e-9);
    }

    @Test
    void theVaultAndTheFaceAreEachMadeHigherByTheirOwnScaleWithoutAStep() {
        final double vault = w.apply(new V3(0, 13, 0)).y() - w.apply(new V3(0, 8, 0)).y();
        final double face = w.apply(new V3(0, -2, 0)).y() - w.apply(new V3(0, -7, 0)).y();
        assertEquals(5 * 0.8, vault, 1e-9);
        assertEquals(5 * 0.9, face, 1e-9);
        double last = w.apply(new V3(0, -3, 0)).y();
        for (double y = -2.99; y < 9; y += 0.01) {
            final double now = w.apply(new V3(0, y, 0)).y();
            final double slope = (now - last) / 0.01;
            assertTrue(slope >= 0.8 - 1e-6 && slope <= 0.9 + 1e-6, "rising no faster or slower than either: " + slope);
            last = now;
        }
    }

    @Test
    void theFaceIsAsBroadAsTheCheekbonesAndTheVaultAsItIsLong() {
        final double jaw = w.apply(new V3(5, eye.y() - 12, eye.z() - 9)).x() / 5;
        final double chin = w.apply(new V3(1, eye.y() - 12, eye.z())).x() / 1;
        final double vault = w.apply(new V3(6, eye.y() + 8, eye.z() - 9)).x() / 6;
        assertEquals(1.05, jaw, 1e-9);
        assertEquals(1.05, chin, 1e-9);
        assertEquals(1.02, vault, 1e-9);
    }

    @Test
    void everyPointIsTakenBackToWhereItWas() {
        for (double x = -9; x <= 9; x += 3) {
            for (double y = -12; y <= 16; y += 0.7) {
                for (double z = -10; z <= 14; z += 4) {
                    final V3 p = new V3(x, y, z);
                    final V3 q = w.back(w.apply(p));
                    assertEquals(0, q.minus(p).length(), 1e-9, p.toString());
                }
            }
        }
    }
}
