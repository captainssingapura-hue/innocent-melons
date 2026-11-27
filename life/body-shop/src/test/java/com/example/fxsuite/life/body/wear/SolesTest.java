package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A boot's sole under a foot, here an egg of a foot in the right foot's frame: lying between its top and its heel's
 * underside; flat under its tread ahead of the heel, the heel deeper only behind its front; as wide as the foot is
 * where its outline is taken, and its welt more.
 */
class SolesTest {

    /** The egg's middle and its half-lengths across, up and along, in the foot's frame. */
    private static final double MIDDLE_X = 0.5;
    private static final double HALF_ACROSS = 4.5;
    private static final double MIDDLE_UP = -3.5;
    private static final double HALF_UP = 3;
    private static final double SLACK = 0.3;

    private final Rig.Frame f = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false).frame(Part.RIGHT_FOOT);
    private final Soles.Sole sole = new Soles.Sole(Part.RIGHT_FOOT, -5.5, -7.1, -8.5, 1.1, 3.5, -9.8,
            new double[] {-8, -10.3, -11, 9, -6.6, 16}, 0.25, 0.2);

    @Test
    void itLiesUnderTheFootFlatUnderItsTreadItsHeelDeeperOnlyBehindAndItsWeltPastTheFoot() {
        final V3 k = f.scale();
        final Field.Shape foot = Field.Ellipsoid.of(f.place(new V3(MIDDLE_X, MIDDLE_UP, 4)),
                f.way(V3.X).times(HALF_ACROSS * k.x()), f.way(V3.Y).times(HALF_UP * k.y()),
                f.way(V3.Z).times(11 * k.z()));
        final Plate made = Soles.INSTANCE.of(sole, f, Field.Sampled.of(foot, 0.25));
        final float[] p = made.mesh().points();
        assertTrue(p.length > 0, "a sole is made");
        double widest = 0;
        boolean tread = false;
        boolean heel = false;
        for (int i = 0; i < p.length; i += 3) {
            final V3 local = f.local(new V3(p[i], p[i + 1], p[i + 2]));
            assertTrue(local.y() > sole.under() - SLACK && local.y() < sole.top() + SLACK, "between: " + local);
            if (local.y() < sole.tread() - SLACK) {
                assertTrue(local.z() < sole.heel() + SLACK, "the heel only behind its front: " + local);
                heel = true;
            }
            tread |= Math.abs(local.y() - sole.tread()) < SLACK && local.z() > sole.heel() + 1;
            widest = Math.max(widest, Math.abs(local.x() - MIDDLE_X));
        }
        assertTrue(tread && heel, "a tread ahead and a heel behind");
        final double drop = (sole.level() - MIDDLE_UP) / HALF_UP;
        final double across = HALF_ACROSS * Math.sqrt(1 - drop * drop);
        assertTrue(widest > across + 0.5 && widest < across + sole.welt() + 0.6, widest + " against " + across);
    }
}
