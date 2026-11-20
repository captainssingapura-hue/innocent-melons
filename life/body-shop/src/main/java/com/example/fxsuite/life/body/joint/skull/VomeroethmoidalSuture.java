package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Ethmoid;
import com.example.fxsuite.life.body.bone.skull.Vomer;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The vomer behind the ethmoid's plate, down the nose's septum: knit fast.
 */
public record VomeroethmoidalSuture() implements Simple<Ethmoid, Vomer> {

    public static final VomeroethmoidalSuture INSTANCE = new VomeroethmoidalSuture();

    @Override
    public String name() {
        return "vomeroethmoidal suture";
    }

    @Override
    public Ethmoid from() {
        return Ethmoid.INSTANCE;
    }

    @Override
    public Point<Ethmoid> fromPoint() {
        return Ethmoid.Points.VOMER;
    }

    @Override
    public Vomer to() {
        return Vomer.INSTANCE;
    }

    @Override
    public Point<Vomer> toPoint() {
        return Vomer.Points.ETHMOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
