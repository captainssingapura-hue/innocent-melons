package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.leg.Fibula;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fibula bound to the tibia above the ankle: held fast, the mortise the talus turns in.
 */
public record DistalTibiofibular() implements Simple<Tibia, Fibula> {

    public static final DistalTibiofibular INSTANCE = new DistalTibiofibular();

    @Override
    public String name() {
        return "distal tibiofibular joint";
    }

    @Override
    public Tibia from() {
        return Tibia.INSTANCE;
    }

    @Override
    public Point<Tibia> fromPoint() {
        return Tibia.Points.FIBULA_LOW;
    }

    @Override
    public Fibula to() {
        return Fibula.INSTANCE;
    }

    @Override
    public Point<Fibula> toPoint() {
        return Fibula.Points.LOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
