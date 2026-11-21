package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The skull's condyles on the atlas: nodding forward as far as 15 degrees and back 25, a little to either side.
 */
public record AtlantoOccipital() implements Simple<Atlas, Occipital> {

    public static final AtlantoOccipital INSTANCE = new AtlantoOccipital();

    @Override
    public String name() {
        return "atlanto-occipital joint";
    }

    @Override
    public Atlas from() {
        return Atlas.INSTANCE;
    }

    @Override
    public Point<Atlas> fromPoint() {
        return Atlas.Points.ABOVE;
    }

    @Override
    public Occipital to() {
        return Occipital.INSTANCE;
    }

    @Override
    public Point<Occipital> toPoint() {
        return Occipital.Points.ATLAS;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-25, 15), new Range(-5, 5));
    }
}
