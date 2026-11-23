package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Hoop;
import com.example.fxsuite.life.body.wear.Underneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * What fills the belly between the ribs and the pelvis, as a body whose wall rests on it: not the organs one by one,
 * but their bulk, round as the ribs are where they end and as the pelvis is at its crests, from the one to the other,
 * a little into each; so that the muscles of the belly's wall have something behind them, and what lies over them is
 * not drawn in through the gaps between them. Following the abdomen as the body moves, the chest at its top and the
 * pelvis at its foot.
 */
public record Viscera() implements StatelessFunctionalObject {

    public static final Viscera INSTANCE = new Viscera();

    /** How many points round, and how many rings. */
    private static final int ROUND = 32;
    private static final int RINGS = 16;
    /** How far ahead of the line from the neck's joint to the head's the front of its vertebrae may be, cm. */
    private static final double COLUMN = 2;

    /** The belly's bulk, between the bones {@code bones} where {@code bind} holds them. */
    public Beneath of(final Beneath bones, final Rig bind) {
        final double hip = bind.frame(Part.RIGHT_THIGH).at().y();
        final double ribs = bind.frame(Part.CHEST).at().y() + 2;
        final double crests = hip + 6;
        final Hoop.Ring top = section(bones, ribs);
        final Hoop.Ring foot = section(bones, crests);
        final List<V3> points = new ArrayList<>();
        final List<Weights> weights = new ArrayList<>();
        for (int r = 0; r < RINGS; r++) {
            final double u = (double) r / (RINGS - 1);
            final double y = crests - 4 + (ribs - crests + 8) * u;
            final double s = smooth((y - crests) / (ribs - crests));
            for (int k = 0; k < ROUND; k++) {
                final V3 a = foot.points().get(k).minus(foot.middle());
                final V3 b = top.points().get(k).minus(top.middle());
                final V3 mid = foot.middle().plus(top.middle().minus(foot.middle()).times(s));
                final V3 off = a.plus(b.minus(a).times(s));
                points.add(new V3(mid.x() + off.x(), y, mid.z() + off.z()));
                weights.add(along(s));
            }
        }
        return beneath(points, weights);
    }

    /**
     * The throat: the windpipe, the larynx and the gullet before the neck's bones, as one tube, from behind the top of
     * the breastbone up to under the jaw, broadest at the larynx; so that the front of the neck has something under its
     * strap muscles. Following the neck, the chest at its foot. Before the neck's vertebrae: the front of its bones
     * down its middle, of those near the line from the neck's joint to the head's, no more than {@link #COLUMN} ahead
     * of it; the hyoid, well ahead of it, left out.
     */
    public Beneath throat(final Beneath bones, final Rig bind) {
        final V3 neckJoint = bind.frame(Part.NECK).at();
        final V3 headJoint = bind.frame(Part.HEAD).at();
        final double neck = neckJoint.y();
        final double jaw = headJoint.y() - 1;
        double spine = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < bones.size(); i++) {
            final V3 p = bones.point(i);
            final double line = neckJoint.z() + (headJoint.z() - neckJoint.z()) * (p.y() - neck) / (headJoint.y() - neck);
            spine = bones.main()[i] == Part.NECK && p.y() > neck && p.y() < neck + 6 && Math.abs(p.x()) < 1.5
                    && p.z() < line + COLUMN ? Math.max(spine, p.z()) : spine;
        }
        final List<V3> points = new ArrayList<>();
        final List<Weights> weights = new ArrayList<>();
        for (int r = 0; r < RINGS; r++) {
            final double u = (double) r / (RINGS - 1);
            final double y = neck - 6 + (jaw - neck + 6) * u;
            final double radius = 1.8 + 0.6 * Math.sin(Math.PI * Math.min(1, u / 0.8));
            for (int k = 0; k < ROUND; k++) {
                final double a = 2 * Math.PI * k / ROUND;
                points.add(new V3(radius * Math.cos(a), y, spine + radius + 0.3 + radius * Math.sin(a)));
                final float[] w = new float[Part.values().length];
                w[Part.CHEST.ordinal()] = (float) Math.max(0, 1 - 3 * u);
                w[Part.NECK.ordinal()] = 1 - w[Part.CHEST.ordinal()];
                weights.add(new Weights(w));
            }
        }
        return beneath(points, weights);
    }

    /** The section of {@code bones} at height {@code y}: its hull, {@link #ROUND} points round. */
    private Hoop.Ring section(final Beneath bones, final double y) {
        final double[] angles = new double[ROUND];
        for (int k = 0; k < ROUND; k++) {
            angles[k] = 2 * Math.PI * k / ROUND;
        }
        final java.util.Set<Part> trunk = java.util.EnumSet.of(Part.PELVIS, Part.ABDOMEN, Part.CHEST);
        final Beneath own = Underneath.INSTANCE.only(bones, trunk);
        return Hoop.INSTANCE.ring(own, java.util.stream.IntStream.range(0, own.triangles().length / 3).toArray(),
                new Hoop.Plane(new V3(0, y, 0), V3.X, V3.Z), p -> true, angles, 0);
    }

    /** Following the pelvis at the foot, the abdomen between, the chest at the top, as far up as {@code s}. */
    private Weights along(final double s) {
        final float[] w = new float[Part.values().length];
        final double low = Math.max(0, 1 - 2 * s);
        final double high = Math.max(0, 2 * s - 1);
        w[Part.PELVIS.ordinal()] = (float) low;
        w[Part.CHEST.ordinal()] = (float) high;
        w[Part.ABDOMEN.ordinal()] = (float) (1 - low - high);
        return new Weights(w);
    }

    /** The rings of {@code points} as one closed surface, its ends shut. */
    private Beneath beneath(final List<V3> points, final List<Weights> weights) {
        final List<Integer> t = new ArrayList<>();
        for (int r = 0; r + 1 < RINGS; r++) {
            for (int k = 0; k < ROUND; k++) {
                final int a = r * ROUND + k;
                final int b = r * ROUND + (k + 1) % ROUND;
                t.addAll(List.of(a, b + ROUND, b, a, a + ROUND, b + ROUND));
            }
        }
        for (int k = 1; k + 1 < ROUND; k++) {
            t.addAll(List.of(0, k, k + 1));
            final int top = (RINGS - 1) * ROUND;
            t.addAll(List.of(top, top + k + 1, top + k));
        }
        final float[] at = new float[3 * points.size()];
        final Part[] main = new Part[points.size()];
        for (int i = 0; i < points.size(); i++) {
            at[3 * i] = (float) points.get(i).x();
            at[3 * i + 1] = (float) points.get(i).y();
            at[3 * i + 2] = (float) points.get(i).z();
            main[i] = weights.get(i).most();
        }
        return new Beneath(at, t.stream().mapToInt(Integer::intValue).toArray(), main,
                weights.toArray(new Weights[0]));
    }

    private double smooth(final double u) {
        final double k = Math.clamp(u, 0, 1);
        return k * k * (3 - 2 * k);
    }
}
