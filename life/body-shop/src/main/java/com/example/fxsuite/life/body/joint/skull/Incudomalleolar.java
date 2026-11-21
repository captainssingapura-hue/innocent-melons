package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Incus;
import com.example.fxsuite.life.body.bone.skull.Malleus;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The hammer's head on the anvil's body: held, here.
 */
public record Incudomalleolar() implements Simple<Incus, Malleus> {

    public static final Incudomalleolar INSTANCE = new Incudomalleolar();

    @Override
    public String name() {
        return "incudomalleolar joint";
    }

    @Override
    public Incus from() {
        return Incus.INSTANCE;
    }

    @Override
    public Point<Incus> fromPoint() {
        return Incus.Points.MALLEUS;
    }

    @Override
    public Malleus to() {
        return Malleus.INSTANCE;
    }

    @Override
    public Point<Malleus> toPoint() {
        return Malleus.Points.INCUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
