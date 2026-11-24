package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
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
 * The chest as its ribs are measured ({@link RibLine}), hung from the spine as it is curved, from as it was drawn by
 * eye ({@link RibAsDrawn}) on the spine as it was drawn: the sternum turned and moved to meet them
 * ({@link SternumFit}); the clavicles' inner ends moved with it, their outer ends as far, the shoulder girdle hung
 * from them moved with them ({@link #girdle}). What was drawn on the ribs, the sternum, their cartilages and the
 * clavicles bent smoothly ({@link ThinPlate}) from as drawn to as measured, each rib along its length, the sternum
 * moved, the vertebrae moved as the spine is curved, the clavicles' outer ends moved with the girdle. Found once.
 */
public enum ThoraxForm {
    INSTANCE;

    /** How many points along each rib it is bent through. */
    private static final int ALONG = 30;

    private final SternumFit.Move sternum;
    private final Bend bend;

    ThoraxForm() {
        sternum = SternumFit.INSTANCE.fit();
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
                pair(drawn, bent, l.drawn(), l.middle());
                pair(drawn, bent, l.drawnTransverseTip(), l.transverseTip());
            }
        }
        bend = new Bend(ThinPlate.INSTANCE.through(drawn, bent), ThinPlate.INSTANCE.through(bent, drawn));
    }

    /**
     * The sternum's corners as drawn, moved; the clavicles' inner ends with it, their outer ends as far, with the
     * girdle; inside the spine's canal at three levels, moved as it is curved.
     */
    private void front(final List<V3> drawn, final List<V3> bent) {
        for (final V3 p : List.of(new V3(2.7, 45.6, 6.6), new V3(1.5, 40.9, 7.4), new V3(1.85, 35.5, 8.4),
                new V3(1.5, 31.1, 9.2), new V3(0, 28.6, 9.0), new V3(0, 46.2, 6.6), ArmContacts.STERNAL)) {
            pair(drawn, bent, p, sternum.moved(p));
        }
        for (final V3 p : List.of(ArmContacts.ACROMIAL, new V3(13.4, 47.0, 3.4))) {
            pair(drawn, bent, p, p.plus(clavicle()));
        }
        final V3 canal = new V3(0, 0, -2);
        for (final Level l : List.of(Level.T1, Level.T6, Level.T12)) {
            pair(drawn, bent, l.drawn().plus(canal), l.middle().plus(canal));
        }
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

    /** How the sternum is moved from where it was drawn. */
    public SternumFit.Move sternum() {
        return sternum;
    }

    /**
     * How far the clavicle is moved from where it was drawn, on the right, both its ends: as far as its inner end is,
     * on the sternum, so it stays as raised and as swept back as it was drawn.
     */
    public V3 clavicle() {
        return sternum.moved(ArmContacts.STERNAL).minus(ArmContacts.STERNAL);
    }

    /**
     * How far the rest of the shoulder girdle is moved from where it was drawn, on the right, its scapula, the shoulder
     * joint and the arm beyond: back as far as the clavicle is, not up; the scapula had been drawn as high above the
     * clavicle's end as the sternum's top now rises, its acromion that much over the clavicle's, where the two meet
     * level.
     */
    public V3 girdle() {
        return new V3(0, 0, clavicle().z());
    }

    /**
     * Where {@code bone}, drawn in the body's frame at rest as its part was first drawn, is drawn from in its part, its
     * part's joint now {@code at}: the girdle's bones not bent with the chest, the scapula, from where the girdle was
     * before it was moved, so they go with it; every other from {@code at}.
     */
    public V3 drawnFrom(final Bone bone, final V3 at) {
        final boolean girdle = bone.part() == Part.RIGHT_GIRDLE || bone.part() == Part.LEFT_GIRDLE;
        return girdle && !bends(bone) ? at.minus(girdle()) : at;
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
