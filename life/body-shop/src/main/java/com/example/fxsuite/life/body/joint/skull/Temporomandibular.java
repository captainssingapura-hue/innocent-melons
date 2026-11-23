package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The jaw's condyles turning in their hollows by the ears, both at once, on one axis: opening as far as 30 degrees.
 */
public record Temporomandibular() implements Simple<Skull, Mandible> {

    public static final Temporomandibular INSTANCE = new Temporomandibular();

    @Override
    public String name() {
        return "temporomandibular joints";
    }

    @Override
    public Skull from() {
        return Skull.INSTANCE;
    }

    @Override
    public Point<Skull> fromPoint() {
        return Skull.Points.MANDIBLE;
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
