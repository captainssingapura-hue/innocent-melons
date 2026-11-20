package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T7 vertebra on the T8, on the disc between them: bending forward as far as 3.5 degrees and back 2.5, to either
 * side 6, twisting 7.
 */
public record IntervertebralT7T8() implements Simple<T8, T7> {

    public static final IntervertebralT7T8 INSTANCE = new IntervertebralT7T8();

    @Override
    public String name() {
        return "T7/T8 intervertebral joint";
    }

    @Override
    public T8 from() {
        return T8.INSTANCE;
    }

    @Override
    public Point<T8> fromPoint() {
        return T8.Points.ABOVE;
    }

    @Override
    public T7 to() {
        return T7.INSTANCE;
    }

    @Override
    public Point<T7> toPoint() {
        return T7.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-2.5, 3.5), new Range(-6, 6), new Range(-7, 7));
    }
}
