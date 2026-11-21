package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.Calcaneus;
import com.example.fxsuite.life.body.bone.foot.Talus;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The heel bone under the talus, turning about the subtalar axis: inverting as far as 20 degrees, everting 10.
 */
public record Subtalar() implements Simple<Talus, Calcaneus> {

    public static final Subtalar INSTANCE = new Subtalar();

    @Override
    public String name() {
        return "subtalar joint";
    }

    @Override
    public Talus from() {
        return Talus.INSTANCE;
    }

    @Override
    public Point<Talus> fromPoint() {
        return Talus.Points.CALCANEUS;
    }

    @Override
    public Calcaneus to() {
        return Calcaneus.INSTANCE;
    }

    @Override
    public Point<Calcaneus> toPoint() {
        return Calcaneus.Points.TALUS;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-10, 20));
    }
}
