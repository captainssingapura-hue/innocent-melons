package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The tibia's plateau under the femur's condyles, a hinge bending back.
 */
public record Tibiofemoral() implements Joint<Femur, Tibia> {

    public static final Tibiofemoral INSTANCE = new Tibiofemoral();

    @Override
    public String name() {
        return "tibiofemoral joint";
    }

    @Override
    public Femur from() {
        return Femur.INSTANCE;
    }

    @Override
    public Point<Femur> fromPoint() {
        return Femur.Points.KNEE;
    }

    @Override
    public Tibia to() {
        return Tibia.INSTANCE;
    }

    @Override
    public Point<Tibia> toPoint() {
        return Tibia.Points.KNEE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 140));
    }
}
