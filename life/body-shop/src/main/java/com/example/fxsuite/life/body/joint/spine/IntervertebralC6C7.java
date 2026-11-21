package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.C7;
import com.example.fxsuite.life.body.bone.spine.C6;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The C6 vertebra on the C7, on the disc between them: bending forward as far as 10 degrees and back 7, to either side
 * 7, twisting 6.
 */
public record IntervertebralC6C7() implements Simple<C7, C6> {

    public static final IntervertebralC6C7 INSTANCE = new IntervertebralC6C7();

    @Override
    public String name() {
        return "C6/C7 intervertebral joint";
    }

    @Override
    public C7 from() {
        return C7.INSTANCE;
    }

    @Override
    public Point<C7> fromPoint() {
        return C7.Points.ABOVE;
    }

    @Override
    public C6 to() {
        return C6.INSTANCE;
    }

    @Override
    public Point<C6> toPoint() {
        return C6.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-7, 10), new Range(-7, 7), new Range(-6, 6));
    }
}
