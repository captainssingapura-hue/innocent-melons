package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Frontal;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The maxilla's frontal process on the frontal: knit fast.
 */
public record FrontomaxillarySuture() implements Simple<Frontal, Maxilla> {

    public static final FrontomaxillarySuture INSTANCE = new FrontomaxillarySuture();

    @Override
    public String name() {
        return "frontomaxillary suture";
    }

    @Override
    public Frontal from() {
        return Frontal.INSTANCE;
    }

    @Override
    public Point<Frontal> fromPoint() {
        return Frontal.Points.MAXILLA;
    }

    @Override
    public Maxilla to() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> toPoint() {
        return Maxilla.Points.FRONTAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
