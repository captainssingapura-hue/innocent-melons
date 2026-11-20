package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.bone.hand.Trapezoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The trapezoid beside the trapezium: held.
 */
public record Trapeziotrapezoid() implements Joint<Trapezium, Trapezoid> {

    public static final Trapeziotrapezoid INSTANCE = new Trapeziotrapezoid();

    @Override
    public String name() {
        return "trapeziotrapezoid joint";
    }

    @Override
    public Trapezium from() {
        return Trapezium.INSTANCE;
    }

    @Override
    public Point<Trapezium> fromPoint() {
        return Trapezium.Points.TRAPEZOID;
    }

    @Override
    public Trapezoid to() {
        return Trapezoid.INSTANCE;
    }

    @Override
    public Point<Trapezoid> toPoint() {
        return Trapezoid.Points.TRAPEZIUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
