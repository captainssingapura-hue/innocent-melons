package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.IntermediateCuneiform;
import com.example.fxsuite.life.body.bone.foot.LateralCuneiform;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The lateral cuneiform beside the intermediate: held.
 */
public record IntermediateLateral() implements Joint<IntermediateCuneiform, LateralCuneiform> {

    public static final IntermediateLateral INSTANCE = new IntermediateLateral();

    @Override
    public String name() {
        return "intermediate to lateral cuneiform";
    }

    @Override
    public IntermediateCuneiform from() {
        return IntermediateCuneiform.INSTANCE;
    }

    @Override
    public Point<IntermediateCuneiform> fromPoint() {
        return IntermediateCuneiform.Points.LATERAL;
    }

    @Override
    public LateralCuneiform to() {
        return LateralCuneiform.INSTANCE;
    }

    @Override
    public Point<LateralCuneiform> toPoint() {
        return LateralCuneiform.Points.INTERMEDIATE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
