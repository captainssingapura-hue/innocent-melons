package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.Trapezoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The capitate beside the trapezoid: held.
 */
public record Trapezocapitate() implements Joint<Trapezoid, Capitate> {

    public static final Trapezocapitate INSTANCE = new Trapezocapitate();

    @Override
    public String name() {
        return "trapezocapitate joint";
    }

    @Override
    public Trapezoid from() {
        return Trapezoid.INSTANCE;
    }

    @Override
    public Point<Trapezoid> fromPoint() {
        return Trapezoid.Points.CAPITATE;
    }

    @Override
    public Capitate to() {
        return Capitate.INSTANCE;
    }

    @Override
    public Point<Capitate> toPoint() {
        return Capitate.Points.TRAPEZOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
