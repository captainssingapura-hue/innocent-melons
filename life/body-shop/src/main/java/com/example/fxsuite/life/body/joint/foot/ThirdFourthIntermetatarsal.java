package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fourth metatarsal's base beside the third's: held.
 */
public record ThirdFourthIntermetatarsal() implements Joint<ThirdMetatarsal, FourthMetatarsal> {

    public static final ThirdFourthIntermetatarsal INSTANCE = new ThirdFourthIntermetatarsal();

    @Override
    public String name() {
        return "third to fourth intermetatarsal joint";
    }

    @Override
    public ThirdMetatarsal from() {
        return ThirdMetatarsal.INSTANCE;
    }

    @Override
    public Point<ThirdMetatarsal> fromPoint() {
        return ThirdMetatarsal.Points.NEXT;
    }

    @Override
    public FourthMetatarsal to() {
        return FourthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FourthMetatarsal> toPoint() {
        return FourthMetatarsal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
