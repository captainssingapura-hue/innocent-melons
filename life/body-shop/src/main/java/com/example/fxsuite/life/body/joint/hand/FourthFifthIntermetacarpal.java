package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fifth metacarpal's base beside the fourth's: held.
 */
public record FourthFifthIntermetacarpal() implements Joint<FourthMetacarpal, FifthMetacarpal> {

    public static final FourthFifthIntermetacarpal INSTANCE = new FourthFifthIntermetacarpal();

    @Override
    public String name() {
        return "fourth to fifth intermetacarpal joint";
    }

    @Override
    public FourthMetacarpal from() {
        return FourthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FourthMetacarpal> fromPoint() {
        return FourthMetacarpal.Points.NEXT;
    }

    @Override
    public FifthMetacarpal to() {
        return FifthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FifthMetacarpal> toPoint() {
        return FifthMetacarpal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
