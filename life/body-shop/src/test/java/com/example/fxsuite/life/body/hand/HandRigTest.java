package com.example.fxsuite.life.body.hand;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.hand.Digit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A hand rig. At rest, nothing moved. However it is held, each finger whole: each segment as long as it was, each
 * turning where the one before it ends. Closed round a grip: each fingertip brought round and back up
 * toward the wrist; each finger's knuckle and next two joints on a ring as wide as a hand round a motorcycle's grip,
 * its tip near it. No joint turned further than it may.
 */
class HandRigTest {

    private final HandPose rest = new HandPose();
    private final HandRig closed = new HandRig(rest.gripping());

    @Test
    void atRestNothingIsMovedHoweverItIsHeldEachFingerIsWhole() {
        final HandRig still = new HandRig(rest);
        for (final Segment s : Segment.values()) {
            assertEquals(0, still.place(s, s.end()).minus(s.end()).length(), 1e-9, s + " unmoved at rest");
            final V3 joint = closed.place(s, s.joint());
            assertEquals(s.end().minus(s.joint()).length(), closed.place(s, s.end()).minus(joint).length(), 1e-9,
                    s + " as long as it was");
            if (s.parent() != null) {
                assertEquals(0, closed.place(s.parent(), s.joint()).minus(joint).length(), 1e-9,
                        s + " turning where " + s.parent() + " ends");
            }
        }
    }

    @Test
    void closedRoundAGripEachFingerRingsTheBarItsTipComeRound() {
        for (final Segment s : new Segment[] {Segment.INDEX_DISTAL, Segment.MIDDLE_DISTAL, Segment.RING_DISTAL,
            Segment.LITTLE_DISTAL}) {
            final V3 tip = closed.place(s, s.end());
            assertTrue(tip.y() > s.end().y() + 5, s + " come round back toward the wrist: " + tip);
            final V3 ring = closed.ring(s.digit());
            final double wide = ring.minus(s.digit().head()).length();
            assertTrue(wide > 1.8 && wide < 4, s.digit().said() + "'s ring as wide as round a grip: " + wide);
            assertEquals(wide, ring.minus(tip).length(), 1.6, s.digit().said() + "'s tip near the ring");
        }
        final V3 thumb = closed.place(Segment.THUMB_DISTAL, Segment.THUMB_DISTAL.end());
        assertTrue(thumb.z() > Segment.THUMB_DISTAL.end().z(), "the thumb in over it: " + thumb);
        assertEquals(0, closed.ring(Digit.MIDDLE).minus(closed.ring(Digit.MIDDLE)).length(), 1e-12);
    }

    @Test
    void noJointTurnsFurtherThanItMay() {
        final HandPose bent = rest.with(Segment.INDEX_MIDDLE, FingerDof.MIDDLE_BEND, 500)
                .with(Segment.THUMB_METACARPAL, FingerDof.THUMB_SWING, -500);
        assertEquals(FingerDof.MIDDLE_BEND.max(), bent.get(Segment.INDEX_MIDDLE, FingerDof.MIDDLE_BEND));
        assertEquals(FingerDof.THUMB_SWING.min(), bent.get(Segment.THUMB_METACARPAL, FingerDof.THUMB_SWING));
    }
}
