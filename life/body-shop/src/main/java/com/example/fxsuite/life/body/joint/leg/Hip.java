package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The femur's head in the acetabulum, a ball in its socket: flexing forward as far as 120 degrees and back 20, out to
 * the side 45 and in 25, turning in 40 and out 45.
 */
public record Hip() implements Simple<HipBone, Femur> {

    public static final Hip INSTANCE = new Hip();

    @Override
    public String name() {
        return "hip joint";
    }

    @Override
    public HipBone from() {
        return HipBone.INSTANCE;
    }

    @Override
    public Point<HipBone> fromPoint() {
        return HipBone.Points.ACETABULUM;
    }

    @Override
    public Femur to() {
        return Femur.INSTANCE;
    }

    @Override
    public Point<Femur> toPoint() {
        return Femur.Points.HEAD;
    }

    @Override
    public Motion motion() {
        return new Motion.Ball(new Range(-20, 120), new Range(-25, 45), new Range(-40, 45));
    }
}
