package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How thick the fat under a man's skin is, part by part, as Störchle and others measured it by ultrasound at 216 sites
 * spread over the bodies of ten young men, as many on each part as its share of the skin (Sci Rep 2018, its table 5,
 * the fat alone, the fibres in it left out), in mm: the head 1.03, the neck 1.30, the trunk before 3.99 and behind
 * 3.17, the buttocks 10.72, the upper arms 2.38, the forearms 0.99, the hands 0.29, the thighs 4.87, the shins 2.44,
 * the feet 0.36; over all of them 3.17. A body's fat as thick everywhere as its {@code mean} makes it against theirs.
 * Between parts, as a point follows each ({@link Weights}); the trunk's front and back, and the buttocks under the
 * hips' crests behind, found on the part's own frame.
 */
public record FatMap(double mean) implements Scalar {

    /** Their mean over all 216 sites, mm. */
    public static final double MEASURED = 3.17;
    /** Their bodies, as measured: as fat as the ten young men were on average. */
    public static final FatMap AS_MEASURED = new FatMap(MEASURED);

    /** How thick the fat is at {@code x}, cm, following the body as {@code w} says, where {@code bind} holds it. */
    public double thickness(final Weights w, final V3 x, final Rig bind) {
        double t = 0;
        for (final Part p : Part.values()) {
            final double share = w.by()[p.ordinal()];
            if (share > 1e-4) {
                t += share * on(p, bind.frame(p).local(x));
            }
        }
        return t * mean / MEASURED / 10;
    }

    /** How thick, mm, as measured, at {@code at} in part {@code p}'s own frame. */
    private double on(final Part p, final V3 at) {
        final Part right = p.side() < 0 ? p.other() : p;
        final double front = smooth((at.z() + 3) / 6);
        final double trunk = 3.99 * front + 3.17 * (1 - front);
        return switch (right) {
            case HEAD -> 1.03;
            case NECK -> 1.30;
            case ABDOMEN, CHEST, RIGHT_GIRDLE -> trunk;
            case PELVIS -> {
                // the buttocks, behind, under the hips' crests
                final double seat = (1 - front) * smooth((8 - at.y()) / 10);
                yield trunk * (1 - seat) + 10.72 * seat;
            }
            case RIGHT_UPPER_ARM -> 2.38;
            case RIGHT_FOREARM -> 0.99;
            case RIGHT_HAND -> 0.29;
            case RIGHT_THIGH -> 4.87;
            case RIGHT_SHIN -> 2.44;
            default -> 0.36;
        };
    }

    /** {@code u} from 0 to 1, smoothly. */
    private double smooth(final double u) {
        final double k = Math.clamp(u, 0, 1);
        return k * k * (3 - 2 * k);
    }
}
