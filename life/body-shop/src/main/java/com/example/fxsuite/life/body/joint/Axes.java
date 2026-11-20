package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.Rot;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How joints' frames are turned, in the frame of the bones they are on as the body stands at rest, each its y along the
 * bone carried and its z the way that bone flexes ({@link Movement}): upright, as the spine's and the body's own; down
 * a thigh, flexing forward; down a shin, flexing back; along a foot or a toe, flexing down.
 */
public record Axes() implements StatelessFunctionalObject {

    public static final Axes INSTANCE = new Axes();

    /** The body's own: x to the right, y up, z ahead. */
    public static final Rot UPRIGHT = Rot.NONE;
    /** Down a thigh, flexing forward: x to the left, y down, z ahead. */
    public static final Rot THIGH = new Rot(-1, 0, 0, 0, -1, 0, 0, 0, 1);
    /** Down a shin, flexing back: x to the right, y down, z behind. */
    public static final Rot SHIN = new Rot(1, 0, 0, 0, -1, 0, 0, 0, -1);
    /** Along a foot or a toe, ahead, flexing down: x to the right, y ahead, z down. */
    public static final Rot FORWARD = new Rot(1, 0, 0, 0, 0, -1, 0, 1, 0);

    /** A frame whose x is {@code x}, its y as near {@code up} as square to it allows. */
    public Rot along(final V3 x, final V3 up) {
        final V3 u = x.unit();
        final V3 y = up.minus(u.times(up.dot(u))).unit();
        final V3 z = u.cross(y);
        return new Rot(u.x(), y.x(), z.x(), u.y(), y.y(), z.y(), u.z(), y.z(), z.z());
    }
}
