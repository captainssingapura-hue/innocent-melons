package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.fat.FatBeneath;
import com.example.fxsuite.life.body.fat.FatMap;
import com.example.fxsuite.life.body.fat.FatTarget;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.muscle.MuscleBody;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A body's skin as a bench wears it: over its muscles, its deep ones too, and its bones, the fat under it as thick as
 * Störchle's young men had it on average, drawn as taut as the skin bench draws it by default ({@link SkinFit}); found
 * once for a body its size and kept. And the skin as what is worn over it is fitted to ({@link Beneath}).
 */
public final class Skins {

    /** How thick the skin is, cm; how taut it is drawn. */
    private static final double SKIN = 0.2;
    private static final int TENSION = 15;

    /** The colour it is drawn. */
    public static final String COLOUR = "#d9a98c";

    private final Map<Proportions, Flesh> made = new ConcurrentHashMap<>();
    private final Map<Proportions, Flesh> moving = new ConcurrentHashMap<>();

    /** The skin of a body of {@code p}'s size: found now, if not yet; a long while, best away from the screen. */
    public Flesh of(final Proportions p) {
        return made.computeIfAbsent(p, x -> SkinFit.INSTANCE.of(target(x), SKIN, TENSION));
    }

    /**
     * The skin of a body of {@code p}'s size as it is drawn moving, smoothed one step less, a quarter the points
     * ({@link SkinFit#coarse}): found now, if not yet.
     */
    public Flesh moving(final Proportions p) {
        return moving.computeIfAbsent(p, x -> SkinFit.INSTANCE.coarse(target(x), SKIN, TENSION));
    }

    /** What a skin over a body of {@code p}'s size is found over: its muscles, its deep ones too, and its bones. */
    private FatTarget target(final Proportions p) {
        final Rig bind = new Rig(p, BoundPose.INSTANCE.pose(), false);
        final Nearness near = new Nearness(FatBeneath.INSTANCE.of(MuscleBody.INSTANCE.make(p, true, 1), bind));
        return new FatTarget(near, new FatMap(FatMap.MEASURED, SKIN), bind);
    }

    /** {@code skin} as what is worn over it is fitted to: its points, its triangles, how each follows the body. */
    public Beneath beneath(final Flesh skin) {
        final float[] points = new float[3 * skin.size()];
        final Part[] main = new Part[skin.size()];
        final Weights[] weights = new Weights[skin.size()];
        final int[] which = skin.which();
        final float[] shares = skin.shares();
        for (int i = 0; i < skin.size(); i++) {
            points[3 * i] = (float) skin.point(i).x();
            points[3 * i + 1] = (float) skin.point(i).y();
            points[3 * i + 2] = (float) skin.point(i).z();
            main[i] = skin.most(i);
            final float[] by = new float[Part.values().length];
            for (int k = 0; k < Flesh.PARTS; k++) {
                final int q = which[Flesh.PARTS * i + k];
                if (q >= 0) {
                    by[skin.parts().get(q).ordinal()] += shares[Flesh.PARTS * i + k];
                }
            }
            weights[i] = new Weights(by);
        }
        return new Beneath(points, skin.triangles(), main, weights);
    }
}
