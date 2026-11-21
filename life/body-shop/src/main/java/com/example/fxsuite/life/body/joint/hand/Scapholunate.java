package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Lunate;
import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The lunate beside the scaphoid: held.
 */
public record Scapholunate() implements Joint<Scaphoid, Lunate> {

    public static final Scapholunate INSTANCE = new Scapholunate();

    @Override
    public String name() {
        return "scapholunate joint";
    }

    @Override
    public Scaphoid from() {
        return Scaphoid.INSTANCE;
    }

    @Override
    public Point<Scaphoid> fromPoint() {
        return Scaphoid.Points.LUNATE;
    }

    @Override
    public Lunate to() {
        return Lunate.INSTANCE;
    }

    @Override
    public Point<Lunate> toPoint() {
        return Lunate.Points.SCAPHOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
