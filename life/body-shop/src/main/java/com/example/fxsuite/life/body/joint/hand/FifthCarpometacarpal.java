package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fifth metacarpal's base on its carpal: held, in this hand.
 */
public record FifthCarpometacarpal() implements Joint<Hamate, FifthMetacarpal> {

    public static final FifthCarpometacarpal INSTANCE = new FifthCarpometacarpal();

    @Override
    public String name() {
        return "fifth carpometacarpal joint";
    }

    @Override
    public Hamate from() {
        return Hamate.INSTANCE;
    }

    @Override
    public Point<Hamate> fromPoint() {
        return Hamate.Points.FIFTH;
    }

    @Override
    public FifthMetacarpal to() {
        return FifthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FifthMetacarpal> toPoint() {
        return FifthMetacarpal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
