package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.fat.Nearness;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A pair of riding boots, shaped over the skin of the body they are for and over whatever is worn beneath them
 * ({@link Plating}), each in three pieces that move with the parts they are on: the shaft, a tube round the shin from
 * the middle of the calf down past the ankle, with the shin, wide enough to stand round the foot's collar; the foot,
 * its upper closed round the foot from its collar over the ankle, inside the shaft's foot so that the ankle bends
 * between them, to the toes' roots, with the foot; the toe box over the toes, closed under them, with the toes. Under
 * the foot and the toes a sole, a slab as wide as the boot and a welt more, flat underneath; under the foot's back a
 * low heel ({@link Soles}). Each said in its part's own frame as the papers' figure has it, to fit any size of body.
 */
public record Boots() implements StatelessFunctionalObject {

    public static final Boots INSTANCE = new Boots();

    /** How far out each piece's outside stands from what is beneath it, and how thick its leather is, cm. */
    private static final double SHAFT_OFF = 1.3;
    private static final double UPPER_OFF = 0.8;
    private static final double TOES_OFF = 0.8;
    private static final double LEATHER = 0.4;
    /** Where the shaft's top and foot are, down the shin; where the upper's collar is, up the foot. */
    private static final double SHAFT_TOP = -20;
    private static final double SHAFT_FOOT = -40.5;
    private static final double COLLAR = 4.5;
    /** How far up the foot the toes' roots are, and how far along them the toe box begins. */
    private static final double TOES_UP = -5.4;
    private static final double ROOTS = 0.3;
    /** Where the skin under the foot is, up the foot, and under the toes, up the toes. */
    private static final double FOOT_SKIN = -6.45;
    private static final double TOES_SKIN = FOOT_SKIN - TOES_UP;
    /** How thick the tread is and how much deeper the heel; how far forward the heel comes; how far the welt stands. */
    private static final double TREAD = 1.2;
    private static final double HEEL = 1.3;
    private static final double HEEL_FRONT = 3.5;
    private static final double WELT = 0.3;
    /** How far into the foot its outline is taken, over its skin. */
    private static final double OUTLINE = 0.9;
    /** How finely the pieces' shapes are found, the toes', and how finely each is meshed, cm. */
    private static final double FINE = 0.25;
    private static final double TOES = 0.2;
    private static final double CELL = 0.2;
    /** How big the cubes the skin is looked in by are, cm. */
    private static final double LOOK = 0.6;

    /**
     * The boots made: the leather of each, the shafts, uppers and toe boxes; and the soles, under the uppers and the
     * toe boxes.
     *
     * @param leather what is of leather
     * @param soles   what is under them
     */
    public record Made(List<Plate> leather, List<Plate> soles) {
    }

    /**
     * A pair, shaped over {@code skin} as the body {@code bind} holds it, and over what is worn under them,
     * {@code under}: cloth over the skin, or hard pieces over it; standing off whichever is further out.
     */
    public Made of(final Beneath skin, final List<Beneath> under, final Rig bind) {
        final Nearness near = new Nearness(skin, LOOK);
        final List<Nearness> over = under.stream().map(u -> new Nearness(u, LOOK)).toList();
        final Plating plating = Plating.INSTANCE;
        final List<Plate> leather = new ArrayList<>();
        final List<Plate> soles = new ArrayList<>();
        for (final int side : List.of(1, -1)) {
            final Part shin = side > 0 ? Part.RIGHT_SHIN : Part.LEFT_SHIN;
            final Part foot = side > 0 ? Part.RIGHT_FOOT : Part.LEFT_FOOT;
            final Part toes = side > 0 ? Part.RIGHT_TOES : Part.LEFT_TOES;
            leather.add(plating.of(near, over, shaft(shin, bind.frame(shin))));
            final Plating.Cut upper = upper(foot, bind.frame(foot));
            final Field.Sampled underFoot = plating.shaped(near, over, upper);
            leather.add(plating.plate(upper, underFoot));
            soles.add(Soles.INSTANCE.of(sole(foot, FOOT_SKIN, UPPER_OFF, HEEL_FRONT, HEEL, -11, 14.5),
                    bind.frame(foot), underFoot));
            final Plating.Cut box = toeBox(toes, bind.frame(toes));
            final Field.Sampled underToes = plating.shaped(near, over, box);
            leather.add(plating.plate(box, underToes));
            soles.add(Soles.INSTANCE.of(sole(toes, TOES_SKIN, TOES_OFF, 0, 0, -0.8, 11), bind.frame(toes),
                    underToes));
        }
        return new Made(leather, soles);
    }

