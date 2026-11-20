package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.ThirdToeMiddlePhalanx;
import com.example.fxsuite.life.body.bone.foot.ThirdToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The third toe's middle phalanx on its proximal: curling down as far as 70 degrees.
 */
public record ThirdToeProximalInterphalangeal() implements Simple<ThirdToeProximalPhalanx, ThirdToeMiddlePhalanx> {

    public static final ThirdToeProximalInterphalangeal INSTANCE = new ThirdToeProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the third toe";
    }

    @Override
    public ThirdToeProximalPhalanx from() {
        return ThirdToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThirdToeProximalPhalanx> fromPoint() {
        return ThirdToeProximalPhalanx.Points.HEAD;
    }

    @Override
    public ThirdToeMiddlePhalanx to() {
        return ThirdToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<ThirdToeMiddlePhalanx> toPoint() {
        return ThirdToeMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 70));
    }
}
