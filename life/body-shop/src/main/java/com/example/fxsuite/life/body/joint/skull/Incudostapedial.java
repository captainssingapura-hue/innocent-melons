package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Incus;
import com.example.fxsuite.life.body.bone.skull.Stapes;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The anvil's long limb on the stirrup's head: held, here.
 */
public record Incudostapedial() implements Simple<Stapes, Incus> {

    public static final Incudostapedial INSTANCE = new Incudostapedial();

    @Override
    public String name() {
        return "incudostapedial joint";
    }

    @Override
    public Stapes from() {
        return Stapes.INSTANCE;
    }

    @Override
    public Point<Stapes> fromPoint() {
        return Stapes.Points.INCUS;
    }

    @Override
    public Incus to() {
        return Incus.INSTANCE;
    }

    @Override
    public Point<Incus> toPoint() {
        return Incus.Points.STAPES;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
