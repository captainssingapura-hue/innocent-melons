package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.life.body.wear.Beneath;
import com.example.fxsuite.life.body.wear.Bounds;
import com.example.fxsuite.life.body.wear.Plate;
import com.example.fxsuite.life.body.wear.Plating;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A robot's shell, shaped over the skin of the body it is built as ({@link Plating}): a plate to each part, hard and
 * moving with it, parted from the next at the joint between them, so that the frame within shows there and the body
 * bends. Its trunk here: its hips, from under the waist round the seat and down between the legs, open for each leg;
 * its waist; its chest, back and front in one, open for the neck and the arms; its neck. Its limbs as
 * {@link LimbShell} has them. Each said in its part's own frame as the papers' figure has it, so that it fits a body
 * of any size. Its head is its frame's helmet, not a plate.
 */
public record Shell() implements StatelessFunctionalObject {

    public static final Shell INSTANCE = new Shell();

    /** How far out the trunk's plates stand from the skin, and how thick they are, cm. */
    private static final double OFF = 1.2;
    private static final double THICK = 0.5;
    /** How finely the trunk's shape is found, and the neck's; how finely any plate is meshed, at most, cm. */
    private static final double COARSE = 0.4;
    private static final double FINE = 0.25;
    private static final double MESH = 0.3;
    /** How big the cubes the skin is looked in by are, cm: small, the hands' skin being made finely. */
    private static final double LOOK = 0.6;
    /** Where each leg's opening in the hips runs from, between the legs, in the pelvis's frame, cm. */
    private static final double CROTCH_X = 4;
    private static final double CROTCH_Y = -11;

    /**
     * Its plates, shaped over {@code skin} as the body {@code bind} holds it; those round the forearms over it as
     * {@code held} holds it, twisted as they usually are ({@link com.example.fxsuite.life.body.wear.Armour#held}).
     */
    public List<Plate> of(final Beneath skin, final Beneath held, final Rig bind) {
        final Nearness near = new Nearness(skin, LOOK);
        final Nearness twisted = new Nearness(held, LOOK);
        return cuts(bind).stream().map(c -> Plating.INSTANCE.of(c.part() == Part.RIGHT_FOREARM
                || c.part() == Part.LEFT_FOREARM ? twisted : near, List.of(), c)).toList();
    }

    /** What each of its plates is to be, for the body {@code bind} holds: its trunk's, then its limbs'. */
    List<Plating.Cut> cuts(final Rig bind) {
        final List<Plating.Cut> cuts = new ArrayList<>(List.of(hips(bind.frame(Part.PELVIS)),
                waist(bind.frame(Part.ABDOMEN)), chest(bind.frame(Part.CHEST)), neck(bind.frame(Part.NECK))));
        cuts.addAll(new LimbShell().of(bind, this));
        return cuts;
    }

    /**
     * The plane each leg's opening in the hips lies in, in the pelvis's frame {@code f}: from between the legs up and
     * out over the hip, on {@code side}; a point of it and the way out of the hips across it.
     */
    V3[] leg(final int side) {
        final V3 out = new V3(side * 13, -11, 0).unit();
        return new V3[] {new V3(side * CROTCH_X, CROTCH_Y, 0), out};
    }

    /** The hips: round the seat and the belly's foot, from under the waist down between the legs, open for each leg. */
    private Plating.Cut hips(final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        final List<Field.Element> edges = new ArrayList<>(List.of(b.cut("top", f, new V3(0, 10.5, 0), V3.Y, 0)));
        for (final int side : new int[] {1, -1}) {
            edges.add(b.cut("leg", f, leg(side)[0], leg(side)[1], 0));
        }
        return cut(Part.PELVIS, OFF, COARSE, b.box(f, new double[] {-20, -16, -17, 20, 12, 12}), edges,
                Optional.empty());
    }

    /** The waist: round the belly and the small of the back, between the hips and the chest. */
    private Plating.Cut waist(final Rig.Frame f) {
        return cut(Part.ABDOMEN, OFF, COARSE, Bounds.INSTANCE.box(f, new double[] {-17, -3, -14, 17, 14, 14}),
                Bounds.INSTANCE.between(f, 11.5, -0.5), Optional.empty());
    }

    /**
     * The chest: over the trunk from just above the waist up, its top running round the neck's foot and over each
     * shoulder's top, an arm's hole carved out at each side from under the shoulder down to the armpit.
     */
    private Plating.Cut chest(final Rig.Frame f) {
        final double k = f.scale().x();
        final List<Field.Element> edges = new ArrayList<>(List.of(
                Bounds.INSTANCE.cut("collar", f, new V3(0, 26, 9), new V3(0, 23, 2.5), 0),
                Bounds.INSTANCE.cut("waist", f, new V3(0, -1.5, 0), new V3(0, -1, 0), 0),
                new Field.Element("neck", oval(f, new V3(0, 34, -3), V3.Y, 9 * k, 10 * k, V3.Z, 8.0 / 9),
                        Field.Op.CARVE, 0.6)));
        for (final int s : new int[] {1, -1}) {
            edges.add(new Field.Element("arm", oval(f, new V3(s * 21, 15.5, -2.7), V3.X, 8 * k, 7 * k, V3.Z, 6.8 / 8),
                    Field.Op.CARVE, 0.6));
        }
        return cut(Part.CHEST, OFF, COARSE, Bounds.INSTANCE.box(f, new double[] {-24, -4, -18, 24, 30, 15}), edges,
                Optional.empty());
    }

    /** The neck: round it, from under the chest's top up under the helmet, its top rising toward the nape. */
    private Plating.Cut neck(final Rig.Frame f) {
        final List<Field.Element> edges = List.of(
                Bounds.INSTANCE.cut("top", f, new V3(0, 1.5, 5), new V3(0, 11.6, 6), 0),
                Bounds.INSTANCE.cut("foot", f, new V3(0, -1.5, 0), new V3(0, -1, 0), 0));
        return cut(Part.NECK, OFF - 0.2, FINE, Bounds.INSTANCE.box(f, new double[] {-12, -4, -13, 12, 10, 10}), edges,
                Optional.of(new Plating.Core(f.at(), f.way(V3.Y), Part.NECK)));
    }

    /** A plate of the shell, of {@code part}, standing {@code off}, its shape found {@code sample} apart. */
    Plating.Cut cut(final Part part, final double off, final double sample, final double[] box,
                    final List<Field.Element> edges, final Optional<Plating.Core> core) {
        return new Plating.Cut(new Figures.ShelledRobot(), part, off, THICK, sample, Math.min(sample, MESH), box,
                edges, core);
    }

    /**
     * An oval prism in {@code f}, about {@code c} along {@code axis}, {@code r} round and {@code half} long each way,
     * squashed to {@code ratio} of that along {@code across}.
     */
    private Field.Shape oval(final Rig.Frame f, final V3 c, final V3 axis, final double r, final double half,
                             final V3 across, final double ratio) {
        final V3 at = f.place(c);
        return Field.Squash.of(Field.Cylinder.of(at, f.way(axis), r, half, 0), at, f.way(across), ratio);
    }
}
