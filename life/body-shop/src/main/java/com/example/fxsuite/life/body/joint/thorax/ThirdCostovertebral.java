package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.bone.thorax.ThirdRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The third rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record ThirdCostovertebral() implements Simple<T3, ThirdRib> {

    public static final ThirdCostovertebral INSTANCE = new ThirdCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the third rib";
    }

    @Override
    public T3 from() {
        return T3.INSTANCE;
    }

    @Override
    public Point<T3> fromPoint() {
        return T3.Points.RIB;
    }

    @Override
    public ThirdRib to() {
        return ThirdRib.INSTANCE;
    }

    @Override
    public Point<ThirdRib> toPoint() {
        return ThirdRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
