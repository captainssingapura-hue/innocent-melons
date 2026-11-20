package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Palatine;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The palatine behind the maxilla, across the hard palate: knit fast.
 */
public record PalatomaxillarySuture() implements Simple<Maxilla, Palatine> {

    public static final PalatomaxillarySuture INSTANCE = new PalatomaxillarySuture();

    @Override
    public String name() {
        return "palatomaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.PALATINE;
    }

    @Override
    public Palatine to() {
        return Palatine.INSTANCE;
    }

    @Override
    public Point<Palatine> toPoint() {
        return Palatine.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
