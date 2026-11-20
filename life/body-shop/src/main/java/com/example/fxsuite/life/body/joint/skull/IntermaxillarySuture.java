package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Side;
import com.example.fxsuite.life.body.joint.Simple;

import java.util.List;

/**
 * The two maxillae meeting down the middle, under the nose: knit fast.
 */
public record IntermaxillarySuture() implements Simple<Maxilla, Maxilla> {

    public static final IntermaxillarySuture INSTANCE = new IntermaxillarySuture();

    @Override
    public String name() {
        return "intermaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.MAXILLA;
    }

    @Override
    public Maxilla to() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> toPoint() {
        return Maxilla.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }

    @Override
    public List<Ends> sides() {
        return List.of(new Ends(Side.RIGHT, Side.LEFT));
    }
}
