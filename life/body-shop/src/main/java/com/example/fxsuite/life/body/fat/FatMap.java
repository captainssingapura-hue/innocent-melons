package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.Scalar;

/**
 * How thick the fat under a man's skin is, part by part, as a man Störchle and others measured had it
 * ({@link FatPattern}): the ten on average, or the leanest of them. A body's fat as thick everywhere as its
 * {@code mean} makes it against that man's, mm. Between parts, as a point follows each ({@link Weights}); the trunk's
 * front and back, and the buttocks under the hips' crests behind, found on the part's own frame. Within the trunk's
 * front and round the thigh, as their sites are, as Sengeis and others found them in elite judokas, men 1.763 m tall
 * (Scand J Med Sci Sports 2019, 29:1774, the medians at the eight standard sites): the belly below the navel 5.1 mm
 * to 2.2 above it, so the trunk's front above the navel {@link #ABOVE} times its average and below it {@link #BELOW};
 * the thigh's outer side 4.2 to its front 2.5, so its front and inner side {@link #THIGH_FRONT} times its average and
 * its outer side {@link #THIGH_SIDE}. And {@code over} it, cm, as thick everywhere, if anything is, the skin: for what
 * lies on the fat to be found as far out.
 */
public record FatMap(FatPattern pattern, double mean, double over) implements Scalar {

    /** The ten's mean over all 216 sites, mm. */
    public static final double MEASURED = 3.17;
    /** Their bodies, as measured: as fat as the ten young men were on average. */
    public static final FatMap AS_MEASURED = new FatMap(MEASURED);

    /** The trunk's front above the navel and below it, times its average: two thirds of it above, as 2.2 to 5.1. */
    private static final double ABOVE = 3 / (2 + 5.1 / 2.2);
    private static final double BELOW = ABOVE * 5.1 / 2.2;
    /** The thigh's front and inner side, and its outer side, times its average: half of it each, as 2.5 to 4.2. */
    private static final double THIGH_FRONT = 2 / (1 + 4.2 / 2.5);
    private static final double THIGH_SIDE = THIGH_FRONT * 4.2 / 2.5;

    /** The fat as the ten had it on average and as thick on average as {@code mean}, mm, with {@code over} it. */
    public FatMap(final double mean, final double over) {
        this(FatPattern.AVERAGE, mean, over);
    }

    /** The fat as the ten had it on average and as thick on average as {@code mean}, mm, nothing over it. */
    public FatMap(final double mean) {
        this(mean, 0);
    }

    /** How thick the fat is at {@code x} and what is over it, cm, following the body as {@code w} says. */
    public double thickness(final Weights w, final V3 x, final Rig bind) {
        return fat(w, x, bind) + over;
    }

    /** How thick the fat alone is at {@code x}, cm, following the body as {@code w} says, held as {@code bind} is. */
    public double fat(final Weights w, final V3 x, final Rig bind) {
        double t = 0;
        for (final Part p : Part.values()) {
            final double share = w.by()[p.ordinal()];
            if (share > 1e-4) {
                t += share * on(p, bind.frame(p).local(x));
            }
        }
        return t * mean / pattern.mean() / 10;
    }

    /** How thick, mm, as the man measured had it, at {@code at} in part {@code p}'s own frame. */
    private double on(final Part p, final V3 at) {
        final Part right = p.side() < 0 ? p.other() : p;
        final FatPattern m = pattern;
        final double front = smooth((at.z() + 3) / 6);
        final double upper = m.front() * ABOVE * front + m.back() * (1 - front);
        return switch (right) {
            case HEAD -> m.head();
            case NECK -> m.neck();
            case ABDOMEN, CHEST, RIGHT_GIRDLE -> upper;
            case PELVIS -> {
                // the belly below the navel before; the buttocks, behind, under the hips' crests
                final double lower = m.front() * BELOW * front + m.back() * (1 - front);
                final double seat = (1 - front) * smooth((8 - at.y()) / 10);
                yield lower * (1 - seat) + m.buttocks() * seat;
            }
            case RIGHT_UPPER_ARM -> m.upperArm();
            case RIGHT_FOREARM -> m.forearm();
            case RIGHT_HAND -> m.hand();
            case RIGHT_THIGH -> {
                final double out = smooth((at.x() / Math.max(1e-9, Math.hypot(at.x(), at.z())) + 1) / 2);
                yield m.thigh() * (THIGH_FRONT * (1 - out) + THIGH_SIDE * out);
            }
            case RIGHT_SHIN -> m.shin();
            default -> m.foot();
        };
    }

    /** {@code u} from 0 to 1, smoothly. */
    private double smooth(final double u) {
        final double k = Math.clamp(u, 0, 1);
        return k * k * (3 - 2 * k);
    }
}
