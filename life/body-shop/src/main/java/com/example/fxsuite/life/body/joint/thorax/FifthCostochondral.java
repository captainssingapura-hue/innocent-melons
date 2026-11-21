package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.FifthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fifth rib's bone on its cartilage: one, held fast.
 */
public record FifthCostochondral() implements Joint<FifthRib, CostalCartilages> {

    public static final FifthCostochondral INSTANCE = new FifthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the fifth rib";
    }

    @Override
    public FifthRib from() {
        return FifthRib.INSTANCE;
    }

    @Override
    public Point<FifthRib> fromPoint() {
        return FifthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.FIFTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
