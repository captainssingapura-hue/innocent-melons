package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.bone.spine.T4;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T4 vertebra on the T5, on the disc between them: bending forward as far as 2.5 degrees and back 1.5, to either
 * side 6, twisting 8.
 */
public record IntervertebralT4T5() implements Simple<T5, T4> {

    public static final IntervertebralT4T5 INSTANCE = new IntervertebralT4T5();

    @Override
    public String name() {
        return "T4/T5 intervertebral joint";
    }

    @Override
    public T5 from() {
        return T5.INSTANCE;
    }

    @Override
    public Point<T5> fromPoint() {
        return T5.Points.ABOVE;
    }

    @Override
    public T4 to() {
        return T4.INSTANCE;
    }

    @Override
    public Point<T4> toPoint() {
        return T4.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-1.5, 2.5), new Range(-6, 6), new Range(-8, 8));
    }
}
