package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sphenoid's wings under the frontal: knit fast.
 */
public record SphenofrontalSuture() implements Simple<Sphenoid, Frontal> {

    public static final SphenofrontalSuture INSTANCE = new SphenofrontalSuture();

    @Override
    public String name() {
        return "sphenofrontal suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.FRONTAL;
    }

    @Override
    public Frontal to() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> toPoint() {
        return Frontal.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
