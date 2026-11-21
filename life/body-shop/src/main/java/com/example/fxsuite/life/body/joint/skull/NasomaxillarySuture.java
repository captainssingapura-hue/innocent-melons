package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Nasal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The nasal bone beside the maxilla: knit fast.
 */
public record NasomaxillarySuture() implements Simple<Nasal, Maxilla> {

    public static final NasomaxillarySuture INSTANCE = new NasomaxillarySuture();

    @Override
    public String name() {
        return "nasomaxillary suture";
    }

    @Override
    public Nasal from() {
        return Nasal.INSTANCE;
    }

    @Override
    public Point<Nasal> fromPoint() {
        return Nasal.Points.MAXILLA;
    }

    @Override
    public Maxilla to() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> toPoint() {
        return Maxilla.Points.NASAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
