package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.bone.spine.C4;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The C4 vertebra on the C5, on the disc between them: bending forward as far as 12 degrees and back 8, to either side
 * 11, twisting 7.
 */
public record IntervertebralC4C5() implements Simple<C5, C4> {

    public static final IntervertebralC4C5 INSTANCE = new IntervertebralC4C5();

    @Override
    public String name() {
        return "C4/C5 intervertebral joint";
    }

    @Override
    public C5 from() {
        return C5.INSTANCE;
    }

    @Override
    public Point<C5> fromPoint() {
        return C5.Points.ABOVE;
    }

    @Override
    public C4 to() {
        return C4.INSTANCE;
    }

    @Override
    public Point<C4> toPoint() {
        return C4.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-8, 12), new Range(-11, 11), new Range(-7, 7));
    }
}
