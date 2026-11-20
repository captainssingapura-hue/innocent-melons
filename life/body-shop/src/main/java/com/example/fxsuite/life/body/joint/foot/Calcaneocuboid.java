package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.Cuboid;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The heel bone's front on the cuboid: held.
 */
public record Calcaneocuboid() implements Joint<Calcaneus, Cuboid> {

    public static final Calcaneocuboid INSTANCE = new Calcaneocuboid();

    @Override
    public String name() {
        return "calcaneocuboid joint";
    }

    @Override
    public Calcaneus from() {
        return Calcaneus.INSTANCE;
    }

    @Override
    public Point<Calcaneus> fromPoint() {
        return Calcaneus.Points.CUBOID;
    }

    @Override
    public Cuboid to() {
        return Cuboid.INSTANCE;
    }

    @Override
    public Point<Cuboid> toPoint() {
        return Cuboid.Points.CALCANEUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
