package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.LateralCuneiform;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The third metatarsal's base on its tarsal: held.
 */
public record ThirdTarsometatarsal() implements Joint<LateralCuneiform, ThirdMetatarsal> {

    public static final ThirdTarsometatarsal INSTANCE = new ThirdTarsometatarsal();

    @Override
    public String name() {
        return "third tarsometatarsal joint";
    }

    @Override
    public LateralCuneiform from() {
        return LateralCuneiform.INSTANCE;
    }

    @Override
    public Point<LateralCuneiform> fromPoint() {
        return LateralCuneiform.Points.THIRD;
    }

    @Override
    public ThirdMetatarsal to() {
        return ThirdMetatarsal.INSTANCE;
    }

    @Override
    public Point<ThirdMetatarsal> toPoint() {
        return ThirdMetatarsal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
