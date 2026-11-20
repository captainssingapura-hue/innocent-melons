package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The ulna's notch round the trochlea, a hinge bending the forearm forward.
 */
public record Humeroulnar() implements Joint<Humerus, Ulna> {

    public static final Humeroulnar INSTANCE = new Humeroulnar();

    @Override
    public String name() {
        return "humeroulnar joint";
    }

    @Override
    public Humerus from() {
        return Humerus.INSTANCE;
    }

    @Override
    public Point<Humerus> fromPoint() {
        return Humerus.Points.TROCHLEA;
    }

    @Override
    public Ulna to() {
        return Ulna.INSTANCE;
    }

    @Override
    public Point<Ulna> toPoint() {
        return Ulna.Points.TROCHLEA;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 145));
    }
}
