package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Ethmoid;
import com.example.fxsuite.life.body.bone.skull.Lacrimal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The lacrimal before the ethmoid, in the orbit's inner wall: knit fast.
 */
public record EthmoidolacrimalSuture() implements Simple<Ethmoid, Lacrimal> {

    public static final EthmoidolacrimalSuture INSTANCE = new EthmoidolacrimalSuture();

    @Override
    public String name() {
        return "ethmoidolacrimal suture";
    }

    @Override
    public Ethmoid from() {
        return Ethmoid.INSTANCE;
    }

    @Override
    public Point<Ethmoid> fromPoint() {
        return Ethmoid.Points.LACRIMAL;
    }

    @Override
    public Lacrimal to() {
        return Lacrimal.INSTANCE;
    }

    @Override
    public Point<Lacrimal> toPoint() {
        return Lacrimal.Points.ETHMOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
