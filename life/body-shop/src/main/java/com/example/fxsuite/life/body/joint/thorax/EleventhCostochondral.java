package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.EleventhRib;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The eleventh rib's bone on its cartilage: one, held fast.
 */
public record EleventhCostochondral() implements Joint<EleventhRib, CostalCartilages> {

    public static final EleventhCostochondral INSTANCE = new EleventhCostochondral();

    @Override
    public String name() {
        return "costochondral joint of the eleventh rib";
    }

    @Override
    public EleventhRib from() {
        return EleventhRib.INSTANCE;
    }

    @Override
    public Point<EleventhRib> fromPoint() {
        return EleventhRib.Points.CARTILAGE;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.ELEVENTH;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
