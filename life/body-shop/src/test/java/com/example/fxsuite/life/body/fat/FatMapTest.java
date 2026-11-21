package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Weights;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The fat as thick as it was measured part by part: the buttocks the thickest, thicker than the back above them and the
 * belly before; the hands the thinnest; a body twice as fat on average twice as thick everywhere.
 */
class FatMapTest {

    private final Rig bind = new Rig(Proportions.PAPERS, BoundPose.INSTANCE.pose(), false);

    @Test
    void theButtocksAreThickestTheHandsThinnest() {
        final double buttock = at(Part.PELVIS, new V3(6, -2, -10));
        final double back = at(Part.CHEST, new V3(6, 5, -10));
        final double belly = at(Part.PELVIS, new V3(6, 6, 10));
        final double hand = at(Part.RIGHT_HAND, new V3(0, -6, 0));
        assertEquals(1.072, buttock, 0.01, "the buttocks, cm");
        assertEquals(0.317, back, 0.01, "the back");
        assertEquals(0.399, belly, 0.01, "the belly");
        assertEquals(0.029, hand, 0.001, "the hand");
        assertTrue(buttock > belly && belly > back && back > hand);
    }

    @Test
    void aBodyTwiceAsFatIsTwiceAsThickEverywhere() {
        for (final Part p : new Part[] {Part.PELVIS, Part.RIGHT_THIGH, Part.LEFT_FOREARM, Part.HEAD}) {
            final V3 x = bind.frame(p).place(new V3(2, -3, 4));
            final Weights w = alone(p);
            assertEquals(2 * FatMap.AS_MEASURED.thickness(w, x, bind),
                    new FatMap(2 * FatMap.MEASURED).thickness(w, x, bind), 1e-9, p.said());
        }
    }

    /** How thick, cm, at {@code local} in part {@code p}'s own frame, following it alone. */
    private double at(final Part p, final V3 local) {
        return FatMap.AS_MEASURED.thickness(alone(p), bind.frame(p).place(local), bind);
    }

    private Weights alone(final Part p) {
        final float[] w = new float[Part.values().length];
        w[p.ordinal()] = 1;
        return new Weights(w);
    }
}
