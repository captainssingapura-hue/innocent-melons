package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Bounds;
import com.example.fxsuite.life.body.wear.Plating;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A robot's shell over its limbs ({@link Shell}), on both sides: over each shoulder a cap, with the upper arm; round
 * the upper arm below it; round the forearm from below the elbow to above the wrist; over the hand, its fingers
 * together, to the wrist; round the thigh from where the hips leave off, its top running from between the legs up
 * over the hip, to above the knee; round the shin from below the knee to above the ankle; over the foot from the
 * ankle, its heel too, open under the sole; over the toes. The elbow, the wrist, the knee and the ankle left bare,
 * their housings seen. Each said in its part's own frame as the papers' figure has it.
 */
record LimbShell() {

    /** How far out each plate stands from the skin, cm. */
    private static final double OFF = 1.0;
    private static final double CAP_OFF = 1.6;
    private static final double HAND_OFF = 0.5;
    private static final double FOOT_OFF = 0.9;
    /** How thick a hand's or a toe's plate is, cm. */
    private static final double SMALL = 0.3;
    /** How finely a limb's shape is found and meshed, and a hand's or the toes', cm. */
    private static final double FINE = 0.25;
    private static final double HAND = 0.2;
    /** How far below the hips' leg opening the thigh's plate begins, cm. */
    private static final double SEAM = 1;
    /** How far a cap's edges are rounded off where they meet, cm. */
    private static final double BLEND = 0.4;

    /** Its plates' cuts, both sides', for the body {@code bind} holds, of {@code shell}. */
    List<Plating.Cut> of(final Rig bind, final Shell shell) {
        final List<Plating.Cut> cuts = new ArrayList<>();
        for (final int side : new int[] {1, -1}) {
            final boolean right = side > 0;
            final Part arm = right ? Part.RIGHT_UPPER_ARM : Part.LEFT_UPPER_ARM;
            final Part forearm = right ? Part.RIGHT_FOREARM : Part.LEFT_FOREARM;
            final Part hand = right ? Part.RIGHT_HAND : Part.LEFT_HAND;
            final Part thigh = right ? Part.RIGHT_THIGH : Part.LEFT_THIGH;
            final Part shin = right ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = right ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            final Part toes = right ? Part.RIGHT_TOES : Part.LEFT_TOES;
            cuts.add(cap(arm, bind.frame(arm), shell));
            cuts.add(band(arm, bind.frame(arm), -9.5, -28, new double[] {-7, -31, -8, 9, -6, 9}, shell));
            cuts.add(band(forearm, bind.frame(forearm), -2.5, -23, new double[] {-7, -25, -7, 7, 1, 8}, shell));
            cuts.add(hand(hand, bind.frame(hand)));
            cuts.add(thigh(thigh, bind.frame(thigh), bind.frame(Part.PELVIS), side, shell));
            cuts.add(band(shin, bind.frame(shin), -2.5, -39, new double[] {-8, -41, -11, 9, 1, 8}, shell));
            cuts.add(foot(foot, bind.frame(foot), shell));
            cuts.add(toes(toes, bind.frame(toes)));
        }
        return cuts;
    }

    /** A cap over {@code arm}'s shoulder: from its top down the outside of the arm, not round under it. */
    private Plating.Cut cap(final Part arm, final Rig.Frame f, final Shell shell) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(
                new Field.Element("cap", b.cap(f, new V3(1.5, -0.5, 0), new V3(9.5, 9.5, 9.5)), Field.Op.CLIP, BLEND),
                b.cut("inner", f, new V3(-3, 0, 0), new V3(-1, 0, 0), BLEND),
                b.cut("foot", f, new V3(0, -8, 0), new V3(0, -1, 0), BLEND));
        return shell.cut(arm, CAP_OFF, FINE, b.box(f, new double[] {-8, -11, -10, 12, 12, 10}), edges,
                Optional.empty());
    }

    /** A band round {@code part}, from {@code top} down to {@code foot} along it, in the box {@code box}. */
    private Plating.Cut band(final Part part, final Rig.Frame f, final double top, final double foot,
                             final double[] box, final Shell shell) {
        return shell.cut(part, OFF, FINE, Bounds.INSTANCE.box(f, box), Bounds.INSTANCE.between(f, top, foot),
                Optional.of(new Plating.Core(f.at(), f.way(V3.Y), part)));
    }

    /** Over {@code hand}, its fingers together, up to the wrist. */
    private Plating.Cut hand(final Part hand, final Rig.Frame f) {
        return new Plating.Cut(new Figures.ShelledRobot(), hand, HAND_OFF, SMALL, HAND, HAND,
                Bounds.INSTANCE.box(f, new double[] {-5, -21, -4, 8, 3, 8}),
                List.of(Bounds.INSTANCE.cut("wrist", f, new V3(0, 0, 0), V3.Y, 0)), Optional.empty());
    }

    /**
     * Round {@code thigh}, on {@code side}: from just below where the hips leave off, the hips' leg opening in the
     * pelvis's frame {@code pelvis}, down to above the knee.
     */
    private Plating.Cut thigh(final Part thigh, final Rig.Frame f, final Rig.Frame pelvis, final int side,
                              final Shell shell) {
        final V3[] leg = shell.leg(side);
        final List<Field.Element> edges = List.of(
                Bounds.INSTANCE.cut("top", pelvis, leg[0].plus(leg[1].times(SEAM)), leg[1].times(-1), 0),
                Bounds.INSTANCE.cut("foot", f, new V3(0, -39, 0), new V3(0, -1, 0), 0));
        return shell.cut(thigh, OFF, FINE, Bounds.INSTANCE.box(f, new double[] {-11, -41, -11, 14, 4, 11}), edges,
                Optional.of(new Plating.Core(f.at(), f.way(V3.Y), thigh)));
    }

    /** Over {@code foot}, its top, sides and heel, from the ankle to the toes' roots; open under the sole. */
    private Plating.Cut foot(final Part foot, final Rig.Frame f, final Shell shell) {
        final List<Field.Element> edges = List.of(Bounds.INSTANCE.cut("top", f, new V3(0, 0.5, 0), V3.Y, 0),
                Bounds.INSTANCE.cut("sole", f, new V3(0, -4.8, 0), new V3(0, -1, 0), 0),
                Bounds.INSTANCE.cut("toes", f, new V3(0, 0, 13.6), V3.Z, 0));
        return shell.cut(foot, FOOT_OFF, FINE, Bounds.INSTANCE.box(f, new double[] {-7, -8, -9, 8, 4, 16}), edges,
                Optional.empty());
    }

    /** Over {@code toes}, their tops and tips, from just behind their roots; open under them. */
    private Plating.Cut toes(final Part toes, final Rig.Frame f) {
        return new Plating.Cut(new Figures.ShelledRobot(), toes, HAND_OFF, SMALL, HAND, HAND,
                Bounds.INSTANCE.box(f, new double[] {-6, -2, -3, 6, 5, 9}),
                List.of(Bounds.INSTANCE.cut("sole", f, new V3(0, 0.3, 0), new V3(0, -1, 0), 0),
                        Bounds.INSTANCE.cut("roots", f, new V3(0, 0, -0.8), new V3(0, 0, -1), 0)), Optional.empty());
    }
}
