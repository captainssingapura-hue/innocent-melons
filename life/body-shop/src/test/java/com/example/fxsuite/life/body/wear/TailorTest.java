package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Dof;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What is worn, made over the muscles and the bones: tights from the waist, lower in front, over the feet to the
 * toes; briefs lower at the crotch than up the sides of the hips; each going with the body as it moves, a knee bent
 * taking the shin's cloth with it, the hips' staying where they were.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TailorTest {

    private final Proportions body = Proportions.PAPERS;
    private final Rig bind = new Rig(body, BoundPose.INSTANCE.pose(), false);
    private final Beneath under = Underneath.INSTANCE.of(MuscleBody.INSTANCE.make(body, false, 1), bind,
            new Detail(1.2));
    private final Garment tights = Tailor.INSTANCE.tights(under, bind, "#222");
    private final Garment briefs = Tailor.INSTANCE.briefs(under, bind, "#eee");

    @Test
    void tightsGoFromTheWaistLowerInFrontOverTheFeetToTheToes() {
        final Flesh c = tights.cloth();
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        final V3 ankle = bind.frame(Part.RIGHT_FOOT).at();
        double front = -1e9;
        double back = -1e9;
        double low = 1e9;
        double ahead = -1e9;
        for (int i = 0; i < c.size(); i++) {
            final V3 p = c.point(i);
            front = Math.abs(p.x()) < 1 && p.z() > 0 ? Math.max(front, p.y()) : front;
            back = Math.abs(p.x()) < 1 && p.z() < 0 ? Math.max(back, p.y()) : back;
            low = Math.min(low, p.y());
            ahead = Math.max(ahead, p.z());
        }
        assertTrue(back > hip.y() + 10 && back < hip.y() + 14, "up to the waist behind: " + back);
        assertTrue(front < back - 1, "lower in front: " + front + " against " + back);
        assertTrue(low < ankle.y() - 6, "under the heel: " + low);
        assertTrue(ahead > Tailor.INSTANCE.marks(under, bind).toes() - 1, "over the toes: " + ahead);
    }

    @Test
    void briefsAreLowerAtTheCrotchThanUpTheSidesOfTheHips() {
        final Flesh c = briefs.cloth();
        final V3 hip = bind.frame(Part.RIGHT_THIGH).at();
        double middle = 1e9;
        double side = 1e9;
        for (int i = 0; i < c.size(); i++) {
            final V3 p = c.point(i);
            middle = Math.abs(p.x()) < 1 ? Math.min(middle, p.y()) : middle;
            side = Math.abs(p.x()) > hip.x() + 7 ? Math.min(side, p.y()) : side;
        }
        final double crotch = Tailor.INSTANCE.marks(under, bind).crotch();
        assertTrue(middle < crotch + 1, "under the crotch: " + middle + " against " + crotch);
        assertTrue(side > middle + 6, "cut away up the sides: " + side + " against " + middle);
    }

    @Test
    void whatIsWornGoesWithTheBodyAsItMoves() {
        final Flesh c = tights.cloth();
        final Rig bent = new Rig(body, BoundPose.INSTANCE.pose().with(Part.RIGHT_SHIN, Dof.KNEE, 90), false);
        final float[] was = new float[3 * c.size()];
        final float[] now = new float[3 * c.size()];
        c.posed(bind, was, new float[was.length]);
        c.posed(bent, now, new float[now.length]);
        final V3 knee = bind.frame(Part.RIGHT_SHIN).at();
        final double crotch = Tailor.INSTANCE.marks(under, bind).crotch();
        for (int i = 0; i < c.size(); i++) {
            final V3 p = c.point(i);
            final double moved = Math.hypot(Math.hypot(now[3 * i] - was[3 * i], now[3 * i + 1] - was[3 * i + 1]),
                    now[3 * i + 2] - was[3 * i + 2]);
            if (p.y() > crotch + 2) {
                assertTrue(moved < 0.01, "the hips' cloth where it was: " + p + " moved " + moved);
            }
            if (p.x() > 0 && p.y() < knee.y() - 15) {
                assertTrue(moved > 10, "the right shin's cloth gone with it: " + p + " moved " + moved);
            }
        }
    }
}
