package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.foot.Talus;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The talus's trochlea under the tibia.
 */
public record Tibiotalar() implements Joint<Tibia, Talus> {

    public static final Tibiotalar INSTANCE = new Tibiotalar();

    @Override
    public String name() {
        return "tibiotalar joint";
    }

    @Override
    public Tibia from() {
        return Tibia.INSTANCE;
    }

    @Override
    public Point<Tibia> fromPoint() {
        return Tibia.Points.ANKLE;
    }

    @Override
    public Talus to() {
        return Talus.INSTANCE;
    }

    @Override
    public Point<Talus> toPoint() {
        return Talus.Points.ANKLE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-20, 50));
    }
}
