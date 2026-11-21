package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The temporal's mastoid on the occipital: knit fast.
 */
public record OccipitomastoidSuture() implements Simple<Occipital, Temporal> {

    public static final OccipitomastoidSuture INSTANCE = new OccipitomastoidSuture();

    @Override
    public String name() {
        return "occipitomastoid suture";
    }

    @Override
    public Occipital from() {
        return Occipital.INSTANCE;
    }

    @Override
    public Point<Occipital> fromPoint() {
        return Occipital.Points.TEMPORAL;
    }

    @Override
    public Temporal to() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> toPoint() {
        return Temporal.Points.OCCIPITAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
