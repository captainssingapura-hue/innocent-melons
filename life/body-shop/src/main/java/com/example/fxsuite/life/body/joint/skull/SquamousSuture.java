package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Parietal;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The temporal bone's scale lapped over the parietal's edge: knit fast.
 */
public record SquamousSuture() implements Simple<Parietal, Temporal> {

    public static final SquamousSuture INSTANCE = new SquamousSuture();

    @Override
    public String name() {
        return "squamous suture";
    }

    @Override
    public Parietal from() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> fromPoint() {
        return Parietal.Points.TEMPORAL;
    }

    @Override
    public Temporal to() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> toPoint() {
        return Temporal.Points.PARIETAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
