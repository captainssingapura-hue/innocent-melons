package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.RingFingerDistalPhalanx;
import com.example.fxsuite.life.body.bone.hand.RingFingerMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ring finger's distal phalanx on its middle: bending as far as 80 degrees.
 */
public record RingFingerDistalInterphalangeal() implements Simple<RingFingerMiddlePhalanx, RingFingerDistalPhalanx> {

    public static final RingFingerDistalInterphalangeal INSTANCE = new RingFingerDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the ring finger";
    }

    @Override
    public RingFingerMiddlePhalanx from() {
        return RingFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<RingFingerMiddlePhalanx> fromPoint() {
        return RingFingerMiddlePhalanx.Points.HEAD;
    }

    @Override
    public RingFingerDistalPhalanx to() {
        return RingFingerDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<RingFingerDistalPhalanx> toPoint() {
        return RingFingerDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 80));
    }
}
