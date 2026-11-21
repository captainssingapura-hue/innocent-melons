package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The C3 vertebra on the C4, on the disc between them: bending forward as far as 9 degrees and back 6, to either side
 * 11, twisting 7.
 */
public record IntervertebralC3C4() implements Simple<C4, C3> {

    public static final IntervertebralC3C4 INSTANCE = new IntervertebralC3C4();

    @Override
    public String name() {
        return "C3/C4 intervertebral joint";
    }

    @Override
    public C4 from() {
        return C4.INSTANCE;
    }

    @Override
    public Point<C4> fromPoint() {
        return C4.Points.ABOVE;
    }

    @Override
    public C3 to() {
        return C3.INSTANCE;
    }

    @Override
    public Point<C3> toPoint() {
        return C3.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-6, 9), new Range(-11, 11), new Range(-7, 7));
    }
}
