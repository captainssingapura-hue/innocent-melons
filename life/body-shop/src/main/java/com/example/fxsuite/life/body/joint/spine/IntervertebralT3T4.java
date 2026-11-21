package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T3 vertebra on the T4, on the disc between them: bending forward as far as 2.5 degrees and back 1.5, to either
 * side 6, twisting 8.
 */
public record IntervertebralT3T4() implements Simple<T4, T3> {

    public static final IntervertebralT3T4 INSTANCE = new IntervertebralT3T4();

    @Override
    public String name() {
        return "T3/T4 intervertebral joint";
    }

    @Override
    public T4 from() {
        return T4.INSTANCE;
    }

    @Override
    public Point<T4> fromPoint() {
        return T4.Points.ABOVE;
    }

    @Override
    public T3 to() {
        return T3.INSTANCE;
    }

    @Override
    public Point<T3> toPoint() {
        return T3.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-1.5, 2.5), new Range(-6, 6), new Range(-8, 8));
    }
}
