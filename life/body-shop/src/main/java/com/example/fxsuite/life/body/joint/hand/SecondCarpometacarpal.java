package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Trapezoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The second metacarpal's base on its carpal: held, in this hand.
 */
public record SecondCarpometacarpal() implements Joint<Trapezoid, SecondMetacarpal> {

    public static final SecondCarpometacarpal INSTANCE = new SecondCarpometacarpal();

    @Override
    public String name() {
        return "second carpometacarpal joint";
    }

    @Override
    public Trapezoid from() {
        return Trapezoid.INSTANCE;
    }

    @Override
    public Point<Trapezoid> fromPoint() {
        return Trapezoid.Points.SECOND;
    }

    @Override
    public SecondMetacarpal to() {
        return SecondMetacarpal.INSTANCE;
    }

    @Override
    public Point<SecondMetacarpal> toPoint() {
        return SecondMetacarpal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
