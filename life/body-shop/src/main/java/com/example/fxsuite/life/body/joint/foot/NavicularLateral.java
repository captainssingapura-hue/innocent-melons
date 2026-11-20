package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.LateralCuneiform;
import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The lateral cuneiform on the navicular: held.
 */
public record NavicularLateral() implements Joint<Navicular, LateralCuneiform> {

    public static final NavicularLateral INSTANCE = new NavicularLateral();

    @Override
    public String name() {
        return "navicular to lateral cuneiform";
    }

    @Override
    public Navicular from() {
        return Navicular.INSTANCE;
    }

    @Override
    public Point<Navicular> fromPoint() {
        return Navicular.Points.LATERAL;
    }

    @Override
    public LateralCuneiform to() {
        return LateralCuneiform.INSTANCE;
    }

    @Override
    public Point<LateralCuneiform> toPoint() {
        return LateralCuneiform.Points.NAVICULAR;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
