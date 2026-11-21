package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.MedialCuneiform;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The first metatarsal's base on its tarsal: held.
 */
public record FirstTarsometatarsal() implements Joint<MedialCuneiform, FirstMetatarsal> {

    public static final FirstTarsometatarsal INSTANCE = new FirstTarsometatarsal();

    @Override
    public String name() {
        return "first tarsometatarsal joint";
    }

    @Override
    public MedialCuneiform from() {
        return MedialCuneiform.INSTANCE;
    }

    @Override
    public Point<MedialCuneiform> fromPoint() {
        return MedialCuneiform.Points.FIRST;
    }

    @Override
    public FirstMetatarsal to() {
        return FirstMetatarsal.INSTANCE;
    }

    @Override
    public Point<FirstMetatarsal> toPoint() {
        return FirstMetatarsal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
