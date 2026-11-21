package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.FatLayer;
import com.example.fxsuite.life.body.fat.FatMap;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Underneath;
import com.example.fxsuite.life.body.wear.Weights;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Skin over a thigh's fat, the thigh a ball with a lump on it: the skin the fat's own points and faces; drawn taut,
 * over the lump, never nearer what is beneath than as thick as it is and a third of the fat there.
 */
class SkinLayerTest {

    private static final double THICK = 0.2;
    private final Rig bind = new Rig(Proportions.PAPERS, BoundPose.INSTANCE.pose(), false);
    private final V3 middle = new V3(20, -20, 0);

    @Test
    void theSkinIsTheFatsOwnPointsAndFaces() {
        final Flesh skin = skin(5);
        final Flesh fat = fat().flesh();
        assertEquals(fat.size(), skin.size());
        assertArrayEquals(fat.triangles(), skin.triangles());
    }

    @Test
    void drawnTautItIsNeverNearerWhatIsBeneathThanItAndSomeFat() {
        final FatTarget target = target();
        final Flesh skin = skin(15);
        final double fat = FatMap.AS_MEASURED.thickness(alone(), middle, bind);
        for (int i = 0; i < skin.size(); i++) {
            final double d = target.depth(skin.point(i), FatTarget.Region.LEG, 1, 3)[0];
            assertTrue(d > THICK + 0.3 * fat - 1e-3, "point " + i + " " + d + " from what is beneath");
        }
    }

    private Flesh skin(final int tension) {
        return SkinLayer.INSTANCE.of(fat(), target(), THICK, tension);
    }

    /** A thigh beneath: a ball 5 cm round, and a lump 1.2 cm round on it, before. */
    private FatTarget target() {
        final Underneath u = Underneath.INSTANCE;
        final Beneath thigh = u.with(ball(middle, 5, 24, 12), ball(middle.plus(new V3(0, 0, 5)), 1.2, 12, 6));
        return new FatTarget(new Nearness(thigh), FatMap.AS_MEASURED, bind);
    }

    /** The thigh's fat: the ball as far out as the fat is thick, not over the lump, bound to the thigh. */
    private FatLayer.Fat fat() {
        final double t = FatMap.AS_MEASURED.thickness(alone(), middle, bind);
        final Beneath b = ball(middle, 5 + t, 24, 12);
        final Sheet s = new Sheet();
        for (int i = 0; i < b.size(); i++) {
            s.add(b.point(i), alone());
        }
        for (int i = 0; i < b.triangles().length; i += 3) {
            s.triangle(b.triangles()[i], b.triangles()[i + 1], b.triangles()[i + 2]);
        }
        final FatTarget.Region[] regions = new FatTarget.Region[b.size()];
        Arrays.fill(regions, FatTarget.Region.LEG);
        return new FatLayer.Fat(null, s.flesh(bind), List.of(), regions);
    }

    /** A ball round {@code c}, {@code r} cm, its triangles facing out, following the right thigh. */
    private Beneath ball(final V3 c, final double r, final int round, final int rings) {
        final List<V3> at = new ArrayList<>();
        at.add(c.plus(new V3(0, -r, 0)));
        for (int j = 1; j < rings; j++) {
            final double a = Math.PI * j / rings - Math.PI / 2;
            for (int k = 0; k < round; k++) {
                final double b = 2 * Math.PI * k / round;
                at.add(c.plus(new V3(r * Math.cos(a) * Math.cos(b), r * Math.sin(a), r * Math.cos(a) * Math.sin(b))));
            }
        }
        at.add(c.plus(new V3(0, r, 0)));
        final List<Integer> t = new ArrayList<>();
        for (int k = 0; k < round; k++) {
            final int n = (k + 1) % round;
            t.addAll(List.of(0, 1 + n, 1 + k));
            final int top = at.size() - 1;
            final int last = 1 + (rings - 2) * round;
            t.addAll(List.of(top, last + k, last + n));
            for (int j = 0; j + 2 < rings; j++) {
                final int a = 1 + j * round;
                t.addAll(List.of(a + k, a + n, a + round + n, a + k, a + round + n, a + round + k));
            }
        }
        final float[] p = new float[3 * at.size()];
        for (int i = 0; i < at.size(); i++) {
            p[3 * i] = (float) at.get(i).x();
            p[3 * i + 1] = (float) at.get(i).y();
            p[3 * i + 2] = (float) at.get(i).z();
        }
        final Part[] main = new Part[at.size()];
        Arrays.fill(main, Part.RIGHT_THIGH);
        final Weights[] w = new Weights[at.size()];
        Arrays.fill(w, alone());
        return new Beneath(p, t.stream().mapToInt(Integer::intValue).toArray(), main, w);
    }

    private Weights alone() {
        final float[] w = new float[Part.values().length];
        w[Part.RIGHT_THIGH.ordinal()] = 1;
        return new Weights(w);
    }
}
