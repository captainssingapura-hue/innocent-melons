package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.InferiorNasalConcha;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The inferior nasal concha on the maxilla's inner wall: knit fast.
 */
public record ConchomaxillarySuture() implements Simple<Maxilla, InferiorNasalConcha> {

    public static final ConchomaxillarySuture INSTANCE = new ConchomaxillarySuture();

    @Override
    public String name() {
        return "conchomaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.CONCHA;
    }

    @Override
    public InferiorNasalConcha to() {
        return InferiorNasalConcha.INSTANCE;
    }

    @Override
    public Point<InferiorNasalConcha> toPoint() {
        return InferiorNasalConcha.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
