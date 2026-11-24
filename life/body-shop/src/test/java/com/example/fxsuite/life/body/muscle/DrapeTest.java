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
        return Optional.of(new Bed.Laid(new V3(out.x() * 5, p.y(), out.z() * 5), out, false));
    };

    /**
     * A ridge two centimetres wide standing two up from the floor, as a spine's tip or a scapula's angle does under a
     * sheet, a bone's or a muscle's: inside it, its nearest side, or its top if that is nearer; above it, its top.
     */
    private Bed ridge(final boolean bone) {
        return p -> {
            final double side = 1 - Math.abs(p.x());
            final double top = 2 - p.z();
            if (top > 0 && side < top) {
                final double s = Math.signum(p.x());
                return Optional.of(new Bed.Laid(new V3(s, p.y(), p.z()), new V3(s, 0, 0), bone));
            }
            return Optional.of(new Bed.Laid(new V3(p.x(), p.y(), 2), V3.Z, bone));
        };
    }

    @Test
    void aSheetPassesOverABoneStandingOutUnderIt() {
        for (final V3 p : narrow(ridge(true))) {
            assertTrue(p.z() >= 2, "every point over the ridge's top: " + p);
        }
    }

    @Test
    void aSheetDeepInAMuscleIsLaidOnItsNearest() {
        for (final V3 p : narrow(ridge(false))) {
            assertTrue(p.z() < 2, "every point by the ridge's side, none spiking out over its top: " + p);
        }
    }

    /** A narrow band laid a centimetre and a half down inside the ridge, facing up. */
    private java.util.List<V3> narrow(final Bed bed) {
        return Drape.INSTANCE.ring(new V3(0.5, 0, 0.5), new V3(0.5, 0, 0.6), V3.X, V3.Z, V3.Y, 0.2, 0.2, bed);
    }

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
