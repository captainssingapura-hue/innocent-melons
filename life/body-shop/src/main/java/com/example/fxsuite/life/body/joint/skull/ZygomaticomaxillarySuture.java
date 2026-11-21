package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Maxilla;
import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The zygomatic on the maxilla, under the orbit: knit fast.
 */
public record ZygomaticomaxillarySuture() implements Simple<Maxilla, Zygomatic> {

    public static final ZygomaticomaxillarySuture INSTANCE = new ZygomaticomaxillarySuture();

    @Override
    public String name() {
        return "zygomaticomaxillary suture";
    }

    @Override
    public Maxilla from() {
        return Maxilla.INSTANCE;
    }

    @Override
    public Point<Maxilla> fromPoint() {
        return Maxilla.Points.ZYGOMATIC;
    }

    @Override
    public Zygomatic to() {
        return Zygomatic.INSTANCE;
    }

    @Override
    public Point<Zygomatic> toPoint() {
        return Zygomatic.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
