package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Parietal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The frontal bone on the parietals, across the top of the head: knit fast.
 */
public record CoronalSuture() implements Simple<Frontal, Parietal> {

    public static final CoronalSuture INSTANCE = new CoronalSuture();

    @Override
    public String name() {
        return "coronal suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.PARIETAL;
    }

    @Override
    public Parietal to() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> toPoint() {
        return Parietal.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
