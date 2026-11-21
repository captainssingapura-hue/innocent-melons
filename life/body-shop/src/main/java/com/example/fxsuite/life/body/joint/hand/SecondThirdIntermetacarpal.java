package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The third metacarpal's base beside the second's: held.
 */
public record SecondThirdIntermetacarpal() implements Joint<SecondMetacarpal, ThirdMetacarpal> {

    public static final SecondThirdIntermetacarpal INSTANCE = new SecondThirdIntermetacarpal();

    @Override
    public String name() {
        return "second to third intermetacarpal joint";
    }

    @Override
    public SecondMetacarpal from() {
        return SecondMetacarpal.INSTANCE;
    }

    @Override
    public Point<SecondMetacarpal> fromPoint() {
        return SecondMetacarpal.Points.NEXT;
    }

    @Override
    public ThirdMetacarpal to() {
        return ThirdMetacarpal.INSTANCE;
    }

    @Override
    public Point<ThirdMetacarpal> toPoint() {
        return ThirdMetacarpal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
