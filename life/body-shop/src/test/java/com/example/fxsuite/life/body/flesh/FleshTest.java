package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Dof;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A body's flesh as one surface: closed and facing out; each point's shares adding to one, a shin's following the shin
 * alone, the knee's shared between the thigh and the shin; posed as it was bound, where it was found; a knee bent, the
 * shin's flesh swung with it and the chest's left where it was.
 */
class FleshTest {

    private final Flesh flesh = Fleshing.INSTANCE.make(Physique.WOMAN, new Detail(1.6));
    private final Rig bound = new Rig(Physique.WOMAN.proportions(), Fleshing.INSTANCE.bind(), false);

    @Test
    void itIsClosedAndFacesOut() {
        final int[] t = flesh.triangles();
        double volume = 0;
        for (int i = 0; i < t.length; i += 3) {
            volume += flesh.point(t[i]).dot(flesh.point(t[i + 1]).cross(flesh.point(t[i + 2]))) / 6;
        }
        assertTrue(volume > 20_000, "a body's worth, facing out: " + volume + " cm3");
    }

    @Test
    void itsSharesAddToOneTheKneesSharedBetweenItsParts() {
        boolean knee = false;
        boolean shin = false;
        final double kneeAt = bound.frame(Part.RIGHT_SHIN).at().y();
        for (int i = 0; i < flesh.size(); i++) {
            double sum = 0;
            for (final Part p : Part.values()) {
                sum += flesh.share(i, p);
            }
            assertEquals(1, sum, 1e-4, "point " + i);
            final V3 v = flesh.point(i);
            if (v.x() > 0 && Math.abs(v.y() - kneeAt) < 0.5) {
                knee |= flesh.share(i, Part.RIGHT_THIGH) > 0.2 && flesh.share(i, Part.RIGHT_SHIN) > 0.2;
            }
            if (v.x() > 0 && Math.abs(v.y() - (kneeAt - 20)) < 1) {
                shin |= flesh.share(i, Part.RIGHT_SHIN) > 0.99;
            }
        }
        assertTrue(knee, "the knee's flesh follows both the thigh and the shin");
        assertTrue(shin, "the shin's flesh follows the shin alone");
    }

    @Test
    void posedAsItWasBoundItIsWhereItWasFound() {
        final float[] at = new float[flesh.size() * 3];
        final float[] out = new float[flesh.size() * 3];
        flesh.posed(bound, at, out);
        for (int i = 0; i < flesh.size(); i++) {
            assertEquals(0, new V3(at[3 * i], at[3 * i + 1], at[3 * i + 2]).minus(flesh.point(i)).length(), 1e-3);
        }
    }

    @Test
    void aKneeBentSwingsTheShinsFleshAndLeavesTheChests() {
        final float[] was = new float[flesh.size() * 3];
        final float[] is = new float[flesh.size() * 3];
        final float[] out = new float[flesh.size() * 3];
        final Rig bent = new Rig(Physique.WOMAN.proportions(), Fleshing.INSTANCE.bind().with(Part.RIGHT_SHIN, Dof.KNEE,
                90), false);
        flesh.posed(bound, was, out);
        flesh.posed(bent, is, out);
        for (int i = 0; i < flesh.size(); i++) {
            final double moved = new V3(is[3 * i] - was[3 * i], is[3 * i + 1] - was[3 * i + 1],
                    is[3 * i + 2] - was[3 * i + 2]).length();
            final boolean low = flesh.point(i).y() < bound.frame(Part.RIGHT_SHIN).at().y() - 15;
            if (flesh.share(i, Part.RIGHT_SHIN) > 0.99 && low) {
                assertTrue(moved > 10, "the shin's flesh swung back: " + moved);
                final V3 rigid = bent.frame(Part.RIGHT_SHIN).place(bound.frame(Part.RIGHT_SHIN).local(flesh.point(i)));
                if (flesh.share(i, Part.RIGHT_SHIN) > 0.9999) {
                    assertEquals(0, rigid.minus(new V3(is[3 * i], is[3 * i + 1], is[3 * i + 2])).length(), 1e-3,
                            "wholly the shin's, carried as the shin is");
                }
            }
            if (flesh.share(i, Part.CHEST) > 0.99) {
                assertEquals(0, moved, 1e-4, "the chest's flesh where it was");
            }
        }
    }
}
