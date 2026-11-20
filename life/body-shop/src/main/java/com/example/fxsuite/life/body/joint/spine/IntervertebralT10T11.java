package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T11;
import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T10 vertebra on the T11, on the disc between them: bending forward as far as 5.5 degrees and back 3.5, to either
 * side 7, twisting 2.
 */
public record IntervertebralT10T11() implements Simple<T11, T10> {

    public static final IntervertebralT10T11 INSTANCE = new IntervertebralT10T11();

    @Override
    public String name() {
        return "T10/T11 intervertebral joint";
    }

    @Override
    public T11 from() {
        return T11.INSTANCE;
    }

    @Override
    public Point<T11> fromPoint() {
        return T11.Points.ABOVE;
    }

    @Override
    public T10 to() {
        return T10.INSTANCE;
    }

    @Override
    public Point<T10> toPoint() {
        return T10.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-3.5, 5.5), new Range(-7, 7), new Range(-2, 2));
    }
}
