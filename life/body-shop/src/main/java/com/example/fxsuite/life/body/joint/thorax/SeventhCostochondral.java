package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.SeventhRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The seventh rib's bone on its cartilage: one, held fast.
 */
public record SeventhCostochondral() implements Joint<SeventhRib, CostalCartilages> {

    public static final SeventhCostochondral INSTANCE = new SeventhCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the seventh rib";
    }

    @Override
    public SeventhRib from() {
        return SeventhRib.INSTANCE;
    }

    @Override
    public Point<SeventhRib> fromPoint() {
        return SeventhRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.SEVENTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
