package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.spine.Level;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the sternum goes from where it was drawn, the ribs measured ({@link RibLine}) and the spine curved: turned
 * about its angle and moved back or ahead as best makes three depths men's at once: the chest as deep from the back of
 * its ribs to the front of its sternum as men's, 19.5 cm (Robinson and others, 1,225 men's CT, Front Bioeng Biotechnol
 * 2024, 12:1432911), taken as men of Holcombe's 177.9 cm, the same Michigan CT; and its back as far ahead of the
 * vertebral bodies level with its notch and its angle as men's ({@link #NOTCH_DEPTH}, {@link #ANGLE_DEPTH}), which
 * leans its top back toward the spine as far as men's is; then moved as far up as the vertebrae it lies level with in
 * men's CT ask, its notch at the second and third's disc, its angle at the fourth and fifth's (Razzouk and others,
 * 1,035 young adults, J Clin Neurosci 2023, 118:46), where it meets the xiphoid at the tenth, men's most often (Keough
 * and others, Clin Anat 2016, 29:1018).
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
    /**
     * How far the sternum's back is ahead of the vertebral bodies' fronts in men, cm: level with its notch, 5.2 (Dogan
     * and others, 55 males by CT, Med Records 2024, 6:342, aged up to 50, so some boys among them, the depth counted
     * half); level with its angle, 6.0 (Syed Naziya and others, 30 men by CT, Int J Health Sci Res 2014, 4:111);
     * their men taken as 172 cm; how thick the manubrium is, cm, its front to its back.
     */
    private static final double NOTCH_DEPTH = 5.23;
    private static final double ANGLE_DEPTH = 5.97;
    private static final double SAMPLED = 172;
    private static final double CHILDREN = 0.5;
    private static final double MANUBRIUM = 1.1;
    /** How far it is turned at most either way, in looking for how far, radians. */
    private static final double TILTS = 0.6;

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

    /**
     * How the sternum is moved: turned and moved back or ahead as best makes three depths men's at once, the chest from
     * the back of its ribs to the front of its sternum at its deepest, and the sternum's back to its vertebrae's
     * bodies' fronts level with its notch and with its angle; then up as far as the vertebrae it lies level with ask.
     */
    public Move fit() {
        double rear = Double.MAX_VALUE;
        for (final Rib rib : Rib.values()) {
            for (final V3 p : RibLine.INSTANCE.line(rib, 0)) {
                rear = Math.min(rear, p.z() - RIB);
            }
        }
        final double scale = Proportions.DRAWN.stature();
        // where each of the three depths' fronts should be, and how much each counts
        final double[] want = {rear + DEEP * scale / MEN,
            front(Level.T2, Level.T3) + (NOTCH_DEPTH + MANUBRIUM) * scale / SAMPLED,
            front(Level.T4, Level.T5) + (ANGLE_DEPTH + MANUBRIUM) * scale / SAMPLED};
        final double[] weight = {1, CHILDREN, 1};
        double best = Double.MAX_VALUE;
        Move fitted = null;
        for (double tilt = -TILTS; tilt <= TILTS; tilt += TILTS / 1000) {
            final Move turned = new Move(tilt, V3.ZERO);
            final double[] at = {turned.moved(new V3(0, FRONT_HIGH, FRONT)).z(),
                turned.moved(new V3(0, NOTCH, NOTCH_AHEAD)).z(), turned.moved(new V3(0, ANGLE, ANGLE_AHEAD)).z()};
            double ahead = 0;
            double sum = 0;
            for (int i = 0; i < 3; i++) {
                ahead += weight[i] * (want[i] - at[i]);
                sum += weight[i];
            }
            ahead /= sum;
            double miss = 0;
            for (int i = 0; i < 3; i++) {
                miss += weight[i] * Math.pow(at[i] + ahead - want[i], 2);
            }
            if (miss < best) {
                best = miss;
                fitted = new Move(tilt, new V3(0, 0, ahead));
            }
        }
        return new Move(fitted.tilt(), new V3(0, up(fitted), fitted.shift().z()));
    }

    /** How far up the sternum turned as {@code turned} is goes to lie level with its vertebrae. */
    private double up(final Move turned) {
        final Move t = new Move(turned.tilt(), V3.ZERO);
        final double notch = (Level.T2.middle().y() + Level.T3.middle().y()) / 2
                - t.moved(new V3(0, NOTCH, NOTCH_AHEAD)).y();
        final double angle = (Level.T4.middle().y() + Level.T5.middle().y()) / 2
                - t.moved(new V3(0, ANGLE, ANGLE_AHEAD)).y();
        final double foot = Level.T10.middle().y() - t.moved(new V3(0, FOOT, FOOT_AHEAD)).y();
        return (notch + angle + foot) / 3;
    }

    /** How far ahead the fronts of the bodies of {@code a} and {@code b} are, midway. */
    private double front(final Level a, final Level b) {
        return (a.middle().z() + a.deep() + b.middle().z() + b.deep()) / 2;
    }
}
