package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A body's muscles at rest ({@link MuscleState.Natural}), soft: each point of a muscle that lies inside another
 * pressed out of it, half the way, the other giving the other half; inside a bone, all the way, the bone not giving;
 * each press spread over the points about it, its tendon staying where it is; then each muscle swelling where nothing
 * presses on it as far as keeps how much of it there is ({@link Packed}). So many rounds, the muscles and bones looked
 * at afresh each.
 */
public record Packing() implements StatelessFunctionalObject {

    public static final Packing INSTANCE = new Packing();

    /** How many rounds; how far in a point is looked for being inside another, cm. */
    private static final int ROUNDS = 8;
    private static final double REACH = 3;
    /** How far a muscle lying over another may go out over it, and in steps of how far; how far a bone is looked for, cm. */
    private static final double OVER = 4;
    private static final double STEP = 0.1;
    private static final double OUT = 15;

    /** The muscles {@code surfaces} at rest among each other and the bones {@code bones}. */
    public List<MuscleSurface.Surface> packed(final List<MuscleSurface.Surface> surfaces, final Beneath bones) {
        final List<Packed> muscles = surfaces.stream().map(Packed::new).toList();
        for (int round = 0; round < ROUNDS; round++) {
            final int[] owner = owners(muscles, bones);
            final Nearness near = new Nearness(together(muscles, bones));
            final double[][] boxes = boxes(muscles);
            IntStream.range(0, muscles.size()).parallel()
                    .forEach(i -> muscles.get(i).press(presses(muscles, i, near, owner, boxes)));
            muscles.parallelStream().forEach(Packed::keep);
        }
        return muscles.stream().map(Packed::surface).toList();
    }

    /** How far each point of {@code m}, the {@code i}th, is to be pressed ({@link #press}); none, if it is too small. */
    private V3[] presses(final List<Packed> all, final int i, final Nearness near, final int[] owner,
                         final double[][] boxes) {
        final Packed m = all.get(i);
        final V3[] at = m.at();
        final V3[] by = new V3[at.length];
        for (int k = 0; k < at.length; k++) {
            by[k] = m.small() || m.tendon()[k] >= 1 ? V3.ZERO : press(all, i, near, owner, boxes, at[k]);
        }
        return by;
    }

    /**
     * How far {@code p}, of the {@code i}th muscle, is to be pressed: out of a bone it lies in, all the way; out of each
     * other muscle it lies inside, each whose box, {@code boxes}, it is in tried, as deep as it: to half way to that
     * one's nearest point; deeper: out over it, away from the bone under it; shallower: not at all, the other lying over
     * it giving way.
     */
    private V3 press(final List<Packed> all, final int i, final Nearness near, final int[] owner,
                     final double[][] boxes, final V3 p) {
        V3 by = V3.ZERO;
        final Nearness.Near bone = near.nearest(p, REACH, tri -> owner[tri] < 0);
        if (bone.triangle() >= 0 && near.inside(bone, p)) {
            by = by.plus(to(near, bone).minus(p));
        }
        final int mine = all.get(i).depth();
        for (int j = 0; j < boxes.length; j++) {
            final int theirs = all.get(j).depth();
            if (j != i && mine <= theirs && in(boxes[j], p) && all.get(j).inside(p)) {
                if (mine < theirs) {
                    by = by.plus(over(all.get(j), p, outward(near, owner, p, boxes[j])));
                } else {
                    final int whose = j;
                    final Nearness.Near n = near.nearest(p, REACH, tri -> owner[tri] == whose);
                    by = n.triangle() < 0 ? by : by.plus(to(near, n).minus(p).times(0.5));
                }
            }
        }
        return by;
    }

    /** How far along {@code way} from {@code p}, inside {@code under}, it is out of it, in steps of {@link #STEP}. */
    private V3 over(final Packed under, final V3 p, final V3 way) {
        double s = STEP;
        while (s < OVER && under.inside(p.plus(way.times(s)))) {
            s += STEP;
        }
        return way.times(s);
    }

