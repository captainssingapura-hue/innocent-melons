package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Stapes;
import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The stirrup's footplate in the inner ear's window, held by its ring of ligament.
 */
public record Stapediovestibular() implements Simple<Skull, Stapes> {

    public static final Stapediovestibular INSTANCE = new Stapediovestibular();

    @Override
    public String name() {
        return "stapediovestibular joint";
    }

    @Override
    public Skull from() {
        return Skull.INSTANCE;
    }

    @Override
    public Point<Skull> fromPoint() {
        return Skull.Points.STAPES;
    }

    @Override
    public Stapes to() {
        return Stapes.INSTANCE;
    }

    @Override
    public Point<Stapes> toPoint() {
        return Stapes.Points.WINDOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
