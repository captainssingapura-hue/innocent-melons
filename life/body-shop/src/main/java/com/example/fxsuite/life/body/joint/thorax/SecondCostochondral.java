package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The second rib's bone on its cartilage: one, held fast.
 */
public record SecondCostochondral() implements Joint<SecondRib, CostalCartilages> {

    public static final SecondCostochondral INSTANCE = new SecondCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the second rib";
    }

    @Override
    public SecondRib from() {
        return SecondRib.INSTANCE;
    }

    @Override
    public Point<SecondRib> fromPoint() {
        return SecondRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.SECOND;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
