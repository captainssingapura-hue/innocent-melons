package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.bone.hand.Trapezoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The trapezoid beyond the scaphoid: held.
 */
public record Scaphotrapezoid() implements Joint<Scaphoid, Trapezoid> {

    public static final Scaphotrapezoid INSTANCE = new Scaphotrapezoid();

    @Override
    public String name() {
        return "scaphotrapezoid joint";
    }

    @Override
    public Scaphoid from() {
        return Scaphoid.INSTANCE;
    }

    @Override
    public Point<Scaphoid> fromPoint() {
        return Scaphoid.Points.TRAPEZOID;
    }

    @Override
    public Trapezoid to() {
        return Trapezoid.INSTANCE;
    }

    @Override
    public Point<Trapezoid> toPoint() {
        return Trapezoid.Points.SCAPHOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
