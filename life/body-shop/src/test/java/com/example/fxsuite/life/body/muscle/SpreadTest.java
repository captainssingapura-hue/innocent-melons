package com.example.fxsuite.life.body.muscle;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.trunk.TrapeziusUpper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A sheet spread from a line to a line ({@link MuscleForm.Spread}): it reaches either end of the line it arises from
 * and of the line it ends on, on either side; the trapezius's lower part from its highest spine to its lowest; the
 * trapezius's upper part following the neck where it arises.
 */
class SpreadTest {

    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);

    @Test
    void aSpreadReachesBothEndsOfBothItsLines() {
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            if (m.form() instanceof MuscleForm.Spread s) {
                for (final boolean left : new boolean[] {false, true}) {
                    final Mesher.Mesh mesh = MuscleShape.INSTANCE.mesh(m, rig, left, 2);
                    for (final List<Anchor> line : List.of(s.origin(), s.insertion())) {
                        for (final Anchor a : List.of(line.getFirst(), line.getLast())) {
                            final double d = nearest(mesh, a.in(rig, left));
                            assertTrue(d < 1, m.name() + " reaches " + a + ": " + d + " cm off");
                        }
                    }
                }
            }
        }
    }


    @Test
    void theTrapeziussUpperPartFollowsTheNeckWhereItArises() {
        final Section origin = TrapeziusUpper.INSTANCE.sections().getFirst();
        assertEquals(Part.NECK, origin.at().part(false), "its origin's half follows the neck, not the head");
    }

    private double nearest(final Mesher.Mesh mesh, final V3 p) {
        final float[] q = mesh.points();
        double best = Double.MAX_VALUE;
        for (int i = 0; i < q.length; i += 3) {
            best = Math.min(best, Math.sqrt((q[i] - p.x()) * (q[i] - p.x()) + (q[i + 1] - p.y()) * (q[i + 1] - p.y())
                    + (q[i + 2] - p.z()) * (q[i + 2] - p.z())));
        }
        return best;
    }
}
