package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.LittleToeDistalPhalanx;
import com.example.fxsuite.life.body.bone.foot.LittleToeMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little toe's distal phalanx on its middle: curling down as far as 60 degrees.
 */
public record LittleToeDistalInterphalangeal() implements Simple<LittleToeMiddlePhalanx, LittleToeDistalPhalanx> {

    public static final LittleToeDistalInterphalangeal INSTANCE = new LittleToeDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the little toe";
    }

    @Override
    public LittleToeMiddlePhalanx from() {
        return LittleToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<LittleToeMiddlePhalanx> fromPoint() {
        return LittleToeMiddlePhalanx.Points.HEAD;
    }

    @Override
    public LittleToeDistalPhalanx to() {
        return LittleToeDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleToeDistalPhalanx> toPoint() {
        return LittleToeDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 60));
    }
}
