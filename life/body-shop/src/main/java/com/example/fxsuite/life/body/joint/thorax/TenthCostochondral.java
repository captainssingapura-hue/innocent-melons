package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The tenth rib's bone on its cartilage: one, held fast.
 */
public record TenthCostochondral() implements Joint<TenthRib, CostalCartilages> {

    public static final TenthCostochondral INSTANCE = new TenthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the tenth rib";
    }

    @Override
    public TenthRib from() {
        return TenthRib.INSTANCE;
    }

    @Override
    public Point<TenthRib> fromPoint() {
        return TenthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.TENTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
