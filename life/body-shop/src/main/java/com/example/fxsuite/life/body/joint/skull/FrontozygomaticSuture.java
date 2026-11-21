package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The zygomatic on the frontal, at the outer edge of the orbit: knit fast.
 */
public record FrontozygomaticSuture() implements Simple<Frontal, Zygomatic> {

    public static final FrontozygomaticSuture INSTANCE = new FrontozygomaticSuture();

    @Override
    public String name() {
        return "frontozygomatic suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.ZYGOMATIC;
    }

    @Override
    public Zygomatic to() {
        return Zygomatic.INSTANCE;
    }

    @Override
    public Point<Zygomatic> toPoint() {
        return Zygomatic.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
