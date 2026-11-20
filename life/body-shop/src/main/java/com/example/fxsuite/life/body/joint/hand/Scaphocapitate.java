package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.Scaphoid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The capitate beside the scaphoid: held.
 */
public record Scaphocapitate() implements Joint<Scaphoid, Capitate> {

    public static final Scaphocapitate INSTANCE = new Scaphocapitate();

    @Override
    public String name() {
        return "scaphocapitate joint";
    }

    @Override
    public Scaphoid from() {
        return Scaphoid.INSTANCE;
    }

    @Override
    public Point<Scaphoid> fromPoint() {
        return Scaphoid.Points.CAPITATE;
    }

    @Override
    public Capitate to() {
        return Capitate.INSTANCE;
    }

    @Override
    public Point<Capitate> toPoint() {
        return Capitate.Points.SCAPHOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
