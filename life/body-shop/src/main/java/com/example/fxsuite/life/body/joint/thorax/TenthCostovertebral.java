package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.thorax.TenthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The tenth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record TenthCostovertebral() implements Simple<T10, TenthRib> {

    public static final TenthCostovertebral INSTANCE = new TenthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the tenth rib";
    }

    @Override
    public T10 from() {
        return T10.INSTANCE;
    }

    @Override
    public Point<T10> fromPoint() {
        return T10.Points.RIB;
    }

    @Override
    public TenthRib to() {
        return TenthRib.INSTANCE;
    }

    @Override
    public Point<TenthRib> toPoint() {
        return TenthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
