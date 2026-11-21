package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.thorax.TwelfthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The twelfth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record TwelfthCostovertebral() implements Simple<T12, TwelfthRib> {

    public static final TwelfthCostovertebral INSTANCE = new TwelfthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the twelfth rib";
    }

    @Override
    public T12 from() {
        return T12.INSTANCE;
    }

    @Override
    public Point<T12> fromPoint() {
        return T12.Points.RIB;
    }

    @Override
    public TwelfthRib to() {
        return TwelfthRib.INSTANCE;
    }

    @Override
    public Point<TwelfthRib> toPoint() {
        return TwelfthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
