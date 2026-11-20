package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.thorax.EighthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The eighth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record EighthCostovertebral() implements Simple<T8, EighthRib> {

    public static final EighthCostovertebral INSTANCE = new EighthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the eighth rib";
    }

    @Override
    public T8 from() {
        return T8.INSTANCE;
    }

    @Override
    public Point<T8> fromPoint() {
        return T8.Points.RIB;
    }

    @Override
    public EighthRib to() {
        return EighthRib.INSTANCE;
    }

    @Override
    public Point<EighthRib> toPoint() {
        return EighthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
