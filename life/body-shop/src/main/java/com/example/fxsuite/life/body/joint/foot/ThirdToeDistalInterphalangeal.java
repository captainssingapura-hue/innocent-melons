package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.ThirdToeDistalPhalanx;
import com.example.fxsuite.life.body.bone.foot.ThirdToeMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The third toe's distal phalanx on its middle: curling down as far as 60 degrees.
 */
public record ThirdToeDistalInterphalangeal() implements Simple<ThirdToeMiddlePhalanx, ThirdToeDistalPhalanx> {

    public static final ThirdToeDistalInterphalangeal INSTANCE = new ThirdToeDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the third toe";
    }

    @Override
    public ThirdToeMiddlePhalanx from() {
        return ThirdToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<ThirdToeMiddlePhalanx> fromPoint() {
        return ThirdToeMiddlePhalanx.Points.HEAD;
    }

    @Override
    public ThirdToeDistalPhalanx to() {
        return ThirdToeDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThirdToeDistalPhalanx> toPoint() {
        return ThirdToeDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 60));
    }
}
