package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.Lunate;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The capitate's head under the lunate: held.
 */
public record Lunocapitate() implements Joint<Lunate, Capitate> {

    public static final Lunocapitate INSTANCE = new Lunocapitate();

    @Override
    public String name() {
        return "lunocapitate joint";
    }

    @Override
    public Lunate from() {
        return Lunate.INSTANCE;
    }

    @Override
    public Point<Lunate> fromPoint() {
        return Lunate.Points.CAPITATE;
    }

    @Override
    public Capitate to() {
        return Capitate.INSTANCE;
    }

    @Override
    public Point<Capitate> toPoint() {
        return Capitate.Points.LUNATE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
