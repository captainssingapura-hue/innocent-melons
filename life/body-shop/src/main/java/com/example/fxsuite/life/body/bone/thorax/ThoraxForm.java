package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.ThinPlate;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.Bend;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.bone.arm.ArmContacts;
import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.spine.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * The chest as its ribs are measured ({@link RibLine}), from as it was drawn by eye ({@link RibAsDrawn}): the sternum
 * moved as far up as the vertebrae it lies level with in men's CT ask, its notch at the second and third's disc, its
 * angle at the fourth and fifth's (Razzouk and others, 1,035 young adults, J Clin Neurosci 2023, 118:46), where it
 * meets the xiphoid at the tenth, men's most often (Keough and others, Clin Anat 2016, 29:1018); and as far back as
 * makes the chest as deep from the back of its ribs to the front of its sternum as men's, 19.5 cm (Robinson and
 * others, 1,225 men's CT, Front Bioeng Biotechnol 2024, 12:1432911), taken as men of Holcombe's 177.9 cm, the same
 * Michigan CT; the clavicles' inner ends moved with it, their outer ends where they were. What was drawn on the ribs,
 * the sternum, their cartilages and the clavicles bent smoothly ({@link ThinPlate}) from as drawn to as measured, each
 * rib along its length, the sternum moved, the vertebrae and the clavicles' outer ends held. Found once.
 */
public enum ThoraxForm {
    INSTANCE;

    /** How thick a rib is, half, cm; how deep the chest is in men, cm, and how tall they are, cm. */
    private static final double RIB = 0.72;
    private static final double DEEP = 19.53;
    private static final double MEN = 177.9;
    /** How far ahead the sternum's front was drawn, cm; its notch, angle and foot, how high. */
    private static final double FRONT = 9.65;
    private static final double NOTCH = 45.35;
    private static final double ANGLE = 40.9;
    private static final double FOOT = 31.05;
    /** How many points along each rib it is bent through. */
    private static final int ALONG = 30;

    private final V3 sternum;
    private final Bend bend;

    ThoraxForm() {
        sternum = new V3(0, up(), back());
        final List<V3> drawn = new ArrayList<>();
        final List<V3> bent = new ArrayList<>();
        front(drawn, bent);
        for (final Rib rib : Rib.values()) {
            final List<V3> was = spaced(rib.asDrawn().line());
            final List<V3> is = spaced(RibLine.INSTANCE.line(rib, 0));
            for (int k = 0; k <= ALONG; k++) {
                pair(drawn, bent, was.get(k), is.get(k));
            }
        }
        for (final Level l : Level.values()) {
            if (l.part() == Part.CHEST) {
                pair(drawn, bent, l.middle(), l.middle());
                pair(drawn, bent, l.transverseTip(), l.transverseTip());
            }
        }
        bend = new Bend(ThinPlate.INSTANCE.through(drawn, bent), ThinPlate.INSTANCE.through(bent, drawn));
    }

    /** How far up the sternum is moved: as far as its notch, angle and foot are, on the whole, below their levels. */
    private double up() {
        final double notch = (Level.T2.middle().y() + Level.T3.middle().y()) / 2 - NOTCH;
        final double angle = (Level.T4.middle().y() + Level.T5.middle().y()) / 2 - ANGLE;
        final double foot = Level.T10.middle().y() - FOOT;
        return (notch + angle + foot) / 3;
    }

    /** How far ahead it is moved, back if less than none: its front as far ahead of the ribs' backs as men's. */
    private double back() {
        double rear = Double.MAX_VALUE;
        for (final Rib rib : Rib.values()) {
            for (final V3 p : RibLine.INSTANCE.line(rib, 0)) {
                rear = Math.min(rear, p.z() - RIB);
            }
        }
        return rear + DEEP * Proportions.DRAWN.stature() / MEN - FRONT;
    }

    /** The sternum's corners as drawn, moved; the clavicles' inner ends with it, their outer ends held. */
    private void front(final List<V3> drawn, final List<V3> bent) {
        for (final V3 p : List.of(new V3(2.7, 45.6, 6.6), new V3(1.5, 40.9, 7.4), new V3(1.85, 35.5, 8.4),
                new V3(1.5, 31.1, 9.2), new V3(0, 28.6, 9.0), new V3(0, 46.2, 6.6), ArmContacts.STERNAL)) {
            pair(drawn, bent, p, p.plus(sternum));
        }
        for (final V3 p : List.of(ArmContacts.ACROMIAL, new V3(13.4, 47.0, 3.4), inside(Level.T1),
                inside(Level.T6), inside(Level.T12))) {
            pair(drawn, bent, p, p);
        }
    }

    /** Inside {@code l}'s canal, held: not its middle, which the ribs' bending holds too. */
    private V3 inside(final Level l) {
        return l.middle().plus(new V3(0, 0, -2));
    }

    /** {@code line}, its points as far apart along it as each other, {@link #ALONG} spans. */
    private List<V3> spaced(final List<V3> line) {
        final double[] run = new double[line.size()];
        for (int k = 1; k < line.size(); k++) {
            run[k] = run[k - 1] + line.get(k).minus(line.get(k - 1)).length();
        }
        final List<V3> out = new ArrayList<>();
        int j = 0;
        for (int k = 0; k <= ALONG; k++) {
            final double s = run[run.length - 1] * k / ALONG;
            while (j < run.length - 2 && run[j + 1] < s) {
                j++;
            }
            final double t = (s - run[j]) / Math.max(run[j + 1] - run[j], 1e-9);
            out.add(line.get(j).plus(line.get(j + 1).minus(line.get(j)).times(Math.min(t, 1))));
        }
        return out;
    }

    /** {@code a} bent to {@code b}; and on the left, if off the middle. */
    private void pair(final List<V3> drawn, final List<V3> bent, final V3 a, final V3 b) {
        drawn.add(a);
        bent.add(b);
        if (a.x() != 0) {
            drawn.add(new V3(-a.x(), a.y(), a.z()));
            bent.add(new V3(-b.x(), b.y(), b.z()));
        }
    }

    /** How far the sternum is moved from where it was drawn. */
    public V3 sternum() {
        return sternum;
    }

    /** Whether what is drawn on {@code bone} is bent with the chest: on a rib, the sternum, a cartilage, a clavicle. */
    public boolean bends(final Bone bone) {
        return bone.region() == Region.THORAX || bone == Clavicle.INSTANCE;
    }

    /** Where {@code p}, drawn on the chest in the body's frame at rest, is as the chest is measured. */
    public V3 apply(final V3 p) {
        return bend.apply(p);
    }

    /**
     * {@code drawn}, the sternum or a clavicle as drawn in the body's frame at rest less {@code origin}, its part's,
     * moved as the chest is: bent through the sternum's corners and the clavicles' ends alone, few enough to be quick.
     */
    public Field.Compound shaped(final Field.Compound drawn, final V3 origin) {
        final List<V3> was = new ArrayList<>();
        final List<V3> is = new ArrayList<>();
        front(was, is);
        was.replaceAll(p -> p.minus(origin));
        is.replaceAll(p -> p.minus(origin));
        return new Bend(ThinPlate.INSTANCE.through(was, is), ThinPlate.INSTANCE.through(is, was)).shaped(drawn);
    }
}
