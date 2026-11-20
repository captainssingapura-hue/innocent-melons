package com.example.fxsuite.life.body.joint.leg;

import com.example.fxsuite.life.body.bone.foot.Talus;
import com.example.fxsuite.life.body.bone.leg.Fibula;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The lateral malleolus on the talus's outer side, on the same axis.
 */
public record Fibulotalar() implements Joint<Fibula, Talus> {

    public static final Fibulotalar INSTANCE = new Fibulotalar();

    @Override
    public String name() {
        return "fibulotalar joint";
    }

    @Override
    public Fibula from() {
        return Fibula.INSTANCE;
    }

    @Override
    public Point<Fibula> fromPoint() {
        return Fibula.Points.MALLEOLUS;
    }

    @Override
    public Talus to() {
        return Talus.INSTANCE;
    }

    @Override
    public Point<Talus> toPoint() {
        return Talus.Points.MALLEOLUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-20, 50));
    }
}
