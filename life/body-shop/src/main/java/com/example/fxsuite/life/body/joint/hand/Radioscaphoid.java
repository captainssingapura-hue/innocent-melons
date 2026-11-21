package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The scaphoid under the radius's foot, turning about the capitate's head.
 */
public record Radioscaphoid() implements Joint<Radius, Scaphoid> {

    public static final Radioscaphoid INSTANCE = new Radioscaphoid();

    @Override
    public String name() {
        return "radioscaphoid joint";
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
    public Scaphoid to() {
        return Scaphoid.INSTANCE;
    }

    @Override
    public Point<Scaphoid> toPoint() {
        return Scaphoid.Points.RADIUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-70, 80), new Range(-35, 20));
    }
}
