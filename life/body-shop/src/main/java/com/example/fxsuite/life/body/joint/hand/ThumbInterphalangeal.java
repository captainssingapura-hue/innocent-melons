package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.ThumbDistalPhalanx;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The thumb's two phalanges: bending as far as 80 degrees and back 20.
 */
public record ThumbInterphalangeal() implements Simple<ThumbProximalPhalanx, ThumbDistalPhalanx> {

    public static final ThumbInterphalangeal INSTANCE = new ThumbInterphalangeal();

    @Override
    public String name() {
        return "interphalangeal joint of the thumb";
    }

    @Override
    public ThumbProximalPhalanx from() {
        return ThumbProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThumbProximalPhalanx> fromPoint() {
        return ThumbProximalPhalanx.Points.HEAD;
    }

    @Override
    public ThumbDistalPhalanx to() {
        return ThumbDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThumbDistalPhalanx> toPoint() {
        return ThumbDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-20, 80));
    }
}
