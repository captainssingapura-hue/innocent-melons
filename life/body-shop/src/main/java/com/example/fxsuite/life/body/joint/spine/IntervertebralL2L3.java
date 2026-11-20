package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.bone.spine.L2;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The L2 vertebra on the L3, on the disc between them: bending forward as far as 9 degrees and back 5, to either side
 * 6, twisting 2.
 */
public record IntervertebralL2L3() implements Simple<L3, L2> {

    public static final IntervertebralL2L3 INSTANCE = new IntervertebralL2L3();

    @Override
    public String name() {
        return "L2/L3 intervertebral joint";
    }

    @Override
    public L3 from() {
        return L3.INSTANCE;
    }

    @Override
    public Point<L3> fromPoint() {
        return L3.Points.ABOVE;
    }

    @Override
    public L2 to() {
        return L2.INSTANCE;
    }

    @Override
    public Point<L2> toPoint() {
        return L2.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 9), new Range(-6, 6), new Range(-2, 2));
    }
}
