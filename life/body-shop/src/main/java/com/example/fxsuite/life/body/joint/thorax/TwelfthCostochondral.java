package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.TwelfthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The twelfth rib's bone on its cartilage: one, held fast.
 */
public record TwelfthCostochondral() implements Joint<TwelfthRib, CostalCartilages> {

    public static final TwelfthCostochondral INSTANCE = new TwelfthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the twelfth rib";
    }

    @Override
    public TwelfthRib from() {
        return TwelfthRib.INSTANCE;
    }

    @Override
    public Point<TwelfthRib> fromPoint() {
        return TwelfthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.TWELFTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
