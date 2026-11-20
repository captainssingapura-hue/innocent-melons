package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.thorax.FourthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fourth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record FourthCostovertebral() implements Simple<T4, FourthRib> {

    public static final FourthCostovertebral INSTANCE = new FourthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the fourth rib";
    }

    @Override
    public T4 from() {
        return T4.INSTANCE;
    }

    @Override
    public Point<T4> fromPoint() {
        return T4.Points.RIB;
    }

    @Override
    public FourthRib to() {
        return FourthRib.INSTANCE;
    }

    @Override
    public Point<FourthRib> toPoint() {
        return FourthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
