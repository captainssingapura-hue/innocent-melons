package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T7;
import com.example.fxsuite.life.body.bone.spine.T6;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T6 vertebra on the T7, on the disc between them: bending forward as far as 3 degrees and back 2, to either side
 * 6, twisting 7.
 */
public record IntervertebralT6T7() implements Simple<T7, T6> {

    public static final IntervertebralT6T7 INSTANCE = new IntervertebralT6T7();

    @Override
    public String name() {
        return "T6/T7 intervertebral joint";
    }

    @Override
    public T7 from() {
        return T7.INSTANCE;
    }

    @Override
    public Point<T7> fromPoint() {
        return T7.Points.ABOVE;
    }

    @Override
    public T6 to() {
        return T6.INSTANCE;
    }

    @Override
    public Point<T6> toPoint() {
        return T6.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-2, 3), new Range(-6, 6), new Range(-7, 7));
    }
}
