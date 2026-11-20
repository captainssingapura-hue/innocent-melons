package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.bone.thorax.SeventhRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The seventh rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record SeventhCostovertebral() implements Simple<T7, SeventhRib> {

    public static final SeventhCostovertebral INSTANCE = new SeventhCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the seventh rib";
    }

    @Override
    public T7 from() {
        return T7.INSTANCE;
    }

    @Override
    public Point<T7> fromPoint() {
        return T7.Points.RIB;
    }

    @Override
    public SeventhRib to() {
        return SeventhRib.INSTANCE;
    }

    @Override
    public Point<SeventhRib> toPoint() {
        return SeventhRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
