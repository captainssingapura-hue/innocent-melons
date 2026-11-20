package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.RingFingerMiddlePhalanx;
import com.example.fxsuite.life.body.bone.hand.RingFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ring finger's middle phalanx on its proximal: bending as far as 100 degrees.
 */
public record RingFingerProximalInterphalangeal()
        implements Simple<RingFingerProximalPhalanx, RingFingerMiddlePhalanx> {

    public static final RingFingerProximalInterphalangeal INSTANCE = new RingFingerProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the ring finger";
    }

    @Override
    public RingFingerProximalPhalanx from() {
        return RingFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<RingFingerProximalPhalanx> fromPoint() {
        return RingFingerProximalPhalanx.Points.HEAD;
    }

    @Override
    public RingFingerMiddlePhalanx to() {
        return RingFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<RingFingerMiddlePhalanx> toPoint() {
        return RingFingerMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 100));
    }
}
