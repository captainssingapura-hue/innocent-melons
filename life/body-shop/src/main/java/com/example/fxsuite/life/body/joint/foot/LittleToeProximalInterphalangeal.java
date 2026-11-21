package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.LittleToeMiddlePhalanx;
import com.example.fxsuite.life.body.bone.foot.LittleToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little toe's middle phalanx on its proximal: curling down as far as 70 degrees.
 */
public record LittleToeProximalInterphalangeal() implements Simple<LittleToeProximalPhalanx, LittleToeMiddlePhalanx> {

    public static final LittleToeProximalInterphalangeal INSTANCE = new LittleToeProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the little toe";
    }

    @Override
    public LittleToeProximalPhalanx from() {
        return LittleToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleToeProximalPhalanx> fromPoint() {
        return LittleToeProximalPhalanx.Points.HEAD;
    }

    @Override
    public LittleToeMiddlePhalanx to() {
        return LittleToeMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<LittleToeMiddlePhalanx> toPoint() {
        return LittleToeMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 70));
    }
}
