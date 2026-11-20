package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.IntermediateCuneiform;
import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The second metatarsal's base on its tarsal: held.
 */
public record SecondTarsometatarsal() implements Joint<IntermediateCuneiform, SecondMetatarsal> {

    public static final SecondTarsometatarsal INSTANCE = new SecondTarsometatarsal();

    @Override
    public String name() {
        return "second tarsometatarsal joint";
    }

    @Override
    public IntermediateCuneiform from() {
        return IntermediateCuneiform.INSTANCE;
    }

    @Override
    public Point<IntermediateCuneiform> fromPoint() {
        return IntermediateCuneiform.Points.SECOND;
    }

    @Override
    public SecondMetatarsal to() {
        return SecondMetatarsal.INSTANCE;
    }

    @Override
    public Point<SecondMetatarsal> toPoint() {
        return SecondMetatarsal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
