package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.bone.hand.Triquetrum;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The hamate beyond the triquetrum: held.
 */
public record Triquetrohamate() implements Joint<Triquetrum, Hamate> {

    public static final Triquetrohamate INSTANCE = new Triquetrohamate();

    @Override
    public String name() {
        return "triquetrohamate joint";
    }

    @Override
    public Triquetrum from() {
        return Triquetrum.INSTANCE;
    }

    @Override
    public Point<Triquetrum> fromPoint() {
        return Triquetrum.Points.HAMATE;
    }

    @Override
    public Hamate to() {
        return Hamate.INSTANCE;
    }

    @Override
    public Point<Hamate> toPoint() {
        return Hamate.Points.TRIQUETRUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
