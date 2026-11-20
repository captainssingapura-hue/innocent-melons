package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.bone.thorax.SixthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sixth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record SixthCostovertebral() implements Simple<T6, SixthRib> {

    public static final SixthCostovertebral INSTANCE = new SixthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the sixth rib";
    }

    @Override
    public T6 from() {
        return T6.INSTANCE;
    }

    @Override
    public Point<T6> fromPoint() {
        return T6.Points.RIB;
    }

    @Override
    public SixthRib to() {
        return SixthRib.INSTANCE;
    }

    @Override
    public Point<SixthRib> toPoint() {
        return SixthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