    /**
     * A hard piece worn under the boots, as what they are shaped over: its surface, each point going with its part.
     */
    public Beneath under(final Plate plate) {
        final int n = plate.mesh().points().length / 3;
        final Part[] main = new Part[n];
        java.util.Arrays.fill(main, plate.part());
        return new Beneath(plate.mesh().points(), plate.mesh().triangles(), main, new Weights[n]);
    }

    /** How far under the foot, from its skin, a boot's tread reaches, cm, in the papers' figure's: tread and all. */
    public double under() {
        return UPPER_OFF + TREAD;
    }

    /** How far under the foot, from its skin, a boot's heel reaches, cm, in the papers' figure's: its lowest. */
    public double heel() {
        return UPPER_OFF + TREAD + HEEL;
    }

    /** The shaft round {@code shin}: from the middle of the calf down past the ankle; round the shin's line. */
    private Plating.Cut shaft(final Part shin, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        return new Plating.Cut(new Wearables.Boot(), shin, SHAFT_OFF, LEATHER, FINE, CELL,
                b.box(f, new double[] {-8, SHAFT_FOOT - 2, -10, 9, SHAFT_TOP + 2, 9}),
                b.between(f, SHAFT_TOP, SHAFT_FOOT), Optional.of(new Plating.Core(f.at(), f.way(V3.Y), shin)));
    }

    /** The upper over {@code foot}: closed round it, from its collar over the ankle to the toes' roots. */
    private Plating.Cut upper(final Part foot, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        return new Plating.Cut(new Wearables.Boot(), foot, UPPER_OFF, LEATHER, FINE, CELL,
                b.box(f, new double[] {-7.5, FOOT_SKIN - UPPER_OFF - 1, -10, 8.5, COLLAR + 1.5, 15}),
                List.of(b.cut("collar", f, new V3(0, COLLAR, 0), V3.Y, 0),
                        b.cut("toes", f, new V3(0, 0, 13.6), V3.Z, 0)), Optional.empty());
    }

    /** The toe box over {@code toes}: their tops, tips and undersides, from just behind their roots. */
    private Plating.Cut toeBox(final Part toes, final Rig.Frame f) {
        final Bounds b = Bounds.INSTANCE;
        return new Plating.Cut(new Wearables.Boot(), toes, TOES_OFF, LEATHER, TOES, TOES,
                b.box(f, new double[] {-6, TOES_SKIN - TOES_OFF - 1, -3, 6, 5, 10}),
                List.of(b.cut("roots", f, new V3(0, 0, ROOTS), new V3(0, 0, -1), 0)), Optional.empty());
    }

    /**
     * The sole under {@code part}, whose skin underneath is {@code skin} up it, the leather standing {@code off} out
     * of it: its top a little inside the leather's underside, its tread under that, as wide as the leather and a welt
     * more; and a heel {@code heel} deeper again as far forward as {@code front}, if it has one; along the part from
     * {@code back} to {@code ahead}.
     */
    private Soles.Sole sole(final Part part, final double skin, final double off, final double front,
                            final double heel, final double back, final double ahead) {
        final double top = skin - off + LEATHER / 2;
        final double tread = skin - off - TREAD;
        final double under = tread - heel;
        return new Soles.Sole(part, skin + OUTLINE, top, tread, off + WELT, front, under,
                new double[] {-8, under - 0.5, back, 9, top + 0.5, ahead}, FINE, CELL);
    }
}
