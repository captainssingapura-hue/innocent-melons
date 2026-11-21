package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The first rib's bone on its cartilage: one, held fast.
 */
public record FirstCostochondral() implements Joint<FirstRib, CostalCartilages> {

    public static final FirstCostochondral INSTANCE = new FirstCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the first rib";
    }

    @Override
    public FirstRib from() {
        return FirstRib.INSTANCE;
    }

    @Override
    public Point<FirstRib> fromPoint() {
        return FirstRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.FIRST;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
