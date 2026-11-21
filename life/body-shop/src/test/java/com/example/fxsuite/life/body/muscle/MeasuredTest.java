package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.muscle.leg.LegVolumes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The muscles as measured: the whole leg's muscle as Handsfield's subjects had it for their height and mass;
 * each leg and arm muscle drawn within a fifth of what it was measured; a heavier body more muscle.
 */
class MeasuredTest {

    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);

    @Test
    void theWholeLegIsAsTheirSubjectsWere() {
        // their mean subject, 1.71 m and 71.8 kg, had 7,057 cm3 of muscle in a leg by their own line
        assertEquals(7060, LegVolumes.INSTANCE.whole(1.71, 71.8), 20);
        assertEquals(849, LegVolumes.INSTANCE.volume("gluteus maximus", 7057).orElseThrow(), 15);
    }

    @Test
    void eachMeasuredMuscleIsDrawnNearlyAsBigAsMeasured() {
        final Measured body = new Measured(Proportions.DRAWN.stature() / 100, Proportions.DRAWN.mass());
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            final var wanted = body.measured(m);
            if (wanted.isPresent()) {
                final double drawn = volume(MuscleShape.INSTANCE.mesh(m, body, rig, false, 2));
                assertTrue(drawn > 0.8 * wanted.get() && drawn < 1.35 * wanted.get(),
                        m.name() + ": drawn " + drawn + ", measured " + wanted.get());
            }
        }
    }

    @Test
    void aHeavierBodyHasMoreMuscle() {
        final Muscle m = Musculature.INSTANCE.muscles().stream().filter(x -> x.name().equals("vastus lateralis"))
                .findFirst().orElseThrow();
        final double light = volume(MuscleShape.INSTANCE.mesh(m, new Measured(1.7, 60), rig, false, 2));
        final double heavy = volume(MuscleShape.INSTANCE.mesh(m, new Measured(1.7, 90), rig, false, 2));
        assertTrue(heavy > 1.3 * light, light + " to " + heavy);
    }

    private double volume(final Mesher.Mesh mesh) {
        final float[] p = mesh.points();
        final int[] t = mesh.triangles();
        double v = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = new V3(p[3 * t[i]], p[3 * t[i] + 1], p[3 * t[i] + 2]);
            final V3 b = new V3(p[3 * t[i + 1]], p[3 * t[i + 1] + 1], p[3 * t[i + 1] + 2]);
            final V3 c = new V3(p[3 * t[i + 2]], p[3 * t[i + 2] + 1], p[3 * t[i + 2] + 2]);
            v += a.dot(b.cross(c)) / 6;
        }
        return v;
    }
}
