package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sphenoid's greater wing on the temporal: knit fast.
 */
public record SphenosquamosalSuture() implements Simple<Sphenoid, Temporal> {

    public static final SphenosquamosalSuture INSTANCE = new SphenosquamosalSuture();

    @Override
    public String name() {
        return "sphenosquamosal suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.TEMPORAL;
    }

    @Override
    public Temporal to() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> toPoint() {
        return Temporal.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
