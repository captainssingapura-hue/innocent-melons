package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * The spine's curves made as men's are measured standing, from as it was drawn ({@link Level#drawn}): from the fifth
 * lumbar, held where it is on the sacrum, up to the atlas, held where it is under the skull, each vertebra's middle on
 * from the one below as it was drawn, the whole a share longer or shorter alike and turned, each region bent a like
 * amount at each vertebra, until the lumbar lordosis, L1 to L5, and the thoracic kyphosis, T1 to T12, are young men's
 * (Heo and others, 27 men aged 20 to 30, by EOS, Asian Spine J 2022, 16:732: 49.6 and 36.1 degrees), and the neck,
 * C2 to C7, all but straight, as Hasegawa and others found men's (40 men, J Anat 2017, 230:619: -0.6 degrees).
 * Each curve the angle between its end vertebrae, each vertebra's up from the middle below it to the middle above.
 */
public record SpineCurves() implements StatelessFunctionalObject {

    public static final SpineCurves INSTANCE = new SpineCurves();

    private static final double LORDOSIS = 49.6;
    private static final double KYPHOSIS = 36.1;
    private static final double NECK = -0.6;
    /** How many steps it is found in, and how far each unknown is nudged to see how much it moves the rest. */
    private static final int STEPS = 30;
    private static final double NUDGE = 1e-4;

    /** Each vertebra's middle as measured, by {@link Level#ordinal}. */
    public V3[] middles() {
        final V3[] drawn = drawn();
        final double[] want = {LORDOSIS, KYPHOSIS, NECK, drawn[0].y(), drawn[0].z()};
        final double[] p = {0, 0, 0, 0, 1};
        for (int step = 0; step < STEPS; step++) {
            final double[] f = measure(build(drawn, p));
            final double[][] slope = new double[5][6];
            for (int j = 0; j < 5; j++) {
                final double[] q = p.clone();
                q[j] += NUDGE;
                final double[] g = measure(build(drawn, q));
                for (int i = 0; i < 5; i++) {
                    slope[i][j] = (g[i] - f[i]) / NUDGE;
                }
            }
            for (int i = 0; i < 5; i++) {
                slope[i][5] = want[i] - f[i];
            }
            final double[] d = solved(slope);
            for (int j = 0; j < 5; j++) {
                p[j] += d[j];
            }
        }
        return build(drawn, p);
    }

    /** The lordosis, the kyphosis, the neck's curve, degrees; where the atlas is, up and ahead. */
    public double[] measure(final V3[] m) {
        return new double[] {lean(m, Level.L5) - lean(m, Level.L1), lean(m, Level.T1) - lean(m, Level.T12),
                lean(m, Level.C7) - lean(m, Level.C2), m[0].y(), m[0].z()};
    }

    /** How far {@code l}'s up leans ahead, degrees, its middles {@code m}. */
    private double lean(final V3[] m, final Level l) {
        final V3 up = l.up(m);
        return Math.toDegrees(Math.atan2(up.z(), up.y()));
    }

    /**
     * The middles from the fifth lumbar up: each on from the one below as drawn, {@code p[4]} times as far, turned
     * by {@code p[3]} degrees and by each region's bend, {@code p[0]}, {@code p[1]}, {@code p[2]} at each vertebra.
     */
    private V3[] build(final V3[] drawn, final double[] p) {
        final int n = drawn.length;
        final V3[] m = new V3[n];
        m[n - 1] = drawn[n - 1];
        double bent = 0;
        for (int k = n - 1; k > 0; k--) {
            final V3 d = drawn[k - 1].minus(drawn[k]);
            bent += p[region(Level.values()[k])];
            final double turn = Math.atan2(d.z(), d.y()) + Math.toRadians(p[3] + bent);
            final double far = Math.hypot(d.y(), d.z()) * p[4];
            m[k - 1] = m[k].plus(new V3(0, far * Math.cos(turn), far * Math.sin(turn)));
        }
        return m;
    }

    /** Which region's bend the step up from {@code below} takes: the lumbar's, the thoracic's, the neck's. */
    private int region(final Level below) {
        if (below.lumbar() && below != Level.L1) {
            return 0;
        }
        return below.cervical() || below == Level.T1 ? 2 : 1;
    }

    private V3[] drawn() {
        final Level[] all = Level.values();
        final V3[] d = new V3[all.length];
        for (final Level l : all) {
            d[l.ordinal()] = l.drawn();
        }
        return d;
    }

    /** {@code a}, an augmented square system, solved. */
    private double[] solved(final double[][] a) {
        final int n = a.length;
        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(a[r][c]) > Math.abs(a[pivot][c])) {
                    pivot = r;
                }
            }
            final double[] t = a[c];
            a[c] = a[pivot];
            a[pivot] = t;
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    final double k = a[r][c] / a[c][c];
                    for (int j = c; j <= n; j++) {
                        a[r][j] -= k * a[c][j];
                    }
                }
            }
        }
        final double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = a[i][n] / a[i][i];
        }
        return x;
    }
}
