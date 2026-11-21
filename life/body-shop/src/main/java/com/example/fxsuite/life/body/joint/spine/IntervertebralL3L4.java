package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.L4;
import com.example.fxsuite.life.body.bone.spine.L3;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The L3 vertebra on the L4, on the disc between them: bending forward as far as 10 degrees and back 5, to either side
 * 8, twisting 2.
 */
public record IntervertebralL3L4() implements Simple<L4, L3> {

    public static final IntervertebralL3L4 INSTANCE = new IntervertebralL3L4();

    @Override
    public String name() {
        return "L3/L4 intervertebral joint";
    }

    @Override
    public L4 from() {
        return L4.INSTANCE;
    }

    @Override
    public Point<L4> fromPoint() {
        return L4.Points.ABOVE;
    }

    @Override
    public L3 to() {
        return L3.INSTANCE;
    }

    @Override
    public Point<L3> toPoint() {
        return L3.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 10), new Range(-8, 8), new Range(-2, 2));
    }
}
