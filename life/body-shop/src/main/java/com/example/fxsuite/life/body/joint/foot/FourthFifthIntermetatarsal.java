package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fifth metatarsal's base beside the fourth's: held.
 */
public record FourthFifthIntermetatarsal() implements Joint<FourthMetatarsal, FifthMetatarsal> {

    public static final FourthFifthIntermetatarsal INSTANCE = new FourthFifthIntermetatarsal();

    @Override
    public String name() {
        return "fourth to fifth intermetatarsal joint";
    }

    @Override
    public FourthMetatarsal from() {
        return FourthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FourthMetatarsal> fromPoint() {
        return FourthMetatarsal.Points.NEXT;
    }

    @Override
    public FifthMetatarsal to() {
        return FifthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FifthMetatarsal> toPoint() {
        return FifthMetatarsal.Points.PREVIOUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
