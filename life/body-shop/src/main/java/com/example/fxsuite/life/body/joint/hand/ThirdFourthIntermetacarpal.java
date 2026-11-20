package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fourth metacarpal's base beside the third's: held.
 */
public record ThirdFourthIntermetacarpal() implements Joint<ThirdMetacarpal, FourthMetacarpal> {

    public static final ThirdFourthIntermetacarpal INSTANCE = new ThirdFourthIntermetacarpal();

    @Override
    public String name() {
        return "third to fourth intermetacarpal joint";
    }

    @Override
    public ThirdMetacarpal from() {
        return ThirdMetacarpal.INSTANCE;
    }

    @Override
    public Point<ThirdMetacarpal> fromPoint() {
        return ThirdMetacarpal.Points.NEXT;
    }

    @Override
    public FourthMetacarpal to() {
        return FourthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FourthMetacarpal> toPoint() {
        return FourthMetacarpal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
