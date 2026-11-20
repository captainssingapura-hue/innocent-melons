package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T3;
import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T2 vertebra on the T3, on the disc between them: bending forward as far as 2.5 degrees and back 1.5, to either
 * side 6, twisting 8.
 */
public record IntervertebralT2T3() implements Simple<T3, T2> {

    public static final IntervertebralT2T3 INSTANCE = new IntervertebralT2T3();

    @Override
    public String name() {
        return "T2/T3 intervertebral joint";
    }

    @Override
    public T3 from() {
        return T3.INSTANCE;
    }

    @Override
    public Point<T3> fromPoint() {
        return T3.Points.ABOVE;
    }

    @Override
    public T2 to() {
        return T2.INSTANCE;
    }

    @Override
    public Point<T2> toPoint() {
        return T2.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-1.5, 2.5), new Range(-6, 6), new Range(-8, 8));
    }
}
