package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.leg.Fibula;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fibula's head bound to the tibia below the knee: held fast.
 */
public record ProximalTibiofibular() implements Simple<Tibia, Fibula> {

    public static final ProximalTibiofibular INSTANCE = new ProximalTibiofibular();

    @Override
    public String name() {
        return "proximal tibiofibular joint";
    }

    @Override
    public Tibia from() {
        return Tibia.INSTANCE;
    }

    @Override
    public Point<Tibia> fromPoint() {
        return Tibia.Points.FIBULA_HEAD;
    }

    @Override
    public Fibula to() {
        return Fibula.INSTANCE;
    }

    @Override
    public Point<Fibula> toPoint() {
        return Fibula.Points.HEAD;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
