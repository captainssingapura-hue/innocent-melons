package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T10;
import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T9 vertebra on the T10, on the disc between them: bending forward as far as 3.5 degrees and back 2.5, to either
 * side 6, twisting 4.
 */
public record IntervertebralT9T10() implements Simple<T10, T9> {

    public static final IntervertebralT9T10 INSTANCE = new IntervertebralT9T10();

    @Override
    public String name() {
        return "T9/T10 intervertebral joint";
    }

    @Override
    public T10 from() {
        return T10.INSTANCE;
    }

    @Override
    public Point<T10> fromPoint() {
        return T10.Points.ABOVE;
    }

    @Override
    public T9 to() {
        return T9.INSTANCE;
    }

    @Override
    public Point<T9> toPoint() {
        return T9.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-2.5, 3.5), new Range(-6, 6), new Range(-4, 4));
    }
}
