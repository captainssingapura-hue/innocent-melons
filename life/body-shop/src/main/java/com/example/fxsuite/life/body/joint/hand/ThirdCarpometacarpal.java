package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Capitate;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The third metacarpal's base on its carpal: held, in this hand.
 */
public record ThirdCarpometacarpal() implements Joint<Capitate, ThirdMetacarpal> {

    public static final ThirdCarpometacarpal INSTANCE = new ThirdCarpometacarpal();

    @Override
    public String name() {
        return "third carpometacarpal joint";
    }

    @Override
    public Capitate from() {
        return Capitate.INSTANCE;
    }

    @Override
    public Point<Capitate> fromPoint() {
        return Capitate.Points.THIRD;
    }

    @Override
    public ThirdMetacarpal to() {
        return ThirdMetacarpal.INSTANCE;
    }

    @Override
    public Point<ThirdMetacarpal> toPoint() {
        return ThirdMetacarpal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
