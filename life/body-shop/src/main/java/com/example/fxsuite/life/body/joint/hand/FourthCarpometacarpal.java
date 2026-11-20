package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Hamate;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fourth metacarpal's base on its carpal: held, in this hand.
 */
public record FourthCarpometacarpal() implements Joint<Hamate, FourthMetacarpal> {

    public static final FourthCarpometacarpal INSTANCE = new FourthCarpometacarpal();

    @Override
    public String name() {
        return "fourth carpometacarpal joint";
    }

    @Override
    public Hamate from() {
        return Hamate.INSTANCE;
    }

    @Override
    public Point<Hamate> fromPoint() {
        return Hamate.Points.FOURTH;
    }

    @Override
    public FourthMetacarpal to() {
        return FourthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FourthMetacarpal> toPoint() {
        return FourthMetacarpal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
