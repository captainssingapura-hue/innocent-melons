package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FourthToeDistalPhalanx;
import com.example.fxsuite.life.body.bone.foot.FourthToeMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fourth toe's distal phalanx on its middle: curling down as far as 60 degrees.
 */
public record FourthToeDistalInterphalangeal() implements Simple<FourthToeMiddlePhalanx, FourthToeDistalPhalanx> {

    public static final FourthToeDistalInterphalangeal INSTANCE = new FourthToeDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the fourth toe";
    }

    @Override
    public FourthToeMiddlePhalanx from() {
        return FourthToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<FourthToeMiddlePhalanx> fromPoint() {
        return FourthToeMiddlePhalanx.Points.HEAD;
    }

    @Override
    public FourthToeDistalPhalanx to() {
        return FourthToeDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<FourthToeDistalPhalanx> toPoint() {
        return FourthToeDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 60));
    }
}
