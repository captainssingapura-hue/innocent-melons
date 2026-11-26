package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Weights;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The groin made neutral, as a mannequin's is: the pad of fat over the pubis and a smooth closed form below it,
 * between the tops of the thighs, rounding down and back to the perineum; so that the skin there lies over something
 * and does not sink between the thighs, and nothing more is drawn. In the pelvis's own frame as drawn, from
 * {@link #TOP} at the pubic crest down to {@link #FOOT}, {@link #ROUND} points round each of {@link #RINGS} rings; as
 * broad as {@link #BROAD} at the top, half as broad at the foot; following the pelvis.
 */
public record Groin() implements StatelessFunctionalObject {

    public static final Groin INSTANCE = new Groin();

    private static final int ROUND = 24;
    private static final int RINGS = 12;
    /** How high its top and its foot are, from the hip joints' height, cm; how broad it is at its top, half. */
    private static final double TOP = 0.5;
    private static final double FOOT = -9.5;
    private static final double BROAD = 4.5;
    /** How far ahead its middle is at its top, and at its foot, cm. */
    private static final double AHEAD = 5.5;
    private static final double BEHIND = -0.5;

    /** The groin's soft, as the body {@code bind} holds it. */
    public Beneath of(final Rig bind) {
        final Rig.Frame f = bind.frame(Part.PELVIS);
        final float[] w = new float[Part.values().length];
        w[Part.PELVIS.ordinal()] = 1;
        final Weights pelvis = new Weights(w);
        final List<V3> points = new ArrayList<>();
        for (int r = 0; r < RINGS; r++) {
            final double u = (double) r / (RINGS - 1);
            final double y = TOP + (FOOT - TOP) * u;
            final double z = AHEAD + (BEHIND - AHEAD) * u;
            final double round = Math.sqrt(Math.max(0.05, Math.sin(Math.PI * (0.15 + 0.85 * u))));
            final double wide = BROAD * (1 - 0.5 * u) * round;
            final double deep = 2.8 + 0.6 * Math.sin(Math.PI * u) - 0.6 * u;
            for (int k = 0; k < ROUND; k++) {
                final double a = 2 * Math.PI * k / ROUND;
                points.add(f.place(new V3(wide * Math.cos(a), y, z + deep * Math.sin(a))));
            }
        }
        return closed(points, pelvis);
    }

    /** The rings of {@code points} as one closed surface, its ends shut, each point following as {@code w} says. */
    private Beneath closed(final List<V3> points, final Weights w) {
        final List<Integer> t = new ArrayList<>();
        for (int r = 0; r + 1 < RINGS; r++) {
            for (int k = 0; k < ROUND; k++) {
                final int a = r * ROUND + k;
                final int b = r * ROUND + (k + 1) % ROUND;
                t.addAll(List.of(a, b + ROUND, b, a, a + ROUND, b + ROUND));
            }
        }
        final int top = (RINGS - 1) * ROUND;
        for (int k = 1; k + 1 < ROUND; k++) {
            t.addAll(List.of(0, k, k + 1));
            t.addAll(List.of(top, top + k + 1, top + k));
        }
        final float[] at = new float[3 * points.size()];
        final Part[] main = new Part[points.size()];
        final Weights[] weights = new Weights[points.size()];
        for (int i = 0; i < points.size(); i++) {
            at[3 * i] = (float) points.get(i).x();
            at[3 * i + 1] = (float) points.get(i).y();
            at[3 * i + 2] = (float) points.get(i).z();
            main[i] = Part.PELVIS;
            weights[i] = w;
        }
        return new Beneath(at, t.stream().mapToInt(Integer::intValue).toArray(), main, weights);
    }
}
