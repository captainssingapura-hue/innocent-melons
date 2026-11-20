package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.SecondToeMiddlePhalanx;
import com.example.fxsuite.life.body.bone.foot.SecondToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The second toe's middle phalanx on its proximal: curling down as far as 70 degrees.
 */
public record SecondToeProximalInterphalangeal() implements Simple<SecondToeProximalPhalanx, SecondToeMiddlePhalanx> {

    public static final SecondToeProximalInterphalangeal INSTANCE = new SecondToeProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the second toe";
    }

    @Override
    public SecondToeProximalPhalanx from() {
        return SecondToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<SecondToeProximalPhalanx> fromPoint() {
        return SecondToeProximalPhalanx.Points.HEAD;
    }

    @Override
    public SecondToeMiddlePhalanx to() {
        return SecondToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<SecondToeMiddlePhalanx> toPoint() {
        return SecondToeMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 70));
    }
}
