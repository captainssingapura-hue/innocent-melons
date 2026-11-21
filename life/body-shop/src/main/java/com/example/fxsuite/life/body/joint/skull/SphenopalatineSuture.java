package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Palatine;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The palatine on the sphenoid's pterygoid plates: knit fast.
 */
public record SphenopalatineSuture() implements Simple<Sphenoid, Palatine> {

    public static final SphenopalatineSuture INSTANCE = new SphenopalatineSuture();

    @Override
    public String name() {
        return "sphenopalatine suture";
    }

    @Override
    public Sphenoid from() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> fromPoint() {
        return Sphenoid.Points.PALATINE;
    }

    @Override
    public Palatine to() {
        return Palatine.INSTANCE;
    }

    @Override
    public Point<Palatine> toPoint() {
        return Palatine.Points.SPHENOID;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
