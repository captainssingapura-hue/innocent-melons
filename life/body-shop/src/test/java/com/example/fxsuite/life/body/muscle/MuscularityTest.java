package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Cage;
import com.example.fxsuite.life.body.BoundPose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A muscle's size apart from its anatomy: a body with twice as much of every muscle has each belly about twice as
 * big, lying further out from its bone, on the same tendons at the same places; a band, as drawn whatever the body.
 */
class MuscularityTest {

    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);
    private final Muscularity twice = (m, asDrawn) -> 2 * asDrawn;

    @Test
    void twiceTheMuscleIsTwiceTheBellyOnTheSameTendons() {
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            if (m.layer() == Layer.TENDON) {
                continue;
            }
            final double drawn = volume(MuscleShape.INSTANCE.mesh(m, rig, false, 2));
            final double grown = volume(MuscleShape.INSTANCE.mesh(m, twice, rig, false, 2));
            assertTrue(grown / drawn > 1.6 && grown / drawn < 2.4, m.name() + ": " + drawn + " to " + grown);
            final Cage a = MuscleShape.INSTANCE.cage(m, rig, false);
            final Cage b = MuscleShape.INSTANCE.cage(m, twice, rig, false);
            final int last = a.size() - MuscleShape.ROUND;
            for (final int i : new int[] {0, last}) {
                if (!MuscleShape.INSTANCE.kept(m, i / MuscleShape.ROUND)) {
                    continue;
                }
                assertEquals(0, middle(a, i).minus(middle(b, i)).length(), 1e-9, m.name() + "'s ends");
            }
        }
    }

    @Test
    void aBigTendonOrBandIsAsDrawnWhateverTheBody() {
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            if (m.layer() == Layer.TENDON) {
                final Cage a = MuscleShape.INSTANCE.cage(m, rig, false);
                final Cage b = MuscleShape.INSTANCE.cage(m, twice, rig, false);
                for (int i = 0; i < a.size(); i++) {
                    assertEquals(0, a.points().get(i).minus(b.points().get(i)).length(), 1e-9, m.name());
                }
            }
        }
    }

    /** The middle of the ring of {@code c} from point {@code from}. */
    private V3 middle(final Cage c, final int from) {
        V3 m = V3.ZERO;
        for (int k = 0; k < MuscleShape.ROUND; k++) {
            m = m.plus(c.points().get(from + k).times(1.0 / MuscleShape.ROUND));
        }
        return m;
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
