package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Cuboid;
import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fourth metatarsal's base on its tarsal: held.
 */
public record FourthTarsometatarsal() implements Joint<Cuboid, FourthMetatarsal> {

    public static final FourthTarsometatarsal INSTANCE = new FourthTarsometatarsal();

    @Override
    public String name() {
        return "fourth tarsometatarsal joint";
    }

    @Override
    public Cuboid from() {
        return Cuboid.INSTANCE;
    }

    @Override
    public Point<Cuboid> fromPoint() {
        return Cuboid.Points.FOURTH;
    }

    @Override
    public FourthMetatarsal to() {
        return FourthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FourthMetatarsal> toPoint() {
        return FourthMetatarsal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
