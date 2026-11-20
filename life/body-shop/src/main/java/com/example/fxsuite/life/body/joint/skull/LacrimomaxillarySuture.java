package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Lacrimal;
import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The lacrimal beside the maxilla's frontal process: knit fast.
 */
public record LacrimomaxillarySuture() implements Simple<Maxilla, Lacrimal> {

    public static final LacrimomaxillarySuture INSTANCE = new LacrimomaxillarySuture();

    @Override
    public String name() {
        return "lacrimomaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.LACRIMAL;
    }

    @Override
    public Lacrimal to() {
        return Lacrimal.INSTANCE;
    }

    @Override
    public Point<Lacrimal> toPoint() {
        return Lacrimal.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
