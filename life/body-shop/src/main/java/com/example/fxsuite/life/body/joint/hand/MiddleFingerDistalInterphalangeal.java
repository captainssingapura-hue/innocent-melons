package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.MiddleFingerDistalPhalanx;
import com.example.fxsuite.life.body.bone.hand.MiddleFingerMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The middle finger's distal phalanx on its middle: bending as far as 80 degrees.
 */
public record MiddleFingerDistalInterphalangeal()
        implements Simple<MiddleFingerMiddlePhalanx, MiddleFingerDistalPhalanx> {

    public static final MiddleFingerDistalInterphalangeal INSTANCE = new MiddleFingerDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the middle finger";
    }

    @Override
    public MiddleFingerMiddlePhalanx from() {
        return MiddleFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<MiddleFingerMiddlePhalanx> fromPoint() {
        return MiddleFingerMiddlePhalanx.Points.HEAD;
    }

    @Override
    public MiddleFingerDistalPhalanx to() {
        return MiddleFingerDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<MiddleFingerDistalPhalanx> toPoint() {
        return MiddleFingerDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 80));
    }
}
