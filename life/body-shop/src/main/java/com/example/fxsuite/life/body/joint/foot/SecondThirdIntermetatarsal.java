package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The third metatarsal's base beside the second's: held.
 */
public record SecondThirdIntermetatarsal() implements Joint<SecondMetatarsal, ThirdMetatarsal> {

    public static final SecondThirdIntermetatarsal INSTANCE = new SecondThirdIntermetatarsal();

    @Override
    public String name() {
        return "second to third intermetatarsal joint";
    }

    @Override
    public SecondMetatarsal from() {
        return SecondMetatarsal.INSTANCE;
    }

    @Override
    public Point<SecondMetatarsal> fromPoint() {
        return SecondMetatarsal.Points.NEXT;
    }

    @Override
    public ThirdMetatarsal to() {
        return ThirdMetatarsal.INSTANCE;
    }

    @Override
    public Point<ThirdMetatarsal> toPoint() {
        return ThirdMetatarsal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
