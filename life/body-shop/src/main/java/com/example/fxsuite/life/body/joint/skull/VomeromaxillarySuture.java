package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Vomer;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The vomer on the maxillae's crest: knit fast.
 */
public record VomeromaxillarySuture() implements Simple<Maxilla, Vomer> {

    public static final VomeromaxillarySuture INSTANCE = new VomeromaxillarySuture();

    @Override
    public String name() {
        return "vomeromaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.VOMER;
    }

    @Override
    public Vomer to() {
        return Vomer.INSTANCE;
    }

    @Override
    public Point<Vomer> toPoint() {
        return Vomer.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
