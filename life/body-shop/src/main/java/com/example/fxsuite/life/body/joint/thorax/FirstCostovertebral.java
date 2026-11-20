package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.thorax.FirstRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The first rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record FirstCostovertebral() implements Simple<T1, FirstRib> {

    public static final FirstCostovertebral INSTANCE = new FirstCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the first rib";
    }

    @Override
    public T1 from() {
        return T1.INSTANCE;
    }

    @Override
    public Point<T1> fromPoint() {
        return T1.Points.RIB;
    }

    @Override
    public FirstRib to() {
        return FirstRib.INSTANCE;
    }

    @Override
    public Point<FirstRib> toPoint() {
        return FirstRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
