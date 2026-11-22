package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Subdivision;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.flesh.Around;
import com.example.fxsuite.life.body.skin.ScanFit;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

/**
 * A head's blank, as clay is shaped before anything is put on it: the head's cage ({@link HeadCage}), smoothed once,
 * fitted to a reference head as to a scan ({@link ScanFit}), each round each of its smoothed points drawn toward the
 * nearest point of the reference's surface but its features ({@link FeatureMasks}), found afresh as it moves, a far
 * one counting for less; where a feature was, nothing of the reference to draw it, each point over it drawn instead to
 * the middle of its neighbours, a membrane stretched over the hole, the surface round it carried smoothly and evenly
 * over, not sagging into it: its eyes, its nose, its mouth and its ears left out, to be put on after. A point is over a
 * hole if the nearest of the reference it may be drawn to is on the hole's rim, and not near.
 */
public record BlankFit() implements StatelessFunctionalObject {

    public static final BlankFit INSTANCE = new BlankFit();

    /** How many rounds; how far a nearest point may be looked for, cm. */
    private static final int ROUNDS = 20;
    private static final double REACH = 6;
    /** How far above where the reference's neck is cut its blank begins, where its surface is whole, cm. */
    private static final double SOUND = 2;
    /** How far from a hole's rim a point is over the hole, cm. */
    private static final double HOLE = 0.3;

    /** What the fit draws toward: the reference, its features left out, the rim round them; the blank's neighbours. */
    private record Pull(Nearness near, IntPredicate counts, boolean[] rim, int[][] next) {
    }

    /** The blank fitted to {@code ref}, its triangles of a feature, {@code masked} says, left out. */
    public Mesher.Mesh of(final ReferenceHead ref, final Feature[] masked) {
        final float[] p = ref.mesh().points();
        final Part[] main = new Part[p.length / 3];
        Arrays.fill(main, Part.HEAD);
        final Weights[] w = new Weights[main.length];
        Arrays.fill(w, new Weights(new float[Part.values().length]));
        final Nearness near = new Nearness(new Beneath(p, ref.mesh().triangles(), main, w));
        double bottom = 1e9;
        double top = -1e9;
        for (int i = 1; i < p.length; i += 3) {
            bottom = Math.min(bottom, p[i]);
            top = Math.max(top, p[i]);
        }
        bottom += SOUND;
        final double[] heights = {bottom, (bottom - 1) / 2, -1, 0.15 * top, 0.33 * top, 0.5 * top, 0.67 * top,
            0.82 * top, 0.94 * top};
        final Subdivision s = Subdivision.INSTANCE;
        final Subdivision.Level base = s.once(s.of(HeadCage.INSTANCE.of(bare(ref.mesh(), masked), heights), 1));
        final Subdivision.Level shape = s.times(base, 2);
        final Pull pull = new Pull(near, k -> masked[k] == null, rim(ref.mesh(), masked),
                Around.INSTANCE.neighbours(shape.size(), s.mesh(shape).triangles()));
        final V3[] fine = ScanFit.INSTANCE.fitted(base, 2, at -> nearest(pull, at), ROUNDS);
        final double[] flat = new double[3 * fine.length];
        for (int i = 0; i < fine.length; i++) {
            flat[3 * i] = fine[i].x();
            flat[3 * i + 1] = fine[i].y();
            flat[3 * i + 2] = fine[i].z();
        }
        return s.mesh(new Subdivision.Level(flat, shape.faces(), shape.sharp()));
    }

    /**
     * What each of {@code at} is drawn toward: the nearest point of the reference but its features; the middle of its
     * neighbours, if it is over a hole; itself, if none is near.
     */
    private V3[] nearest(final Pull pull, final V3[] at) {
        final Nearness near = pull.near();
        final float[] p = near.beneath().points();
        final int[] t = near.beneath().triangles();
        final V3[] to = new V3[at.length];
        IntStream.range(0, at.length).parallel().forEach(i -> {
            final Nearness.Near n = near.nearest(at[i], REACH, pull.counts());
            if (n.triangle() < 0) {
                to[i] = at[i];
                return;
            }
            if (pull.rim()[n.triangle()] && n.distance() > HOLE) {
                to[i] = Around.INSTANCE.middle(at, pull.next()[i]);
                return;
            }
            final int a = t[3 * n.triangle()];
            final int b = t[3 * n.triangle() + 1];
            final int c = t[3 * n.triangle() + 2];
            to[i] = new V3(n.a() * p[3 * a] + n.b() * p[3 * b] + n.c() * p[3 * c],
                    n.a() * p[3 * a + 1] + n.b() * p[3 * b + 1] + n.c() * p[3 * c + 1],
                    n.a() * p[3 * a + 2] + n.b() * p[3 * b + 2] + n.c() * p[3 * c + 2]);
        });
        return to;
    }

    /** The points of {@code m} on a triangle of no feature, three numbers each: where the blank's rings start round. */
    private float[] bare(final Mesher.Mesh m, final Feature[] masked) {
        final int[] t = m.triangles();
        final boolean[] kept = new boolean[m.size()];
        for (int k = 0; k < masked.length; k++) {
            for (int c = 0; c < 3 && masked[k] == null; c++) {
                kept[t[3 * k + c]] = true;
            }
        }
        final float[] q = new float[3 * m.size()];
        int n = 0;
        for (int i = 0; i < kept.length; i++) {
            if (kept[i]) {
                System.arraycopy(m.points(), 3 * i, q, 3 * n++, 3);
            }
        }
        return java.util.Arrays.copyOf(q, 3 * n);
    }

    /** Which of {@code m}'s triangles are on a hole's rim: of no feature, a corner shared with one of a feature. */
    private boolean[] rim(final Mesher.Mesh m, final Feature[] masked) {
        final int[] t = m.triangles();
        final boolean[] edge = new boolean[m.size()];
        for (int k = 0; k < masked.length; k++) {
            for (int c = 0; c < 3 && masked[k] != null; c++) {
                edge[t[3 * k + c]] = true;
            }
        }
        final boolean[] rim = new boolean[masked.length];
        for (int k = 0; k < masked.length; k++) {
            rim[k] = masked[k] == null && (edge[t[3 * k]] || edge[t[3 * k + 1]] || edge[t[3 * k + 2]]);
        }
        return rim;
    }
}
