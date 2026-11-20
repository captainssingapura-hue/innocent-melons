package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Cuboid;
import com.example.fxsuite.life.body.bone.foot.LateralCuneiform;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The cuboid beside the lateral cuneiform: held.
 */
public record Cuneocuboid() implements Joint<LateralCuneiform, Cuboid> {

    public static final Cuneocuboid INSTANCE = new Cuneocuboid();

    @Override
    public String name() {
        return "cuneocuboid joint";
    }

    @Override
    public LateralCuneiform from() {
        return LateralCuneiform.INSTANCE;
    }

    @Override
    public Point<LateralCuneiform> fromPoint() {
        return LateralCuneiform.Points.CUBOID;
    }

    @Override
    public Cuboid to() {
        return Cuboid.INSTANCE;
    }

    @Override
    public Point<Cuboid> toPoint() {
        return Cuboid.Points.LATERAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
