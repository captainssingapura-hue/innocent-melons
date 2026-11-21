package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The radius's head under the capitulum, walked from the radius: the humerus on it turned by the forearm's twist
 * undone, then the elbow's bend, so that a bent and twisted forearm meets it.
 */
public record Humeroradial() implements Joint<Radius, Humerus> {

    public static final Humeroradial INSTANCE = new Humeroradial();

    @Override
    public String name() {
        return "humeroradial joint";
    }

    @Override
    public Radius from() {
        return Radius.INSTANCE;
    }

    @Override
    public Point<Radius> fromPoint() {
        return Radius.Points.HEAD;
    }

    @Override
    public Humerus to() {
        return Humerus.INSTANCE;
    }

    @Override
    public Point<Humerus> toPoint() {
        return Humerus.Points.CAPITULUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-145, 0), Range.NONE, new Range(-170, 10));
    }
}
