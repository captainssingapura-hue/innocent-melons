package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import com.example.fxsuite.taxonomy.Leaf;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A suit of armour, shaped over the skin of the body it is for ({@link Plating}), each piece hard and moving with the
 * part it guards: a cuirass over the chest, back and front in one, open for the neck and the arms and ending at the
 * foot of the ribs, moving with the chest; over each shoulder a pauldron, standing off the cuirass's top, moving with
 * the upper arm; round each forearm a vambrace; round each shin a greave; below the cuirass a fauld round the belly,
 * with the abdomen; a gorget round the neck under the helmet's rim, with the neck; over each hand a gauntlet, with the
 * hand; and over the elbows, the thighs, the knees and the feet, {@link Guards}. Each said in its part's own frame
 * as the papers' figure has it, so that it fits a body of any size.
 */
public record Armour() implements StatelessFunctionalObject {

    public static final Armour INSTANCE = new Armour();

    /** How far out each piece's outside stands from the skin, and how thick it is, cm. */
    private static final double CUIRASS_OFF = 1.6;
    private static final double PAULDRON_OFF = 2.8;
    private static final double VAMBRACE_OFF = 0.9;
    private static final double GREAVE_OFF = 1.0;
    private static final double FAULD_OFF = 1.0;
    private static final double GORGET_OFF = 1.0;
    private static final double GAUNTLET_OFF = 0.6;
    private static final double GAUNTLET_THICK = 0.3;
    private static final double THICK = 0.4;
    /** How finely a large piece's shape is found and meshed, and a small piece's, cm. */
    private static final double COARSE = 0.4;
    private static final double FINE = 0.25;
    private static final double CELL = 0.2;
    /** How finely a gauntlet's shape is found and meshed, for the fingers, cm. */
    private static final double HAND = 0.2;
    /** How big the cubes the skin is looked in by are, cm: small, the hands' skin being made finely. */
    private static final double LOOK = 0.6;

    /**
     * Its pieces, shaped over {@code skin} as the body {@code bind} holds it, and over what is worn under them,
     * {@code under}, cloth over the skin: standing off whichever is further out.
     */
    public List<Plate> of(final Beneath skin, final List<Beneath> under, final Rig bind) {
        final Nearness near = new Nearness(skin, LOOK);
        final List<Nearness> over = under.stream().map(u -> new Nearness(u, LOOK)).toList();
        final List<Plating.Cut> cuts = new ArrayList<>(List.of(cuirass(bind.frame(Part.CHEST)),
                fauld(bind.frame(Part.ABDOMEN)), gorget(bind.frame(Part.NECK))));
        for (final Part hand : List.of(Part.RIGHT_HAND, Part.LEFT_HAND)) {
            cuts.add(gauntlet(hand, bind.frame(hand)));
        }
        for (final Part arm : List.of(Part.RIGHT_UPPER_ARM, Part.LEFT_UPPER_ARM)) {
            cuts.add(pauldron(arm, bind.frame(arm)));
        }
        for (final Part forearm : List.of(Part.RIGHT_FOREARM, Part.LEFT_FOREARM)) {
            cuts.add(band(new Wearables.Vambrace(), forearm, bind.frame(forearm), VAMBRACE_OFF, -4, -19,
                    new double[] {-7, -24, -7, 9, -2, 8}));
        }
        for (final Part shin : List.of(Part.RIGHT_SHIN, Part.LEFT_SHIN)) {
            cuts.add(band(new Wearables.Greave(), shin, bind.frame(shin), GREAVE_OFF, -6, -35,
                    new double[] {-8, -37, -11, 9, -4, 8}));
        }
        cuts.addAll(Guards.INSTANCE.of(bind));
        return cuts.stream().map(c -> Plating.INSTANCE.of(near, over, c)).toList();
    }

    /**
     * The cuirass: over the trunk from the foot of the ribs up, its top running round the neck's foot and over each
     * shoulder's top, an arm's hole carved out at each side from under the shoulder down to the armpit.
     */
    private Plating.Cut cuirass(final Rig.Frame f) {
        final double k = f.scale().x();
        final List<Field.Element> edges = new ArrayList<>(List.of(
                new Field.Element("collar", Field.Plane.of(f.place(new V3(0, 26, 9)), f.way(new V3(0, 23, 2.5))),
                        Field.Op.CLIP, 0),
                new Field.Element("waist", Field.Plane.of(f.place(new V3(0, 1, 0)), f.way(new V3(0, -1, 0))),
                        Field.Op.CLIP, 0),
                new Field.Element("neck", oval(f, new V3(0, 34, -3), V3.Y, 9 * k, 10 * k, V3.Z, 8.0 / 9),
                        Field.Op.CARVE, 0.6)));
        for (final int s : new int[] {1, -1}) {
            edges.add(new Field.Element("arm", oval(f, new V3(s * 21, 15.5, -2.7), V3.X, 8 * k, 7 * k, V3.Z, 6.8 / 8),
                    Field.Op.CARVE, 0.6));
        }
        return new Plating.Cut(new Wearables.Cuirass(), Part.CHEST, CUIRASS_OFF, THICK, COARSE, CELL,
                Bounds.INSTANCE.box(f, new double[] {-24, 2, -18, 24, 30, 15}), edges, Optional.empty());
    }

