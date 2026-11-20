package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.C3;
import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The axis (C2) on the C3, on the disc between them: bending forward as far as 6 degrees and back 4, to either side 10,
 * twisting 3.
 */
public record IntervertebralC2C3() implements Simple<C3, Axis> {

    public static final IntervertebralC2C3 INSTANCE = new IntervertebralC2C3();

    @Override
    public String name() {
        return "C2/C3 intervertebral joint";
    }

    @Override
    public C3 from() {
        return C3.INSTANCE;
    }

    @Override
    public Point<C3> fromPoint() {
        return C3.Points.ABOVE;
    }

    @Override
    public Axis to() {
        return Axis.INSTANCE;
    }

    @Override
    public Point<Axis> toPoint() {
        return Axis.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-4, 6), new Range(-10, 10), new Range(-3, 3));
    }
}
