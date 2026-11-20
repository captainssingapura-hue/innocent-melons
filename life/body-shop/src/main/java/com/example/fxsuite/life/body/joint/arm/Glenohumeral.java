package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Scapula;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The humerus's head in the glenoid, a ball in a shallow socket: raised forward as far as 180 degrees and back 60, out
 * to the side 180 and in 30, turning in 70 and out 90; as far as the shoulder blade's gliding on the chest, not yet a
 * joint here, takes it too.
 */
public record Glenohumeral() implements Simple<Scapula, Humerus> {

    public static final Glenohumeral INSTANCE = new Glenohumeral();

    @Override
    public String name() {
        return "glenohumeral joint";
    }

    @Override
    public Scapula from() {
        return Scapula.INSTANCE;
    }

    @Override
    public Point<Scapula> fromPoint() {
        return Scapula.Points.GLENOID;
    }

    @Override
    public Humerus to() {
        return Humerus.INSTANCE;
    }

    @Override
    public Point<Humerus> toPoint() {
        return Humerus.Points.HEAD;
    }

    @Override
    public Motion motion() {
        return new Motion.Ball(new Range(-60, 180), new Range(-30, 180), new Range(-70, 90));
    }
}
