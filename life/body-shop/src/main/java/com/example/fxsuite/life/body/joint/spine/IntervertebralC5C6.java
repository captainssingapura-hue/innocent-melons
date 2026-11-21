package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.bone.spine.C5;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The C5 vertebra on the C6, on the disc between them: bending forward as far as 12 degrees and back 8, to either side
 * 8, twisting 7.
 */
public record IntervertebralC5C6() implements Simple<C6, C5> {

    public static final IntervertebralC5C6 INSTANCE = new IntervertebralC5C6();

    @Override
    public String name() {
        return "C5/C6 intervertebral joint";
    }

    @Override
    public C6 from() {
        return C6.INSTANCE;
    }

    @Override
    public Point<C6> fromPoint() {
        return C6.Points.ABOVE;
    }

    @Override
    public C5 to() {
        return C5.INSTANCE;
    }

    @Override
    public Point<C5> toPoint() {
        return C5.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-8, 12), new Range(-8, 8), new Range(-7, 7));
    }
}
