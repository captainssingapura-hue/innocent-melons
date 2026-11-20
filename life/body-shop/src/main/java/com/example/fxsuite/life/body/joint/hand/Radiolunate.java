package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.hand.Lunate;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The lunate under the radius's foot, turning about the same.
 */
public record Radiolunate() implements Joint<Radius, Lunate> {

    public static final Radiolunate INSTANCE = new Radiolunate();

    @Override
    public String name() {
        return "radiolunate joint";
    }

    @Override
    public Radius from() {
        return Radius.INSTANCE;
    }

    @Override
    public Point<Radius> fromPoint() {
        return Radius.Points.WRIST;
    }

    @Override
    public Lunate to() {
        return Lunate.INSTANCE;
    }

    @Override
    public Point<Lunate> toPoint() {
        return Lunate.Points.RADIUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-70, 80), new Range(-35, 20));
    }
}
