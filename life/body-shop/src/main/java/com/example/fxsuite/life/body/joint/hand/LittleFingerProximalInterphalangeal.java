package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.LittleFingerMiddlePhalanx;
import com.example.fxsuite.life.body.bone.hand.LittleFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little finger's middle phalanx on its proximal: bending as far as 100 degrees.
 */
public record LittleFingerProximalInterphalangeal()
        implements Simple<LittleFingerProximalPhalanx, LittleFingerMiddlePhalanx> {

    public static final LittleFingerProximalInterphalangeal INSTANCE = new LittleFingerProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the little finger";
    }

    @Override
    public LittleFingerProximalPhalanx from() {
        return LittleFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleFingerProximalPhalanx> fromPoint() {
        return LittleFingerProximalPhalanx.Points.HEAD;
    }

    @Override
    public LittleFingerMiddlePhalanx to() {
        return LittleFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<LittleFingerMiddlePhalanx> toPoint() {
        return LittleFingerMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 100));
    }
}
