package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Lacrimal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The lacrimal under the frontal: knit fast.
 */
public record FrontolacrimalSuture() implements Simple<Frontal, Lacrimal> {

    public static final FrontolacrimalSuture INSTANCE = new FrontolacrimalSuture();

    @Override
    public String name() {
        return "frontolacrimal suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.LACRIMAL;
    }

    @Override
    public Lacrimal to() {
        return Lacrimal.INSTANCE;
    }

    @Override
    public Point<Lacrimal> toPoint() {
        return Lacrimal.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
