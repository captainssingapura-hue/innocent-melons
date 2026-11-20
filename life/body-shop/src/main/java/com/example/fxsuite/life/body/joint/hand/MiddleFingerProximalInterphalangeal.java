package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.MiddleFingerMiddlePhalanx;
import com.example.fxsuite.life.body.bone.hand.MiddleFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The middle finger's middle phalanx on its proximal: bending as far as 100 degrees.
 */
public record MiddleFingerProximalInterphalangeal()
        implements Simple<MiddleFingerProximalPhalanx, MiddleFingerMiddlePhalanx> {

    public static final MiddleFingerProximalInterphalangeal INSTANCE = new MiddleFingerProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the middle finger";
    }

    @Override
    public MiddleFingerProximalPhalanx from() {
        return MiddleFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<MiddleFingerProximalPhalanx> fromPoint() {
        return MiddleFingerProximalPhalanx.Points.HEAD;
    }

    @Override
    public MiddleFingerMiddlePhalanx to() {
        return MiddleFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<MiddleFingerMiddlePhalanx> toPoint() {
        return MiddleFingerMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 100));
    }
}
