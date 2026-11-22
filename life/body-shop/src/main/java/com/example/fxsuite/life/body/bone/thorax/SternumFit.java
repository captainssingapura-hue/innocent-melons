package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the sternum goes from where it was drawn, the ribs measured ({@link RibLine}) and the spine curved: turned
 * about its angle as far as the ends of the ribs that reach it, the first seven, are turned, their heads moved with
 * their vertebrae, each rib turned as it was measured whatever its vertebra; then moved as far up as the vertebrae it
 * lies level with in men's CT ask, its notch at the second and third's disc, its angle at the fourth and fifth's
 * (Razzouk and others, 1,035 young adults, J Clin Neurosci 2023, 118:46), where it meets the xiphoid at the tenth,
 * men's most often (Keough and others, Clin Anat 2016, 29:1018); and as far back as makes the chest as deep from the
 * back of its ribs to the front of its sternum as men's, 19.5 cm (Robinson and others, 1,225 men's CT, Front Bioeng
 * Biotechnol 2024, 12:1432911), taken as men of Holcombe's 177.9 cm, the same Michigan CT.
 */
public record SternumFit() implements StatelessFunctionalObject {

    public static final SternumFit INSTANCE = new SternumFit();

    /** How thick a rib is, half, cm; how deep the chest is in men, cm, and how tall they are, cm. */
    private static final double RIB = 0.72;
    private static final double DEEP = 19.53;
    private static final double MEN = 177.9;
    /** How high its notch, angle and foot were drawn, and how far ahead; how far ahead its front, cm. */
    private static final double NOTCH = 45.35;
    private static final double NOTCH_AHEAD = 6.6;
    private static final double ANGLE = 40.9;
    private static final double ANGLE_AHEAD = 7.45;
    private static final double FOOT = 31.05;
    private static final double FOOT_AHEAD = 9.2;
    private static final double FRONT = 9.65;
    private static final double FRONT_HIGH = 33;
    /** How many ribs reach it. */
    private static final int REACH = 7;

    /**
     * How the sternum is moved: turned about its angle as drawn by {@code tilt}, radians, its top back if less than
     * none; then moved by {@code shift}.
     *
     * @param tilt  how far it is turned, radians
     * @param shift how far it is then moved
     */
    public record Move(double tilt, V3 shift) {

        /** Where {@code p}, drawn on the sternum, goes. */
        public V3 moved(final V3 p) {
            final V3 pivot = new V3(0, ANGLE, ANGLE_AHEAD);
            final V3 d = p.minus(pivot);
            final double c = Math.cos(tilt);
            final double s = Math.sin(tilt);
            return pivot.plus(new V3(d.x(), d.y() * c - d.z() * s, d.y() * s + d.z() * c)).plus(shift);
        }
    }

    /** How the sternum is moved. */
    public Move fit() {
        final Move turned = new Move(tilt(), V3.ZERO);
        final double notch = (Level.T2.middle().y() + Level.T3.middle().y()) / 2
                - turned.moved(new V3(0, NOTCH, NOTCH_AHEAD)).y();
        final double angle = (Level.T4.middle().y() + Level.T5.middle().y()) / 2
                - turned.moved(new V3(0, ANGLE, ANGLE_AHEAD)).y();
        final double foot = Level.T10.middle().y() - turned.moved(new V3(0, FOOT, FOOT_AHEAD)).y();
        double rear = Double.MAX_VALUE;
        for (final Rib rib : Rib.values()) {
            for (final V3 p : RibLine.INSTANCE.line(rib, 0)) {
                rear = Math.min(rear, p.z() - RIB);
            }
        }
        final double ahead = rear + DEEP * Proportions.DRAWN.stature() / MEN
                - turned.moved(new V3(0, FRONT_HIGH, FRONT)).z();
        return new Move(turned.tilt(), new V3(0, (notch + angle + foot) / 3, ahead));
    }

    /**
     * How far the ends of the ribs that reach it are turned by the spine's curving, radians: the slope, ahead for up,
     * of how far each one's head is moved ahead, as high as its end is.
     */
    private double tilt() {
        double sy = 0;
        double sz = 0;
        double syy = 0;
        double syz = 0;
        for (int k = 0; k < REACH; k++) {
            final Rib rib = Rib.values()[k];
            final double z = rib.head().minus(rib.drawnHead()).z();
            final double y = rib.end().y();
            sy += y;
            sz += z;
            syy += y * y;
            syz += y * z;
        }
        final double slope = (REACH * syz - sy * sz) / (REACH * syy - sy * sy);
        return Math.asin(Math.max(-1, Math.min(1, slope)));
    }
}
