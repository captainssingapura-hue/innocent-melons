package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.fat.FatBeneath;
import com.example.fxsuite.life.body.fat.FatMap;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The skin found over the muscles and bones at once: standing at rest, the forearms turned and the elbows bent from how
 * it was found, nowhere torn, no side of it stretched to more than twice and a half its length.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SkinFitTest {

    private final Proportions body = Proportions.PAPERS;
    private final Rig bind = new Rig(body, BoundPose.INSTANCE.pose(), false);
    private final Flesh skin = SkinFit.INSTANCE.of(new FatTarget(new Nearness(FatBeneath.INSTANCE.of(
            MuscleBody.INSTANCE.make(body, true, 1), bind)), new FatMap(FatMap.MEASURED, 0.2), bind), 0.2, 15);

    @Test
    void standingAtRestItIsNowhereTorn() {
        final float[] was = new float[3 * skin.size()];
        final float[] now = new float[3 * skin.size()];
        skin.posed(bind, was, new float[was.length]);
        skin.posed(new Rig(body, Pose.REST, false), now, new float[now.length]);
        final int[] t = skin.triangles();
        int torn = 0;
        for (int i = 0; i < t.length; i += 3) {
            for (int k = 0; k < 3; k++) {
                final int u = t[i + k];
                final int v = t[i + (k + 1) % 3];
                final double a = length(was, u, v);
                final double b = length(now, u, v);
                torn += b > 2.5 * a && b - a > 0.5 ? 1 : 0;
            }
        }
        assertEquals(0, torn, "sides stretched to more than twice and a half");
    }

    private double length(final float[] p, final int u, final int v) {
        return Math.sqrt(Math.pow(p[3 * u] - p[3 * v], 2) + Math.pow(p[3 * u + 1] - p[3 * v + 1], 2)
                + Math.pow(p[3 * u + 2] - p[3 * v + 2], 2));
    }
}
