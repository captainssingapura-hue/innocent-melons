package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A band drawn tight round two legs standing apart: across the gap between them, not into it; on each leg where it
 * bulges; as thick as its knit out from them; following both legs as far as it lies between them.
 */
class HoopTest {

    private static final double EASE = 0.3;

    @Test
    void aBandRoundTwoLegsGoesAcrossTheGapBetweenThem() {
        final Beneath legs = Underneath.INSTANCE.with(box(5, Part.RIGHT_THIGH), box(-5, Part.LEFT_THIGH));
        final double[] angles = new double[16];
        for (int k = 0; k < angles.length; k++) {
            angles[k] = 2 * Math.PI * k / angles.length;
        }
        final Hoop.Ring ring = Hoop.INSTANCE.ring(legs, all(legs), new Hoop.Plane(V3.ZERO, V3.X, V3.Z), p -> true,
                angles, EASE);
        assertEquals(16, ring.points().size());
        // straight before the middle, where there is no leg: across the gap, level with the legs' fronts
        final V3 front = ring.points().get(4);
        assertEquals(0, front.x(), 1e-9);
        assertEquals(2 + EASE, front.z(), 1e-9);
        assertEquals(0.5, ring.weights().get(4).by()[Part.RIGHT_THIGH.ordinal()], 1e-6);
        assertEquals(0.5, ring.weights().get(4).by()[Part.LEFT_THIGH.ordinal()], 1e-6);
        // to the right, on the right leg's outside, as thick as the knit out from it, following it alone
        final V3 right = ring.points().get(0);
        assertEquals(7 + EASE, right.x(), 1e-9);
        assertEquals(1, ring.weights().get(0).by()[Part.RIGHT_THIGH.ordinal()], 1e-6);
        for (final V3 p : ring.points()) {
            assertTrue(Math.abs(p.x()) > 7 || Math.abs(p.z()) > 2, "outside the legs: " + p);
        }
    }

    /** A leg: a box 4 cm square, 20 tall, its middle {@code x} across, following {@code part}. */
    private Beneath box(final double x, final Part part) {
        final List<V3> corners = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            corners.add(new V3(x + ((i & 1) == 0 ? -2 : 2), (i & 2) == 0 ? -10 : 10, (i & 4) == 0 ? -2 : 2));
        }
        final int[] t = {0, 1, 3, 0, 3, 2, 4, 6, 7, 4, 7, 5, 0, 4, 5, 0, 5, 1, 2, 3, 7, 2, 7, 6, 0, 2, 6, 0, 6, 4, 1,
            5, 7, 1, 7, 3};
        final float[] at = new float[24];
        final Part[] main = new Part[8];
        final Weights[] w = new Weights[8];
        for (int i = 0; i < 8; i++) {
            at[3 * i] = (float) corners.get(i).x();
            at[3 * i + 1] = (float) corners.get(i).y();
            at[3 * i + 2] = (float) corners.get(i).z();
            main[i] = part;
            final float[] alone = new float[Part.values().length];
            alone[part.ordinal()] = 1;
            w[i] = new Weights(alone);
        }
        return new Beneath(at, t, main, w);
    }

    private int[] all(final Beneath b) {
        final int[] a = new int[b.triangles().length / 3];
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }
        return a;
    }
}
