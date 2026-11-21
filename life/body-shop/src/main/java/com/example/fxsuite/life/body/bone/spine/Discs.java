package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Pose;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the spine's bones meet, as frames in the body at rest (x to the right, y up along the spine, z ahead): each
 * disc, midway between the middles of the bodies above and below it, up from the one to the other; the pivot the atlas
 * turns on round the axis's dens; the joint between the sacrum and the coccyx; where the skull nods on the atlas; where
 * each rib meets its vertebra. And any of them in the frame of the part a bone is drawn in.
 */
public record Discs() implements StatelessFunctionalObject {

    public static final Discs INSTANCE = new Discs();

    /** The middle of the sacrum's first body, under the fifth lumbar's disc. */
    public static final V3 S1 = new V3(0, 9.45, -3.5);
    /** The sacrum's apex, and the coccyx's top under it. */
    public static final V3 APEX = new V3(0, 1.85, -7.1);
    public static final V3 COCCYX = new V3(0, 0.85, -7.05);
    /** The joint the head nods on atop the atlas, where the papers' rig has it. */
    public static final V3 HEAD = new V3(0, 61, 0);

    /** The disc under {@code level}: between it and the level below, or the sacrum's first body under L5. */
    public Place below(final Level level) {
        final Level[] all = Level.values();
        final V3 low = level == Level.L5 ? S1 : all[level.ordinal() + 1].middle();
        return between(low, level.middle());
    }

    /** The atlas's pivot round the axis's dens: up the dens, between the two vertebrae's middles, at the dens. */
    public Place pivot() {
        final V3 low = Level.C2.middle();
        final V3 high = Level.C1.middle();
        final Place p = between(low, high);
        return new Place(new V3(0, p.at().y(), -0.9), p.turn());
    }

    /** The joint between the sacrum's apex and the coccyx. */
    public Place sacrococcygeal() {
        return between(COCCYX, APEX);
    }

    /** Where the head nods on the atlas, upright. */
    public Place head() {
        return new Place(HEAD, Rot.NONE);
    }

    /**
     * Where the rib hung from {@code level} meets it, on the right: at its head, beside the vertebra's body, turning
     * about its neck, in from its tubercle on the transverse process, its y up. Turned so, its front rises.
     */
    public Place rib(final Level level) {
        final V3 head = level.at(level.wide() / 2 + 0.25, level.high() * 0.35, -level.deep() * 0.2);
        final V3 tubercle = level.transverseTip().plus(new V3(0.15, -0.2, 0.25));
        return new Place(head, Axes.INSTANCE.along(head.minus(tubercle), V3.Y));
    }

    /** A frame midway between {@code low} and {@code high}, up from the one to the other, x to the right. */
    public Place between(final V3 low, final V3 high) {
        final V3 up = high.minus(low).unit();
        final V3 ahead = V3.X.cross(up).unit();
        final V3 right = up.cross(ahead).unit();
        return new Place(low.plus(high).times(0.5), new Rot(right.x(), up.x(), ahead.x(), right.y(), up.y(), ahead.y(),
                right.z(), up.z(), ahead.z()));
    }

    /** {@code body}, a frame in the body at rest, in the frame of part {@code p}, which stands there unturned. */
    public Place inPart(final Place body, final Part p) {
        final V3 origin = new Rig(Proportions.DRAWN, Pose.REST, false).frame(p).at();
        return new Place(body.at().minus(origin), body.turn());
    }
}
