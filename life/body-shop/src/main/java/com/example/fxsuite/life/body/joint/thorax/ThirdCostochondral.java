package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The third rib's bone on its cartilage: one, held fast.
 */
public record ThirdCostochondral() implements Joint<ThirdRib, CostalCartilages> {

    public static final ThirdCostochondral INSTANCE = new ThirdCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the third rib";
    }

    @Override
    public ThirdRib from() {
        return ThirdRib.INSTANCE;
    }

    @Override
    public Point<ThirdRib> fromPoint() {
        return ThirdRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.THIRD;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
