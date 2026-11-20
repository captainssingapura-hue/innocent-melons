package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The hamate beside the capitate: held.
 */
public record Capitohamate() implements Joint<Capitate, Hamate> {

    public static final Capitohamate INSTANCE = new Capitohamate();

    @Override
    public String name() {
        return "capitohamate joint";
    }

    @Override
    public Capitate from() {
        return Capitate.INSTANCE;
    }

    @Override
    public Point<Capitate> fromPoint() {
        return Capitate.Points.HAMATE;
    }

    @Override
    public Hamate to() {
        return Hamate.INSTANCE;
    }

    @Override
    public Point<Hamate> toPoint() {
        return Hamate.Points.CAPITATE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
