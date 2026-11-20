package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.L5;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The L5 vertebra on the sacrum, on the disc between them: bending forward as far as 12 degrees and back 5, to either
 * side 3, twisting 1.
 */
public record IntervertebralL5S1() implements Simple<Sacrum, L5> {

    public static final IntervertebralL5S1 INSTANCE = new IntervertebralL5S1();

    @Override
    public String name() {
        return "L5/S1 intervertebral joint";
    }

    @Override
    public Sacrum from() {
        return Sacrum.INSTANCE;
    }

    @Override
    public Point<Sacrum> fromPoint() {
        return Sacrum.Points.ABOVE;
    }

    @Override
    public L5 to() {
        return L5.INSTANCE;
    }

    @Override
    public Point<L5> toPoint() {
        return L5.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 12), new Range(-3, 3), new Range(-1, 1));
    }
}
