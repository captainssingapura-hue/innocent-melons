package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.bone.thorax.NinthRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ninth rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills, sinking
 * 5.
 */
public record NinthCostovertebral() implements Simple<T9, NinthRib> {

    public static final NinthCostovertebral INSTANCE = new NinthCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the ninth rib";
    }

    @Override
    public T9 from() {
        return T9.INSTANCE;
    }

    @Override
    public Point<T9> fromPoint() {
        return T9.Points.RIB;
    }

    @Override
    public NinthRib to() {
        return NinthRib.INSTANCE;
    }

    @Override
    public Point<NinthRib> toPoint() {
        return NinthRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
