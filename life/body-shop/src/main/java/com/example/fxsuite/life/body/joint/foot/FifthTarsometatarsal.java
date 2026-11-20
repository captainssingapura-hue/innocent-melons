package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Cuboid;
import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fifth metatarsal's base on its tarsal: held.
 */
public record FifthTarsometatarsal() implements Joint<Cuboid, FifthMetatarsal> {

    public static final FifthTarsometatarsal INSTANCE = new FifthTarsometatarsal();

    @Override
    public String name() {
        return "fifth tarsometatarsal joint";
    }

    @Override
    public Cuboid from() {
        return Cuboid.INSTANCE;
    }

    @Override
    public Point<Cuboid> fromPoint() {
        return Cuboid.Points.FIFTH;
    }

    @Override
    public FifthMetatarsal to() {
        return FifthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FifthMetatarsal> toPoint() {
        return FifthMetatarsal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
