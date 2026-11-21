package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Ethmoid;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ethmoid before the sphenoid: knit fast.
 */
public record SphenoethmoidalSuture() implements Simple<Sphenoid, Ethmoid> {

    public static final SphenoethmoidalSuture INSTANCE = new SphenoethmoidalSuture();

    @Override
    public String name() {
        return "sphenoethmoidal suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.ETHMOID;
    }

    @Override
    public Ethmoid to() {
        return Ethmoid.INSTANCE;
    }

    @Override
    public Point<Ethmoid> toPoint() {
        return Ethmoid.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
