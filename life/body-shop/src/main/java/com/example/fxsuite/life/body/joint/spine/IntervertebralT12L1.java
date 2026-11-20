package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.bone.spine.T12;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The T12 vertebra on the L1, on the disc between them: bending forward as far as 7 degrees and back 5, to either side
 * 8, twisting 2.
 */
public record IntervertebralT12L1() implements Simple<L1, T12> {

    public static final IntervertebralT12L1 INSTANCE = new IntervertebralT12L1();

    @Override
    public String name() {
        return "T12/L1 intervertebral joint";
    }

    @Override
    public L1 from() {
        return L1.INSTANCE;
    }

    @Override
    public Point<L1> fromPoint() {
        return L1.Points.ABOVE;
    }

    @Override
    public T12 to() {
        return T12.INSTANCE;
    }

    @Override
    public Point<T12> toPoint() {
        return T12.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 7), new Range(-8, 8), new Range(-2, 2));
    }
}
