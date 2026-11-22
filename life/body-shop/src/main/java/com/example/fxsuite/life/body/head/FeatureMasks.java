package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.EnumMap;
import java.util.Map;

/**
 * Where a reference head's features are ({@link Feature}), each the inside of an ellipsoid placed by its landmarks:
 * each eye about its middle, a little ahead, its lids with it; the mouth about its line, back from its lips' front;
 * the nose from the eyes to its foot, back from its tip, the tip, the foot, the upper lip and the mouth's line found
 * down the middle of the face, where it reaches furthest ahead and least, in turn; each ear round what of the head
 * is furthest out beside it, about the height of the eyes. Which feature each of its triangles is of, by where its
 * middle is: the first whose region it is in, the eyes, the mouth, the nose, the ears in turn; none, the blank's.
 */
public record FeatureMasks() implements StatelessFunctionalObject {

    public static final FeatureMasks INSTANCE = new FeatureMasks();

    /** How far in from its furthest an ear's points are, cm; how far round them its region reaches, cm. */
    private static final double EAR = 1.2;
    private static final double ROUND = 1.5;

    /** A feature's region: its middle and its half axes along x, y and z. */
    public record Region(V3 middle, V3 half) {

        /** Whether {@code p} is in it. */
        public boolean holds(final V3 p) {
            final V3 d = p.minus(middle);
            final double x = d.x() / half.x();
            final double y = d.y() / half.y();
            final double z = d.z() / half.z();
            return x * x + y * y + z * z < 1;
        }
    }

    /** The feature each triangle of {@code r} is of, by number; none, if it is the blank's. */
    public Feature[] of(final ReferenceHead r) {
        final Map<Feature, Region> regions = regions(r);
        final float[] p = r.mesh().points();
        final int[] t = r.mesh().triangles();
        final Feature[] of = new Feature[t.length / 3];
        for (int k = 0; k < of.length; k++) {
            final V3 mid = point(p, t[3 * k]).plus(point(p, t[3 * k + 1])).plus(point(p, t[3 * k + 2])).times(1.0 / 3);
            for (final Feature f : Feature.values()) {
                if (of[k] == null && regions.get(f).holds(mid)) {
                    of[k] = f;
                }
            }
        }
        return of;
    }

    /** Each feature's region on {@code r}. */
    public Map<Feature, Region> regions(final ReferenceHead r) {
        final float[] p = r.mesh().points();
        final double eyes = (r.rightEye().middle().y() + r.leftEye().middle().y()) / 2;
        // down the middle of the face: the nose's tip, the nose's foot under it, the upper lip, the mouth's line
        final double tip = extreme(p, eyes - 7, eyes - 1, true);
        final double foot = extreme(p, tip - 2.5, tip - 0.5, false);
        final double lip = extreme(p, foot - 2, foot - 0.2, true);
        final double mouth = extreme(p, lip - 1.5, lip - 0.2, false);
        final double lips = ahead(p, mouth - 1.2, mouth + 1.2);
        final Map<Feature, Region> m = new EnumMap<>(Feature.class);
        final V3 eye = new V3(1.9, 1.4, 1.8);
        m.put(Feature.RIGHT_EYE, new Region(r.rightEye().middle().plus(new V3(0, 0, 0.6)), eye));
        m.put(Feature.LEFT_EYE, new Region(r.leftEye().middle().plus(new V3(0, 0, 0.6)), eye));
        m.put(Feature.MOUTH, new Region(new V3(0, mouth, lips - 0.8), new V3(2.7, 1.3, 1.5)));
        m.put(Feature.NOSE, new Region(new V3(0, (eyes + foot) / 2, ahead(p, tip - 0.25, tip + 0.25) - 1),
                new V3(1.9, (eyes - foot) / 2 + 0.1, 2)));
        m.put(Feature.RIGHT_EAR, ear(p, eyes, 1));
        m.put(Feature.LEFT_EAR, ear(p, eyes, -1));
        return m;
    }

    /**
     * The height between {@code low} and {@code high} where the face, down its middle, reaches furthest ahead, if
     * {@code most}, or least far: a tip, or a hollow between two.
     */
    private double extreme(final float[] p, final double low, final double high, final boolean most) {
        double best = most ? -1e9 : 1e9;
        double at = (low + high) / 2;
        for (double y = low; y <= high; y += 0.1) {
            final double z = ahead(p, y - 0.25, y + 0.25);
            if (z > -1e8 && (most ? z > best : z < best)) {
                best = z;
                at = y;
            }
        }
        return at;
    }

    /** How far ahead the points {@code p} near the middle reach between heights {@code low} and {@code high}. */
    private double ahead(final float[] p, final double low, final double high) {
        double z = -1e9;
        for (int i = 0; i < p.length; i += 3) {
            if (Math.abs(p[i]) < 1 && p[i + 1] > low && p[i + 1] < high) {
                z = Math.max(z, p[i + 2]);
            }
        }
        return z;
    }

    /**
     * The ear on side {@code side} (1 the right): round the points of {@code p} beside the head, from below the eyes'
     * height {@code eyes} to a little above, as far out as its furthest but {@link #EAR}, its lobe and its rim
     * with it, {@link #ROUND} more each way, room for it to go over into the blank; a centimetre in from its furthest.
     */
    private Region ear(final float[] p, final double eyes, final int side) {
        final V3 far = furthestOut(p, eyes, side);
        final double[] b = {1e9, 1e9, -1e9, -1e9};
        for (int i = 0; i < p.length; i += 3) {
            if (p[i + 1] > eyes - 7 && p[i + 1] < eyes + 2 && p[i] * side > far.x() * side - EAR) {
                b[0] = Math.min(b[0], p[i + 1]);
                b[1] = Math.min(b[1], p[i + 2]);
                b[2] = Math.max(b[2], p[i + 1]);
                b[3] = Math.max(b[3], p[i + 2]);
            }
        }
        return new Region(new V3(far.x() - side, (b[0] + b[2]) / 2, (b[1] + b[3]) / 2),
                new V3(2.2, (b[2] - b[0]) / 2 + ROUND, (b[3] - b[1]) / 2 + ROUND));
    }

    /** The point of {@code p} furthest out on side {@code side} (1 the right), from the eyes' height {@code eyes}. */
    private V3 furthestOut(final float[] p, final double eyes, final int side) {
        V3 best = new V3(0, eyes, 0);
        for (int i = 0; i < p.length; i += 3) {
            if (p[i + 1] > eyes - 5 && p[i + 1] < eyes + 1 && p[i] * side > best.x() * side) {
                best = point(p, i / 3);
            }
        }
        return best;
    }

    private V3 point(final float[] p, final int i) {
        return new V3(p[3 * i], p[3 * i + 1], p[3 * i + 2]);
    }
}
