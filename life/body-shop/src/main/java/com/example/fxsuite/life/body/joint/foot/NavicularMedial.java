package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.MedialCuneiform;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The medial cuneiform on the navicular: held.
 */
public record NavicularMedial() implements Joint<Navicular, MedialCuneiform> {

    public static final NavicularMedial INSTANCE = new NavicularMedial();

    @Override
    public String name() {
        return "navicular to medial cuneiform";
    }

    @Override
    public Navicular from() {
        return Navicular.INSTANCE;
    }

    @Override
    public Point<Navicular> fromPoint() {
        return Navicular.Points.MEDIAL;
    }

    @Override
    public MedialCuneiform to() {
        return MedialCuneiform.INSTANCE;
    }

    @Override
    public Point<MedialCuneiform> toPoint() {
        return MedialCuneiform.Points.NAVICULAR;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
