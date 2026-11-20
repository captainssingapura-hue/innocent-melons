package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.bone.skull.Vomer;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The vomer under the sphenoid's body: knit fast.
 */
public record SphenovomerineSuture() implements Simple<Sphenoid, Vomer> {

    public static final SphenovomerineSuture INSTANCE = new SphenovomerineSuture();

    @Override
    public String name() {
        return "sphenovomerine suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.VOMER;
    }

    @Override
    public Vomer to() {
        return Vomer.INSTANCE;
    }

    @Override
    public Point<Vomer> toPoint() {
        return Vomer.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