    /**
     * The way out from the bones at {@code p}: from the nearest bone within {@link #OUT}; from the middle of
     * {@code box}, the muscle it is in, if no bone is near.
     */
    private V3 outward(final Nearness near, final int[] owner, final V3 p, final double[] box) {
        final Nearness.Near bone = near.nearest(p, OUT, tri -> owner[tri] < 0);
        final V3 from = bone.triangle() >= 0 ? to(near, bone)
                : new V3((box[0] + box[3]) / 2, (box[1] + box[4]) / 2, (box[2] + box[5]) / 2);
        final V3 way = p.minus(from);
        return way.length() < 1e-9 ? V3.Y : way.unit();
    }

    /** The point {@code n} is the nearest of. */
    private V3 to(final Nearness near, final Nearness.Near n) {
        final Beneath b = near.beneath();
        final int[] t = b.triangles();
        return b.point(t[3 * n.triangle()]).times(n.a()).plus(b.point(t[3 * n.triangle() + 1]).times(n.b()))
                .plus(b.point(t[3 * n.triangle() + 2]).times(n.c()));
    }

    /** Whether {@code p} is within {@code box}, its lowest corner then its highest. */
    private boolean in(final double[] box, final V3 p) {
        return p.x() >= box[0] && p.y() >= box[1] && p.z() >= box[2] && p.x() <= box[3] && p.y() <= box[4]
                && p.z() <= box[5];
    }

    /** Each muscle's box, as it is now. */
    private double[][] boxes(final List<Packed> muscles) {
        final double[][] boxes = new double[muscles.size()][];
        for (int i = 0; i < boxes.length; i++) {
            final double[] b = {1e9, 1e9, 1e9, -1e9, -1e9, -1e9};
            for (final V3 p : muscles.get(i).at()) {
                final double[] v = {p.x(), p.y(), p.z()};
                for (int c = 0; c < 3; c++) {
                    b[c] = Math.min(b[c], v[c]);
                    b[c + 3] = Math.max(b[c + 3], v[c]);
                }
            }
            boxes[i] = b;
        }
        return boxes;
    }

    /** Whose each triangle of the muscles and bones together is: a muscle's, by its number; a bone's, none. */
    private int[] owners(final List<Packed> muscles, final Beneath bones) {
        int count = 0;
        for (final Packed m : muscles) {
            count += m.triangles().length / 3;
        }
        final int[] owner = new int[count + bones.triangles().length / 3];
        Arrays.fill(owner, -1);
        int at = 0;
        for (int i = 0; i < muscles.size(); i++) {
            final int n = muscles.get(i).triangles().length / 3;
            Arrays.fill(owner, at, at + n, i);
            at += n;
        }
        return owner;
    }

    /** The muscles as they are now and the bones, one surface, the muscles' triangles first. */
    private Beneath together(final List<Packed> muscles, final Beneath bones) {
        final List<float[]> points = new ArrayList<>();
        final List<int[]> triangles = new ArrayList<>();
        int base = 0;
        for (final Packed m : muscles) {
            final V3[] at = m.at();
            final float[] p = new float[3 * at.length];
            for (int k = 0; k < at.length; k++) {
                p[3 * k] = (float) at[k].x();
                p[3 * k + 1] = (float) at[k].y();
                p[3 * k + 2] = (float) at[k].z();
            }
            points.add(p);
            final int[] t = m.triangles().clone();
            for (int k = 0; k < t.length; k++) {
                t[k] += base;
            }
            triangles.add(t);
            base += at.length;
        }
        points.add(bones.points());
        final int[] bt = bones.triangles().clone();
        for (int k = 0; k < bt.length; k++) {
            bt[k] += base;
        }
        triangles.add(bt);
        return beneath(points, triangles);
    }

    /** {@code points} and {@code triangles}, each list's numbered on from the last, as one surface. */
    private Beneath beneath(final List<float[]> points, final List<int[]> triangles) {
        final float[] p = new float[points.stream().mapToInt(a -> a.length).sum()];
        int at = 0;
        for (final float[] a : points) {
            System.arraycopy(a, 0, p, at, a.length);
            at += a.length;
        }
        final int[] t = new int[triangles.stream().mapToInt(a -> a.length).sum()];
        at = 0;
        for (final int[] a : triangles) {
            System.arraycopy(a, 0, t, at, a.length);
            at += a.length;
        }
        final Part[] main = new Part[p.length / 3];
        Arrays.fill(main, Part.CHEST);
        final Weights[] w = new Weights[main.length];
        Arrays.fill(w, new Weights(new float[Part.values().length]));
        return new Beneath(p, t, main, w);
    }
}
