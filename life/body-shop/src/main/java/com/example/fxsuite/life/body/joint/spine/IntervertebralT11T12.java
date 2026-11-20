package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.bone.spine.T11;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T11 vertebra on the T12, on the disc between them: bending forward as far as 7 degrees and back 5, to either side
 * 9, twisting 2.
 */
public record IntervertebralT11T12() implements Simple<T12, T11> {

    public static final IntervertebralT11T12 INSTANCE = new IntervertebralT11T12();

    @Override
    public String name() {
        return "T11/T12 intervertebral joint";
    }

    @Override
    public T12 from() {
        return T12.INSTANCE;
    }

    @Override
    public Point<T12> fromPoint() {
        return T12.Points.ABOVE;
    }

    @Override
    public T11 to() {
        return T11.INSTANCE;
    }

    @Override
    public Point<T11> toPoint() {
        return T11.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 7), new Range(-9, 9), new Range(-2, 2));
    }
}
