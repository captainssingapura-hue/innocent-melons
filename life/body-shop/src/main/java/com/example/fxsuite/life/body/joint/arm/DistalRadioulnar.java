package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The radius's notch turning round the ulna's head, on the same axis.
 */
public record DistalRadioulnar() implements Joint<Ulna, Radius> {

    public static final DistalRadioulnar INSTANCE = new DistalRadioulnar();

    @Override
    public String name() {
        return "distal radioulnar joint";
    }

    @Override
    public Ulna from() {
        return Ulna.INSTANCE;
    }

    @Override
    public Point<Ulna> fromPoint() {
        return Ulna.Points.HEAD;
    }

    @Override
    public Radius to() {
        return Radius.INSTANCE;
    }

    @Override
    public Point<Radius> toPoint() {
        return Radius.Points.ULNAR_NOTCH;
    }

    @Override
    public Motion motion() {
        return new Motion.Pivot(new Range(-10, 170));
    }
}
