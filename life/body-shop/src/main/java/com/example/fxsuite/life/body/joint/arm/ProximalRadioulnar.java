package com.example.fxsuite.life.body.joint.arm;

import com.example.fxsuite.life.body.bone.arm.Radius;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;

/**
 * The radius's head turning in the ulna's notch, in its ring of ligament: pronating the forearm.
 */
public record ProximalRadioulnar() implements Joint<Ulna, Radius> {

    public static final ProximalRadioulnar INSTANCE = new ProximalRadioulnar();

    @Override
    public String name() {
        return "proximal radioulnar joint";
    }

    @Override
    public Ulna from() {
        return Ulna.INSTANCE;
    }

    @Override
    public Point<Ulna> fromPoint() {
        return Ulna.Points.RADIAL_NOTCH;
    }

    @Override
    public Radius to() {
        return Radius.INSTANCE;
    }

    @Override
    public Point<Radius> toPoint() {
        return Radius.Points.CIRCUMFERENCE;
    }

    @Override
    public Motion motion() {
        return new Motion.Pivot(new Range(-10, 170));
    }
}
