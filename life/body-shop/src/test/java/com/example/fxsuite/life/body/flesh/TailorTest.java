package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tights cut from a skin: none of them above the waistband, their edge on it; stood off the skin as thick as the cloth;
 * every share of every point adding to one; the skin under them left out, the skin above them kept.
 */
class TailorTest {

    private final Physique who = Physique.MAN;
    private final Flesh skin = Fleshing.INSTANCE.make(who, new Detail(1.6));
    private final Tailor.Clad clad = Tailor.INSTANCE.clad(skin, Tights.BLACK, who.proportions().scale());
    private final double top = (Tights.BLACK.waist().y(new Rig(Proportions.DRAWN, Fleshing.INSTANCE.bind(), false))
            + Tights.BLACK.off()) * who.proportions().scale();

    @Test
    void theyReachNoHigherThanTheirWaistbandTheirEdgeOnIt() {
        double highest = -1e9;
        for (int i = 0; i < clad.worn().size(); i++) {
            highest = Math.max(highest, clad.worn().point(i).y());
        }
        assertEquals(top, highest, 0.5, "the waistband's edge, cut straight");
    }

    @Test
    void theyStandOffTheSkinAndFollowWhatItFollows() {
        final int[] t = clad.worn().triangles();
        double volume = 0;
        for (int i = 0; i < t.length; i += 3) {
            final V3 a = clad.worn().point(t[i]);
            volume += a.dot(clad.worn().point(t[i + 1]).cross(clad.worn().point(t[i + 2]))) / 6;
        }
        assertTrue(volume > 0, "facing out");
        for (int i = 0; i < clad.worn().size(); i++) {
            double sum = 0;
            for (final Part p : Part.values()) {
                sum += clad.worn().share(i, p);
            }
            assertEquals(1, sum, 1e-4, "point " + i + "'s shares");
        }
    }

    @Test
    void theSkinUnderThemIsLeftOutTheSkinAboveKept() {
        final int[] bare = clad.bare().triangles();
        int above = 0;
        for (int i = 0; i < bare.length; i += 3) {
            double high = -1e9;
            double arms = 0;
            for (int k = 0; k < 3; k++) {
                high = Math.max(high, skin.point(bare[i + k]).y());
                final int v = bare[i + k];
                arms += skin.share(v, Part.RIGHT_FOREARM) + skin.share(v, Part.LEFT_FOREARM)
                        + skin.share(v, Part.RIGHT_UPPER_ARM) + skin.share(v, Part.LEFT_UPPER_ARM);
            }
            assertTrue(high >= top || arms > 0, "no bare skin wholly under them but an arm's");
            above += high > top ? 1 : 0;
        }
        assertTrue(above > 1000, "the skin above them kept: " + above);
    }
}
