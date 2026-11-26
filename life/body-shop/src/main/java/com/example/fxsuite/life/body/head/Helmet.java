package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Blur;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Reshaped;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A helmet over the whole head, made from the head's own bones, so that it fits any head the body has and follows its
 * brow, its cheekbones, its jaw and its chin: the skull and the jaw swollen {@link #SWELL} all round, which fills the
 * orbits and the nose's opening and smooths the bone's small shapes away; a core inside the head blended in, filling
 * the mouth and between the jaw's sides, so that the face is closed. A shell {@link #THICK} thick, open under the chin
 * and at the nape along a rim leaning back and up, the neck passing through it; a band across the eyes cut through
 * it, and a visor {@link #GLASS} thick set in the band just inside the shell. In the head's frame, as the body was
 * bound; following the head alone.
 */
public record Helmet() implements StatelessFunctionalObject {

    public static final Helmet INSTANCE = new Helmet();

    /** How far out from the bone the helmet's outside is, how thick its shell is, and its visor, cm. */
    private static final double SWELL = 2.2;
    private static final double THICK = 0.4;
    private static final double GLASS = 0.3;
    /** How far the core blends into the swollen bone, and how fine the helmet is made, cm. */
    private static final double BLEND = 1.5;
    private static final double CELL = 0.15;
    /** How high the band across the eyes is, half, cm. */
    private static final double BAND = 1.2;
    /** How finely its outside is found to be smoothed, cm; how many steps either side each value is averaged over; how
     * many times. */
    private static final double SAMPLE = 0.3;
    private static final int REACH = 3;
    private static final int PASSES = 3;
    /**
     * The neck where it comes up into the head: how broad and how deep, half, how far ahead its middle is, how far down
     * the helmet is open for it, and how much room it is given, cm.
     */
    private static final double NECK_BROAD = 5.2;
    private static final double NECK_DEEP = 6.0;
    private static final double NECK_MIDDLE = -1.0;
    private static final double NECK_FOOT = -20;
    private static final double ROOM = 0.6;

    /** Its shell and its visor, meshed. */
    public record Made(Mesher.Mesh shell, Mesher.Mesh visor) {
    }

    /** The helmet over the head as the skull now is. */
    public Made made() {
        final Field.Shape outer = outer();
        return new Made(Mesher.of(new Field(List.of(new Field.Element("shell", shell(outer), Field.Op.ADD, 0))), CELL),
                Mesher.of(new Field(List.of(new Field.Element("visor", visor(outer), Field.Op.ADD, 0))), CELL));
    }

    /**
     * Its outside: the head's bones swollen, and a core inside the head from the vault's middle down between the
     * jaw's angles to behind the chin, blended in.
     */
    public Field.Shape outer() {
        final SkullForm s = SkullForm.INSTANCE;
        final V3 side = s.apply(Landmark.EURYON.drawn());
        final V3 angle = s.apply(Landmark.GONION.drawn());
        final V3 chin = s.apply(Landmark.GNATHION.drawn());
        final Field.Shape bones = new Reshaped(PartBones.INSTANCE.shape(Part.HEAD), V3.ZERO, V3.ZERO, Rot.NONE,
                new V3(1, 1, 1), SWELL);
        final V3 middle = new V3(0, (side.y() + chin.y()) / 2, (angle.z() + chin.z()) / 2 - 1);
        final Field.Shape core = Field.Ellipsoid.of(middle, new V3(angle.x(), 0, 0),
                new V3(0, side.y() - middle.y(), 0), new V3(0, 0, chin.z() - middle.z()));
        final V3 top = s.apply(Landmark.BREGMA.drawn());
        final V3 brow = s.apply(Landmark.GLABELLA.drawn());
        final V3 back = s.apply(Landmark.OPISTHOCRANION.drawn());
        final V3 vault = new V3(0, side.y(), (brow.z() + back.z()) / 2);
        final Field.Shape inside = Field.Ellipsoid.of(vault, new V3(side.x() - 1, 0, 0),
                new V3(0, top.y() - 1 - vault.y(), 0), new V3(0, 0, (brow.z() - back.z()) / 2 - 1));
        return smoothed(Field.Compound.of(List.of(new Field.Element("bones", bones, Field.Op.ADD, 0),
                new Field.Element("core", core, Field.Op.ADD, BLEND),
                new Field.Element("vault", inside, Field.Op.ADD, BLEND))));
    }

    /**
     * {@code shape} with its small shapes smoothed away: found on a grid {@link #SAMPLE} apart, each value then the
     * average of those round it, {@link #REACH} either way along each axis in turn, {@link #PASSES} times over
     * ({@link Blur}); its large shape kept.
     */
    private Field.Shape smoothed(final Field.Shape shape) {
        return Blur.INSTANCE.smoothed(Field.Sampled.of(shape, SAMPLE), REACH, PASSES);
    }

    /** Its shell: its outside, {@code outer}, hollowed, the band across the eyes cut through it, open at the neck. */
    public Field.Shape shell(final Field.Shape outer) {
        return Field.Compound.of(List.of(new Field.Element("outside", outer, Field.Op.ADD, 0),
                new Field.Element("inside", in(outer, THICK), Field.Op.CARVE, 0),
                new Field.Element("band", band(BAND), Field.Op.CARVE, 0.2),
                new Field.Element("neck", neck(), Field.Op.CARVE, 0.5),
                new Field.Element("rim", rim(), Field.Op.CLIP, 0)));
    }

    /** Its visor: a thin piece of its outside's form, {@code outer}, set in the band, just inside it. */
    public Field.Shape visor(final Field.Shape outer) {
        return Field.Compound.of(List.of(new Field.Element("visor", in(outer, (THICK - GLASS) / 2), Field.Op.ADD, 0),
                new Field.Element("behind", in(outer, (THICK + GLASS) / 2), Field.Op.CARVE, 0),
                new Field.Element("band", band(BAND + 0.3), Field.Op.CLIP, 0)));
    }

    /** {@code shape} made smaller all round by {@code by}, cm. */
    private Field.Shape in(final Field.Shape shape, final double by) {
        return new Reshaped(shape, V3.ZERO, V3.ZERO, Rot.NONE, new V3(1, 1, 1), -by);
    }

    /** The band across the eyes, {@code half} high either side of them, round the front of the head to the cheeks. */
    private Field.Shape band(final double half) {
        final V3 eye = SkullUnder.INSTANCE.eyeOnSkull();
        return Field.Ellipsoid.of(new V3(0, eye.y(), eye.z() + 4), new V3(eye.x() + 4.5, 0, 0), new V3(0, half, 0),
                new V3(0, 0, 7));
    }

    /**
     * Where the neck comes up into it: an oval as broad and as deep as a man's neck and {@link #ROOM} more, from below
     * up to just under the jaw's angles, which it leaves whole, the chin ahead of it.
     */
    private Field.Shape neck() {
        final V3 angle = SkullForm.INSTANCE.apply(Landmark.GONION.drawn());
        final double top = angle.y() + 0.3;
        final double half = (top - NECK_FOOT) / 2;
        final Field.Shape round = Field.Cylinder.of(new V3(0, NECK_FOOT + half, NECK_MIDDLE), V3.Y, NECK_DEEP + ROOM,
                half, 1);
        return Field.Squash.of(round, new V3(0, 0, NECK_MIDDLE), V3.X, (NECK_BROAD + ROOM) / (NECK_DEEP + ROOM));
    }

    /** Above the rim: under the chin at the front, rising to the nape behind. */
    private Field.Shape rim() {
        final V3 chin = SkullForm.INSTANCE.apply(Landmark.GNATHION.drawn());
        final V3 front = new V3(0, chin.y() - 2.4, chin.z());
        final V3 nape = new V3(0, -3.5, -9);
        final V3 up = nape.minus(front);
        return Field.Plane.of(front, new V3(0, -up.z(), up.y()).times(-1));
    }
}
