package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.SixthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The sixth rib's bone on its cartilage: one, held fast.
 */
public record SixthCostochondral() implements Joint<SixthRib, CostalCartilages> {

    public static final SixthCostochondral INSTANCE = new SixthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the sixth rib";
    }

    @Override
    public SixthRib from() {
        return SixthRib.INSTANCE;
    }

    @Override
    public Point<SixthRib> fromPoint() {
        return SixthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.SIXTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
