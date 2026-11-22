package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The skull's landmarks where they are to be: every measure within half a spread of men's mean; those held where they
 * are; those down the middle kept there; the skull bent through them, each going there, the held ones staying.
 */
class SkullLandmarksTest {

    private final Map<Landmark, V3> to = SkullLandmarks.INSTANCE.targets();

    @Test
    void everyMeasureIsAsMensAre() {
        for (final SkullLandmarks.Measure m : SkullLandmarks.INSTANCE.measures()) {
            final double d = side(to.get(m.a()), m.aLeft()).minus(side(to.get(m.b()), m.bLeft())).length();
            assertTrue(Math.abs(d - m.mean()) < 0.5 * m.sd(), m.said() + ": " + d + " against " + m.mean());
        }
    }

    @Test
    void theHeldStayAndTheMiddleStaysInTheMiddle() {
        for (final Landmark l : Landmark.values()) {
            if (l.held()) {
                assertEquals(0, to.get(l).minus(l.drawn()).length(), 1e-12, l.toString());
            }
            if (l.middle()) {
                assertEquals(0, to.get(l).x(), 1e-12, l.toString());
            }
        }
    }

    @Test
    void theSkullIsBentThroughThem() {
        for (final Landmark l : Landmark.values()) {
            assertEquals(0, SkullForm.INSTANCE.apply(l.drawn()).minus(to.get(l)).length(), 1e-6, l.toString());
        }
        assertEquals(0, SkullForm.INSTANCE.apply(SkullContacts.CONDYLE).minus(SkullContacts.CONDYLE).length(), 1e-6,
                "the jaw's hinge where it was");
    }

    private V3 side(final V3 p, final boolean left) {
        return left ? new V3(-p.x(), p.y(), p.z()) : p;
    }
}
