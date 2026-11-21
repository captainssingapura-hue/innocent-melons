package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.spine.T11;
import com.example.fxsuite.life.body.bone.thorax.EleventhRib;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The eleventh rib's head on its vertebra, turning about its neck: rising as far as 10 degrees as the chest fills,
 * sinking 5.
 */
public record EleventhCostovertebral() implements Simple<T11, EleventhRib> {

    public static final EleventhCostovertebral INSTANCE = new EleventhCostovertebral();

    @Override
    public String name() {
        return "costovertebral joint of the eleventh rib";
    }

    @Override
    public T11 from() {
        return T11.INSTANCE;
    }

    @Override
    public Point<T11> fromPoint() {
        return T11.Points.RIB;
    }

    @Override
    public EleventhRib to() {
        return EleventhRib.INSTANCE;
    }

    @Override
    public Point<EleventhRib> toPoint() {
        return EleventhRib.Points.VERTEBRA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 10));
    }
}
