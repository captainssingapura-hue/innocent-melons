package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.bone.spine.L4;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The L4 vertebra on the L5, on the disc between them: bending forward as far as 11 degrees and back 5, to either side
 * 6, twisting 2.
 */
public record IntervertebralL4L5() implements Simple<L5, L4> {

    public static final IntervertebralL4L5 INSTANCE = new IntervertebralL4L5();

    @Override
    public String name() {
        return "L4/L5 intervertebral joint";
    }

    @Override
    public L5 from() {
        return L5.INSTANCE;
    }

    @Override
    public Point<L5> fromPoint() {
        return L5.Points.ABOVE;
    }

    @Override
    public L4 to() {
        return L4.INSTANCE;
    }

    @Override
    public Point<L4> toPoint() {
        return L4.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 11), new Range(-6, 6), new Range(-2, 2));
    }
}
