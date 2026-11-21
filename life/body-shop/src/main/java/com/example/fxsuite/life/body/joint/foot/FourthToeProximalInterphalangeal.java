package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FourthToeMiddlePhalanx;
import com.example.fxsuite.life.body.bone.foot.FourthToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fourth toe's middle phalanx on its proximal: curling down as far as 70 degrees.
 */
public record FourthToeProximalInterphalangeal() implements Simple<FourthToeProximalPhalanx, FourthToeMiddlePhalanx> {

    public static final FourthToeProximalInterphalangeal INSTANCE = new FourthToeProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the fourth toe";
    }

    @Override
    public FourthToeProximalPhalanx from() {
        return FourthToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<FourthToeProximalPhalanx> fromPoint() {
        return FourthToeProximalPhalanx.Points.HEAD;
    }

    @Override
    public FourthToeMiddlePhalanx to() {
        return FourthToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<FourthToeMiddlePhalanx> toPoint() {
        return FourthToeMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 70));
    }
}
