package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Stapes;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The stirrup's footplate in the inner ear's window, held by its ring of ligament.
 */
public record Stapediovestibular() implements Simple<Temporal, Stapes> {

    public static final Stapediovestibular INSTANCE = new Stapediovestibular();

    @Override
    public String name() {
        return "stapediovestibular joint";
    }

    @Override
    public Temporal from() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> fromPoint() {
        return Temporal.Points.STAPES;
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
