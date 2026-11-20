package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.SecondToeDistalPhalanx;
import com.example.fxsuite.life.body.bone.foot.SecondToeMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The second toe's distal phalanx on its middle: curling down as far as 60 degrees.
 */
public record SecondToeDistalInterphalangeal() implements Simple<SecondToeMiddlePhalanx, SecondToeDistalPhalanx> {

    public static final SecondToeDistalInterphalangeal INSTANCE = new SecondToeDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the second toe";
    }

    @Override
    public SecondToeMiddlePhalanx from() {
        return SecondToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<SecondToeMiddlePhalanx> fromPoint() {
        return SecondToeMiddlePhalanx.Points.HEAD;
    }

    @Override
    public SecondToeDistalPhalanx to() {
        return SecondToeDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<SecondToeDistalPhalanx> toPoint() {
        return SecondToeDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 60));
    }
}
