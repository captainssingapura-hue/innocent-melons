package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The clavicle's end in the sternum's notch: raised as far as 40 degrees and lowered 10, drawn back 25 and forward 15,
 * turned about its length 40 one way and 10 the other.
 */
public record Sternoclavicular() implements Simple<Sternum, Clavicle> {

    public static final Sternoclavicular INSTANCE = new Sternoclavicular();

    @Override
    public String name() {
        return "sternoclavicular joint";
    }

    @Override
    public Sternum from() {
        return Sternum.INSTANCE;
    }

    @Override
    public Point<Sternum> fromPoint() {
        return Sternum.Points.CLAVICLE;
    }

    @Override
    public Clavicle to() {
        return Clavicle.INSTANCE;
    }

    @Override
    public Point<Clavicle> toPoint() {
        return Clavicle.Points.STERNUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Ball(new Range(-10, 40), new Range(-15, 25), new Range(-10, 40));
    }
}
