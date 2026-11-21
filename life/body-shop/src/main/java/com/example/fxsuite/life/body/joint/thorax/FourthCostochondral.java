package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.FourthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The fourth rib's bone on its cartilage: one, held fast.
 */
public record FourthCostochondral() implements Joint<FourthRib, CostalCartilages> {

    public static final FourthCostochondral INSTANCE = new FourthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the fourth rib";
    }

    @Override
    public FourthRib from() {
        return FourthRib.INSTANCE;
    }

    @Override
    public Point<FourthRib> fromPoint() {
        return FourthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.FOURTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
