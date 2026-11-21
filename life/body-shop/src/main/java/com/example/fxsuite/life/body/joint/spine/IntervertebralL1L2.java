package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.L2;
import com.example.fxsuite.life.body.bone.spine.L1;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The L1 vertebra on the L2, on the disc between them: bending forward as far as 8 degrees and back 4, to either side
 * 6, twisting 2.
 */
public record IntervertebralL1L2() implements Simple<L2, L1> {

    public static final IntervertebralL1L2 INSTANCE = new IntervertebralL1L2();

    @Override
    public String name() {
        return "L1/L2 intervertebral joint";
    }

    @Override
    public L2 from() {
        return L2.INSTANCE;
    }

    @Override
    public Point<L2> fromPoint() {
        return L2.Points.ABOVE;
    }

    @Override
    public L1 to() {
        return L1.INSTANCE;
    }

    @Override
    public Point<L1> toPoint() {
        return L1.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-4, 8), new Range(-6, 6), new Range(-2, 2));
    }
}
