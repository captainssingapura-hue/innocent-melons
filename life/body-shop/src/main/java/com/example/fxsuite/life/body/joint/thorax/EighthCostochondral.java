package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.EighthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The eighth rib's bone on its cartilage: one, held fast.
 */
public record EighthCostochondral() implements Joint<EighthRib, CostalCartilages> {

    public static final EighthCostochondral INSTANCE = new EighthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the eighth rib";
    }

    @Override
    public EighthRib from() {
        return EighthRib.INSTANCE;
    }

    @Override
    public Point<EighthRib> fromPoint() {
        return EighthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.EIGHTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
