package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Nasal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The nasal bones under the frontal, at the bridge of the nose: knit fast.
 */
public record FrontonasalSuture() implements Simple<Frontal, Nasal> {

    public static final FrontonasalSuture INSTANCE = new FrontonasalSuture();

    @Override
    public String name() {
        return "frontonasal suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.NASAL;
    }

    @Override
    public Nasal to() {
        return Nasal.INSTANCE;
    }

    @Override
    public Point<Nasal> toPoint() {
        return Nasal.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
