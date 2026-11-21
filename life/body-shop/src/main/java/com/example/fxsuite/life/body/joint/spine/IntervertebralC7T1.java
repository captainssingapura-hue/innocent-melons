package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.T1;
import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The C7 vertebra on the T1, on the disc between them: bending forward as far as 5 degrees and back 4, to either side
 * 4, twisting 2.
 */
public record IntervertebralC7T1() implements Simple<T1, C7> {

    public static final IntervertebralC7T1 INSTANCE = new IntervertebralC7T1();

    @Override
    public String name() {
        return "C7/T1 intervertebral joint";
    }

    @Override
    public T1 from() {
        return T1.INSTANCE;
    }

    @Override
    public Point<T1> fromPoint() {
        return T1.Points.ABOVE;
    }

    @Override
    public C7 to() {
        return C7.INSTANCE;
    }

    @Override
    public Point<C7> toPoint() {
        return C7.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-4, 5), new Range(-4, 4), new Range(-2, 2));
    }
}
