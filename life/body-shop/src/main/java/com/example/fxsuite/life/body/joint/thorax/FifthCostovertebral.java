package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.bone.thorax.FifthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fifth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record FifthCostovertebral() implements Simple<T5, FifthRib> {

    public static final FifthCostovertebral INSTANCE = new FifthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the fifth rib";
    }

    @Override
    public T5 from() {
        return T5.INSTANCE;
    }

    @Override
    public Point<T5> fromPoint() {
        return T5.Points.RIB;
    }

    @Override
    public FifthRib to() {
        return FifthRib.INSTANCE;
    }

    @Override
    public Point<FifthRib> toPoint() {
        return FifthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
