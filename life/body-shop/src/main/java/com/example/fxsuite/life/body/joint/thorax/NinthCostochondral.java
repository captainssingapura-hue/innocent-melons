package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.NinthRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The ninth rib's bone on its cartilage: one, held fast.
 */
public record NinthCostochondral() implements Joint<NinthRib, CostalCartilages> {

    public static final NinthCostochondral INSTANCE = new NinthCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the ninth rib";
    }

    @Override
    public NinthRib from() {
        return NinthRib.INSTANCE;
    }

    @Override
    public Point<NinthRib> fromPoint() {
        return NinthRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.NINTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
