package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Parietal;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sphenoid's greater wing on the parietal, at the temple: knit fast.
 */
public record SphenoparietalSuture() implements Simple<Sphenoid, Parietal> {

    public static final SphenoparietalSuture INSTANCE = new SphenoparietalSuture();

    @Override
    public String name() {
        return "sphenoparietal suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.PARIETAL;
    }

    @Override
    public Parietal to() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> toPoint() {
        return Parietal.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
