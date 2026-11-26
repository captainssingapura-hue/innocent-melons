package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The pieces of a suit of armour over the joints of the limbs and the thighs and feet ({@link Armour}): over each
 * elbow a couter, a cup round its point, moving with the forearm; over each thigh a cuisse, its front and sides, with
 * the thigh; over each knee a poleyn, a cap standing over the cuisse's foot and the greave's top, with the shin; over
 * each foot a sabaton, its top and sides from the ankle on, with the foot, and a cap over the toes, with the toes.
 * Each open where its joint bends: a couter in the elbow's crook, a cuisse and a poleyn behind, a sabaton under the
 * sole. Each said in its part's own frame as the papers' figure has it.
 */
public record Guards() implements StatelessFunctionalObject {

    public static final Guards INSTANCE = new Guards();

    /** How far out each piece's outside stands from the skin, and how thick it is, cm. */
    private static final double COUTER_OFF = 1.8;
    private static final double CUISSE_OFF = 1.0;
    private static final double POLEYN_OFF = 2.0;
    private static final double SABATON_OFF = 0.9;
    private static final double TOES_OFF = 0.5;
    private static final double THICK = 0.4;
    private static final double TOES_THICK = 0.3;
    /** How finely a large piece's shape is found, a small piece's, and the toes', and how finely each is meshed, cm. */
    private static final double COARSE = 0.4;
    private static final double FINE = 0.25;
    private static final double TOES = 0.2;
    private static final double CELL = 0.2;
    /** How far a cap's edges are rounded off where they meet, cm. */
    private static final double BLEND = 0.4;

    /** Its pieces' cuts, both sides', for the body {@code bind} holds. */
    public List<Plating.Cut> of(final Rig bind) {
        final List<Plating.Cut> cuts = new ArrayList<>();
        for (final Part forearm : List.of(Part.RIGHT_FOREARM, Part.LEFT_FOREARM)) {
            cuts.add(couter(forearm, bind.frame(forearm)));
        }
        for (final Part thigh : List.of(Part.RIGHT_THIGH, Part.LEFT_THIGH)) {
            cuts.add(cuisse(thigh, bind.frame(thigh)));
        }
        for (final Part shin : List.of(Part.RIGHT_SHIN, Part.LEFT_SHIN)) {
            cuts.add(poleyn(shin, bind.frame(shin)));
        }
        for (final Part foot : List.of(Part.RIGHT_FOOT, Part.LEFT_FOOT)) {
            cuts.add(sabaton(foot, bind.frame(foot)));
        }
        for (final Part toes : List.of(Part.RIGHT_TOES, Part.LEFT_TOES)) {
            cuts.add(toes(toes, bind.frame(toes)));
        }
        return cuts;
    }

    /** A couter over {@code forearm}'s elbow: a cup round its point, the ulna's top, and its sides; open in front. */
    private Plating.Cut couter(final Part forearm, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(
                new Field.Element("cup", b.cap(f, new V3(-0.8, 0.5, -1.8), new V3(4.5, 5, 5)), Field.Op.CLIP, BLEND),
                b.cut("crook", f, new V3(0, 0, 0.8), V3.Z, BLEND));
        return new Plating.Cut(new Wearables.Couter(), forearm, COUTER_OFF, THICK, FINE, CELL,
                b.box(f, new double[] {-7, -6, -9, 6, 8, 3}), edges, Optional.empty());
    }

    /**
     * A cuisse over {@code thigh}: its front and sides from below the hip, its top rising toward the outside of the
     * hip, down to above the knee; open behind, and short of the groin within; round the thigh's line
     * ({@link Plating.Core}).
     */
    private Plating.Cut cuisse(final Part thigh, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(b.cut("top", f, new V3(0, -7, 0), new V3(-0.35, 1, 0), 0),
                b.cut("foot", f, new V3(0, -37, 0), new V3(0, -1, 0), 0),
                b.cut("back", f, new V3(0, 0, -3), new V3(0, 0, -1), 0),
                b.cut("groin", f, new V3(-8.3, 0, 0), new V3(-1, 0, 0), 0));
        return new Plating.Cut(new Wearables.Cuisse(), thigh, CUISSE_OFF, THICK, COARSE, CELL,
                b.box(f, new double[] {-9, -39, -11, 13, -1, 10}), edges,
                Optional.of(new Plating.Core(f.at(), f.way(V3.Y), thigh)));
    }

    /** A poleyn over the knee {@code shin} hangs from: a cap over its front and sides; open behind. */
    private Plating.Cut poleyn(final Part shin, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(
                new Field.Element("cap", b.cap(f, new V3(0.3, 1, 2.5), new V3(6.5, 6.5, 6.5)), Field.Op.CLIP, BLEND),
                b.cut("back", f, new V3(0, 0, -0.5), new V3(0, 0, -1), BLEND));
        return new Plating.Cut(new Wearables.Poleyn(), shin, POLEYN_OFF, THICK, FINE, CELL,
                b.box(f, new double[] {-9, -7, -3, 9, 10, 12}), edges, Optional.empty());
    }

    /**
     * A sabaton over {@code foot}: its top and sides, from the front of the ankle, its top falling toward the heel,
     * to the toes' roots; open under the sole and at the heel.
     */
    private Plating.Cut sabaton(final Part foot, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(b.cut("top", f, new V3(0, -0.5, 0), new V3(0, 1, -0.4), 0),
                b.cut("sole", f, new V3(0, -4.8, 0), new V3(0, -1, 0), 0),
                b.cut("heel", f, new V3(0, 0, -2.5), new V3(0, 0, -1), 0),
                b.cut("toes", f, new V3(0, 0, 13.6), V3.Z, 0));
        return new Plating.Cut(new Wearables.Sabaton(), foot, SABATON_OFF, THICK, FINE, CELL,
                b.box(f, new double[] {-7, -8, -9, 8, 4, 16}), edges, Optional.empty());
    }

    /** The sabaton's cap over {@code toes}: their tops and tips, from just behind their roots; open under them. */
    private Plating.Cut toes(final Part toes, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = List.of(b.cut("sole", f, new V3(0, 0.3, 0), new V3(0, -1, 0), 0),
                b.cut("roots", f, new V3(0, 0, -0.8), new V3(0, 0, -1), 0));
        return new Plating.Cut(new Wearables.Sabaton(), toes, TOES_OFF, TOES_THICK, TOES, TOES,
                b.box(f, new double[] {-6, -2, -3, 6, 5, 9}), edges, Optional.empty());
    }
}
