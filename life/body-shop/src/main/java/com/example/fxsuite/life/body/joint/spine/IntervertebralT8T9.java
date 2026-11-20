package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T9;
import com.example.fxsuite.life.body.bone.spine.T8;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T8 vertebra on the T9, on the disc between them: bending forward as far as 3.5 degrees and back 2.5, to either
 * side 6, twisting 6.
 */
public record IntervertebralT8T9() implements Simple<T9, T8> {

    public static final IntervertebralT8T9 INSTANCE = new IntervertebralT8T9();

    @Override
    public String name() {
        return "T8/T9 intervertebral joint";
    }

    @Override
    public T9 from() {
        return T9.INSTANCE;
    }

    @Override
    public Point<T9> fromPoint() {
        return T9.Points.ABOVE;
    }

    @Override
    public T8 to() {
        return T8.INSTANCE;
    }

    @Override
    public Point<T8> toPoint() {
        return T8.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-2.5, 3.5), new Range(-6, 6), new Range(-6, 6));
    }
}
