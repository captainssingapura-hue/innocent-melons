package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.bone.spine.T5;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T5 vertebra on the T6, on the disc between them: bending forward as far as 3 degrees and back 2, to either side
 * 6, twisting 8.
 */
public record IntervertebralT5T6() implements Simple<T6, T5> {

    public static final IntervertebralT5T6 INSTANCE = new IntervertebralT5T6();

    @Override
    public String name() {
        return "T5/T6 intervertebral joint";
    }

    @Override
    public T6 from() {
        return T6.INSTANCE;
    }

    @Override
    public Point<T6> fromPoint() {
        return T6.Points.ABOVE;
    }

    @Override
    public T5 to() {
        return T5.INSTANCE;
    }

    @Override
    public Point<T5> toPoint() {
        return T5.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-2, 3), new Range(-6, 6), new Range(-8, 8));
    }
}
