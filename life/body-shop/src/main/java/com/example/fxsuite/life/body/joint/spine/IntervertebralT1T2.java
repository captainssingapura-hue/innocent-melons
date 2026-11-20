package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T2;
import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T1 vertebra on the T2, on the disc between them: bending forward as far as 2.5 degrees and back 1.5, to either
 * side 6, twisting 9.
 */
public record IntervertebralT1T2() implements Simple<T2, T1> {

    public static final IntervertebralT1T2 INSTANCE = new IntervertebralT1T2();

    @Override
    public String name() {
        return "T1/T2 intervertebral joint";
    }

    @Override
    public T2 from() {
        return T2.INSTANCE;
    }

    @Override
    public Point<T2> fromPoint() {
        return T2.Points.ABOVE;
    }

    @Override
    public T1 to() {
        return T1.INSTANCE;
    }

    @Override
    public Point<T1> toPoint() {
        return T1.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-1.5, 2.5), new Range(-6, 6), new Range(-9, 9));
    }
}
