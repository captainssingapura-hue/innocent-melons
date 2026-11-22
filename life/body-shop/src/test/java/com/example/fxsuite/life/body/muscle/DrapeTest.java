package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A sheet's section laid on its bed: a band eight centimetres wide on an upright cylinder five round lies round it, as
 * thick as it is all across, not running straight off it; with nothing near, it is as drawn.
 */
class DrapeTest {

    /** An upright cylinder, five centimetres round its axis. */
    private final Bed cylinder = p -> {
        final V3 out = new V3(p.x(), 0, p.z()).unit();
        return Optional.of(new Bed.Laid(new V3(out.x() * 5, p.y(), out.z() * 5), out));
    };

    @Test
    void aBandLiesRoundWhatItIsOn() {
        for (final V3 p : ring(cylinder)) {
            final double r = Math.hypot(p.x(), p.z());
            assertTrue(r >= 5 && r <= 5.3, "every point within its depth of the cylinder: " + r);
            assertTrue(Math.abs(p.y()) < 1e-9, "none moved up or down the way it runs");
        }
    }

    @Test
    void withNothingNearItIsAsDrawn() {
        double widest = 0;
        for (final V3 p : ring(q -> Optional.empty())) {
            widest = Math.max(widest, Math.abs(p.x()));
        }
        assertTrue(Math.abs(widest - 4) < 1e-9, "straight across, four centimetres each way");
    }

    private java.util.List<V3> ring(final Bed bed) {
        return Drape.INSTANCE.ring(new V3(0, 0, 5), new V3(0, 0, 5.1), V3.X, V3.Z, V3.Y, 8, 0.2, bed);
    }
}
