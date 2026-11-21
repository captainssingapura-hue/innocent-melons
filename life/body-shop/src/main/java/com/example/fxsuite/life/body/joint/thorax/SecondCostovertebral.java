package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.thorax.SecondRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The second rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record SecondCostovertebral() implements Simple<T2, SecondRib> {

    public static final SecondCostovertebral INSTANCE = new SecondCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the second rib";
    }

    @Override
    public T2 from() {
        return T2.INSTANCE;
    }

    @Override
    public Point<T2> fromPoint() {
        return T2.Points.RIB;
    }

    @Override
    public SecondRib to() {
        return SecondRib.INSTANCE;
    }

    @Override
    public Point<SecondRib> toPoint() {
        return SecondRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
