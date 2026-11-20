package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The jaw's condyles turning in their hollows by the ears, both at once, on one axis: opening as far as 30 degrees.
 */
public record Temporomandibular() implements Simple<Temporal, Mandible> {

    public static final Temporomandibular INSTANCE = new Temporomandibular();

    @Override
    public String name() {
        return "temporomandibular joints";
    }

    @Override
    public Temporal from() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> fromPoint() {
        return Temporal.Points.MANDIBLE;
    }

    @Override
    public Mandible to() {
        return Mandible.INSTANCE;
    }

    @Override
    public Point<Mandible> toPoint() {
        return Mandible.Points.TEMPORAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 30));
    }

    /** Both sides at once, the jaw one bone: posed down the middle. */
    @Override
    public boolean whole() {
        return true;
    }
}
