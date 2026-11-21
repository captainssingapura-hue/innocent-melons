package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The trapezium beyond the scaphoid: held.
 */
public record Scaphotrapezial() implements Joint<Scaphoid, Trapezium> {

    public static final Scaphotrapezial INSTANCE = new Scaphotrapezial();

    @Override
    public String name() {
        return "scaphotrapezial joint";
    }

    @Override
    public Scaphoid from() {
        return Scaphoid.INSTANCE;
    }

    @Override
    public Point<Scaphoid> fromPoint() {
        return Scaphoid.Points.TRAPEZIUM;
    }

    @Override
    public Trapezium to() {
        return Trapezium.INSTANCE;
    }

    @Override
    public Point<Trapezium> toPoint() {
        return Trapezium.Points.SCAPHOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
