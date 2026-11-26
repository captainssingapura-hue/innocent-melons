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
 * belly before; the hands the thinnest; the belly below the navel as much thicker than above it as the judokas' was,
 * the trunk's front on average as measured; the thigh's outer side as much thicker than its front; the leanest man's
 * thinner everywhere; a body twice as fat on average twice as thick everywhere.
 */
class FatMapTest {

    private final Rig bind = new Rig(Proportions.PAPERS, BoundPose.INSTANCE.pose(), false);

    @Test
    void theButtocksAreThickestTheHandsThinnest() {
        final double buttock = at(FatMap.AS_MEASURED, Part.PELVIS, new V3(6, -2, -10));
        final double back = at(FatMap.AS_MEASURED, Part.CHEST, new V3(6, 5, -10));
        final double belly = at(FatMap.AS_MEASURED, Part.PELVIS, new V3(6, 6, 10));
        final double hand = at(FatMap.AS_MEASURED, Part.RIGHT_HAND, new V3(0, -6, 0));
        assertEquals(1.072, buttock, 0.01, "the buttocks, cm");
        assertEquals(0.317, back, 0.01, "the back");
        assertEquals(0.029, hand, 0.001, "the hand");
        assertTrue(buttock > belly && belly > back && back > hand);
    }

    @Test
    void theBellyBelowTheNavelAndTheThighsOuterSideAreThickerAsMeasured() {
        final double below = at(FatMap.AS_MEASURED, Part.PELVIS, new V3(6, 6, 10));
        final double above = at(FatMap.AS_MEASURED, Part.ABDOMEN, new V3(6, 6, 10));
        assertEquals(5.1 / 2.2, below / above, 1e-6, "as the judokas' sites below and above the navel");
        assertEquals(0.399, (2 * above + below) / 3, 1e-3, "the trunk's front on average as the ten had it");
        final double side = at(FatMap.AS_MEASURED, Part.RIGHT_THIGH, new V3(8, -20, 0));
        final double inner = at(FatMap.AS_MEASURED, Part.RIGHT_THIGH, new V3(-8, -20, 0));
        assertEquals(4.2 / 2.5, side / inner, 1e-6, "as the judokas' outer and front thigh, the inner as the front");
    }

    @Test
    void theLeanestMansFatIsThinnerEverywhere() {
        final FatMap lean = new FatMap(FatPattern.LEAN, FatPattern.LEAN.mean(), 0);
        for (final Part p : Part.values()) {
            final V3 local = new V3(2, -3, 4);
            assertTrue(at(lean, p, local) < at(FatMap.AS_MEASURED, p, local), p.said());
        }
        assertEquals(0.649, at(lean, Part.PELVIS, new V3(6, -2, -10)), 0.01, "his buttocks, cm");
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

    /** How thick {@code map} is, cm, at {@code local} in part {@code p}'s own frame, following it alone. */
    private double at(final FatMap map, final Part p, final V3 local) {
        return map.thickness(alone(p), bind.frame(p).place(local), bind);
    }

    private Weights alone(final Part p) {
        final float[] w = new float[Part.values().length];
        w[p.ordinal()] = 1;
        return new Weights(w);
    }
}
