package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A hand closed round a grip: the palm and the wrist as they were; each fingertip brought round out of the palm and
 * back up toward the wrist, as far from its knuckle as before or nearer, its finger bent and not stretched. Closed
 * round the bar: each finger's knuckle, its joints and its tip near as far from the bar's line, a ring round it, and
 * that as wide as a hand round a motorcycle's grip; the thumb's tip over it.
 */
class GraspTest {

    private final Grasp grasp = Grasp.INSTANCE;

    @Test
    void thePalmStaysEachFingertipComesRoundOutOfThePalmItsFingerNoLonger() {
        final V3 palm = new V3(0, -6, 0.5);
        assertEquals(0, held(palm).minus(palm).length(), 1e-9, "the palm as it was");
        for (final Digit d : new Digit[] {Digit.INDEX, Digit.MIDDLE, Digit.RING, Digit.LITTLE}) {
            final V3 tip = tip(d);
            final V3 closed = held(tip);
            assertTrue(closed.z() > tip.z(), d.said() + " out of the palm: " + closed);
            assertTrue(closed.y() > tip.y() + 5, d.said() + " back up toward the wrist: " + closed);
            assertTrue(closed.minus(d.head()).length() < tip.minus(d.head()).length() + 0.5, d.said() + " no longer");
        }
    }

    @Test
    void itsFingersRingTheBarAsWideAsAHandRoundAGripTheThumbOverIt() {
        final V3[] axis = grasp.axis();
        final V3 along = axis[0].minus(axis[1]).unit();
        for (final Digit d : new Digit[] {Digit.INDEX, Digit.MIDDLE, Digit.RING, Digit.LITTLE}) {
            final double knuckle = off(d.head(), axis[1], along);
            assertTrue(knuckle > 2 && knuckle < 4, d.said() + "'s ring as wide as round a grip: " + knuckle);
            for (int q = 1; q < d.phalanges(); q++) {
                assertEquals(knuckle, off(held(Fingers.INSTANCE.interphalangeal(d, q)), axis[1], along), 0.8,
                        d.said() + "'s joint " + q + " on the ring");
            }
            assertEquals(knuckle, off(held(tip(d)), axis[1], along), 1.6, d.said() + "'s tip near the ring");
        }
        assertTrue(held(tip(Digit.THUMB)).z() > tip(Digit.THUMB).z(), "the thumb over it");
    }

    /** {@code p} closed. */
    private V3 held(final V3 p) {
        final float[] q = grasp.held(new float[] {(float) p.x(), (float) p.y(), (float) p.z()});
        return new V3(q[0], q[1], q[2]);
    }

    /** {@code d}'s tip, as it hangs. */
    private V3 tip(final Digit d) {
        final V3[] last = Fingers.INSTANCE.way(d, d.phalanges() - 1);
        return last[0].plus(last[1].times(d.length(d.phalanges() - 1)));
    }

    /** How far {@code p} is from the line through {@code at} along {@code along}. */
    private double off(final V3 p, final V3 at, final V3 along) {
        final V3 d = p.minus(at);
        return d.minus(along.times(d.dot(along))).length();
    }
}
