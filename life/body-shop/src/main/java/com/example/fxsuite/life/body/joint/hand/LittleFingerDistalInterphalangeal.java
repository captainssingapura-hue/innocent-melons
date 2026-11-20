package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.LittleFingerDistalPhalanx;
import com.example.fxsuite.life.body.bone.hand.LittleFingerMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little finger's distal phalanx on its middle: bending as far as 80 degrees.
 */
public record LittleFingerDistalInterphalangeal()
        implements Simple<LittleFingerMiddlePhalanx, LittleFingerDistalPhalanx> {

    public static final LittleFingerDistalInterphalangeal INSTANCE = new LittleFingerDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the little finger";
    }

    @Override
    public LittleFingerMiddlePhalanx from() {
        return LittleFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<LittleFingerMiddlePhalanx> fromPoint() {
        return LittleFingerMiddlePhalanx.Points.HEAD;
    }

    @Override
    public LittleFingerDistalPhalanx to() {
        return LittleFingerDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleFingerDistalPhalanx> toPoint() {
        return LittleFingerDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 80));
    }
}