    /**
     * A pauldron over {@code arm}'s shoulder: a cap over the joint, from the shoulder's top down the outside of the
     * arm, not round under it toward the armpit.
     */
    private Plating.Cut pauldron(final Part arm, final Rig.Frame f) {
        final double k = f.scale().x();
        final double r = 10.5 * k;
        final List<Field.Element> edges = List.of(
                new Field.Element("cap", Field.Ellipsoid.of(f.place(new V3(1.5, -0.5, 0)), f.way(V3.X).times(r),
                        f.way(V3.Y).times(r), f.way(V3.Z).times(r)), Field.Op.CLIP, 0.4),
                new Field.Element("inner", Field.Plane.of(f.place(new V3(-3, 0, 0)), f.way(new V3(-1, 0, 0))),
                        Field.Op.CLIP, 0.4),
                new Field.Element("foot", Field.Plane.of(f.place(new V3(0, -9, 0)), f.way(new V3(0, -1, 0))),
                        Field.Op.CLIP, 0.4));
        return new Plating.Cut(new Wearables.Pauldron(), arm, PAULDRON_OFF, THICK, FINE, CELL,
                Bounds.INSTANCE.box(f, new double[] {-8, -12, -10, 13, 13, 10}), edges, Optional.empty());
    }

    /**
     * A band of {@code kind} round {@code part}, from {@code top} down to {@code foot} along it, its box {@code b};
     * round the limb's line ({@link Plating.Core}).
     */
    private Plating.Cut band(final Leaf<?> kind, final Part part, final Rig.Frame f, final double off,
                             final double top, final double foot, final double[] b) {
        return new Plating.Cut(kind, part, off, THICK, FINE, CELL, Bounds.INSTANCE.box(f, b),
                Bounds.INSTANCE.between(f, top, foot), Optional.of(new Plating.Core(f.at(), f.way(V3.Y), part)));
    }

    /**
     * The fauld: round the belly and the small of the back below the cuirass, from under its foot, standing just
     * inside it, down over the hips' crests to the tights' waist; moving with the abdomen.
     */
    private Plating.Cut fauld(final Rig.Frame f) {
        return new Plating.Cut(new Wearables.Fauld(), Part.ABDOMEN, FAULD_OFF, THICK, COARSE, CELL,
                Bounds.INSTANCE.box(f, new double[] {-21, -4, -17, 21, 18, 17}), Bounds.INSTANCE.between(f, 16, -2),
                Optional.empty());
    }

    /**
     * The gorget: round the neck, from under the cuirass's top up under the helmet's rim, its top rising toward the
     * nape as the rim does; moving with the neck.
     */
    private Plating.Cut gorget(final Rig.Frame f) {
        final List<Field.Element> edges = List.of(
                new Field.Element("top", Field.Plane.of(f.place(new V3(0, 1.5, 5)), f.way(new V3(0, 11.6, 6))),
                        Field.Op.CLIP, 0),
                new Field.Element("foot", Field.Plane.of(f.place(new V3(0, -1.5, 0)), f.way(new V3(0, -1, 0))),
                        Field.Op.CLIP, 0));
        return new Plating.Cut(new Wearables.Gorget(), Part.NECK, GORGET_OFF, THICK, FINE, CELL,
                Bounds.INSTANCE.box(f, new double[] {-12, -4, -13, 12, 10, 10}), edges,
                Optional.of(new Plating.Core(f.at(), f.way(V3.Y), Part.NECK)));
    }

    /**
     * A gauntlet over {@code hand}: the whole hand as it hangs, the fingers together in one, and a cuff over the wrist;
     * moving with the hand.
     */
    private Plating.Cut gauntlet(final Part hand, final Rig.Frame f) {
        return new Plating.Cut(new Wearables.Gauntlet(), hand, GAUNTLET_OFF, GAUNTLET_THICK, HAND, HAND,
                Bounds.INSTANCE.box(f, new double[] {-5, -21, -4, 8, 5, 8}), List.of(new Field.Element("cuff",
                Field.Plane.of(f.place(new V3(0, 3, 0)), f.way(V3.Y)), Field.Op.CLIP, 0)), Optional.empty());
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
