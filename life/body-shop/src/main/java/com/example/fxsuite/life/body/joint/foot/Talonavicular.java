package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Navicular;
import com.example.fxsuite.life.body.bone.foot.Talus;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The talus's head in the navicular's hollow: held, in this foot.
 */
public record Talonavicular() implements Joint<Talus, Navicular> {

    public static final Talonavicular INSTANCE = new Talonavicular();

    @Override
    public String name() {
        return "talonavicular joint";
    }

    @Override
    public Talus from() {
        return Talus.INSTANCE;
    }

    @Override
    public Point<Talus> fromPoint() {
        return Talus.Points.NAVICULAR;
    }

    @Override
    public Navicular to() {
        return Navicular.INSTANCE;
    }

    @Override
    public Point<Navicular> toPoint() {
        return Navicular.Points.TALUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
