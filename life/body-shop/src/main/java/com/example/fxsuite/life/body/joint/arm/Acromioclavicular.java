package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Clavicle;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The clavicle's end on the acromion: gliding a little every way.
 */
public record Acromioclavicular() implements Simple<Clavicle, Scapula> {

    public static final Acromioclavicular INSTANCE = new Acromioclavicular();

    @Override
    public String name() {
        return "acromioclavicular joint";
    }

    @Override
    public Clavicle from() {
        return Clavicle.INSTANCE;
    }

    @Override
    public Point<Clavicle> fromPoint() {
        return Clavicle.Points.ACROMION;
    }

    @Override
    public Scapula to() {
        return Scapula.INSTANCE;
    }

    @Override
    public Point<Scapula> toPoint() {
        return Scapula.Points.CLAVICLE;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-10, 10), new Range(-10, 10), new Range(-15, 15));
    }
}
