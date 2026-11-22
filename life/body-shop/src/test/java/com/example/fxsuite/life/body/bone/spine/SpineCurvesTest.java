package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The spine curved as men's are measured: its lumbar lordosis and thoracic kyphosis young men's, its neck all but
 * straight; the atlas where it was drawn, under the skull, and the fifth lumbar where it was, on the sacrum.
 */
class SpineCurvesTest {

    @Test
    void theCurvesAreMensWithItsEndsWhereTheyWere() {
        final V3[] m = SpineForm.INSTANCE.middles();
        final double[] f = SpineCurves.INSTANCE.measure(m);
        assertEquals(49.6, f[0], 0.1, "lordosis, L1 to L5");
        assertEquals(36.1, f[1], 0.1, "kyphosis, T1 to T12");
        assertEquals(-0.6, f[2], 0.1, "the neck, C2 to C7");
        assertEquals(0, Level.C1.middle().minus(Level.C1.drawn()).length(), 0.01, "the atlas");
        assertEquals(0, Level.L5.middle().minus(Level.L5.drawn()).length(), 1e-9, "the fifth lumbar");
    }
}
