package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.IntermediateCuneiform;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The intermediate cuneiform on the navicular: held.
 */
public record NavicularIntermediate() implements Joint<Navicular, IntermediateCuneiform> {

    public static final NavicularIntermediate INSTANCE = new NavicularIntermediate();

    @Override
    public String name() {
        return "navicular to intermediate cuneiform";
    }

    @Override
    public Navicular from() {
        return Navicular.INSTANCE;
    }

    @Override
    public Point<Navicular> fromPoint() {
        return Navicular.Points.INTERMEDIATE;
    }

    @Override
    public IntermediateCuneiform to() {
        return IntermediateCuneiform.INSTANCE;
    }

    @Override
    public Point<IntermediateCuneiform> toPoint() {
        return IntermediateCuneiform.Points.NAVICULAR;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
