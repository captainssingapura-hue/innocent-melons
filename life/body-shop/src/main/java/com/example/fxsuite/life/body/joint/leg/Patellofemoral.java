package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Patella;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The kneecap on the femur's groove, riding down it as the knee bends.
 */
public record Patellofemoral() implements Joint<Femur, Patella> {

    public static final Patellofemoral INSTANCE = new Patellofemoral();

    @Override
    public String name() {
        return "patellofemoral joint";
    }

    @Override
    public Femur from() {
        return Femur.INSTANCE;
    }

    @Override
    public Point<Femur> fromPoint() {
        return Femur.Points.GROOVE;
    }

    @Override
    public Patella to() {
        return Patella.INSTANCE;
    }

    @Override
    public Point<Patella> toPoint() {
        return Patella.Points.GROOVE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 105));
    }
}
