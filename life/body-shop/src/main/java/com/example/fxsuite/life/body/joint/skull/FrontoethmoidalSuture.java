package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Ethmoid;
import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ethmoid under the frontal, between the orbits: knit fast.
 */
public record FrontoethmoidalSuture() implements Simple<Frontal, Ethmoid> {

    public static final FrontoethmoidalSuture INSTANCE = new FrontoethmoidalSuture();

    @Override
    public String name() {
        return "frontoethmoidal suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.ETHMOID;
    }

    @Override
    public Ethmoid to() {
        return Ethmoid.INSTANCE;
    }

    @Override
    public Point<Ethmoid> toPoint() {
        return Ethmoid.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
