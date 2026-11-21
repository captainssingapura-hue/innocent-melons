package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Pisiform;
import com.example.fxsuite.life.body.bone.hand.Triquetrum;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The pisiform on the triquetrum's front: held.
 */
public record Pisotriquetral() implements Joint<Triquetrum, Pisiform> {

    public static final Pisotriquetral INSTANCE = new Pisotriquetral();

    @Override
    public String name() {
        return "pisotriquetral joint";
    }

    @Override
    public Triquetrum from() {
        return Triquetrum.INSTANCE;
    }

    @Override
    public Point<Triquetrum> fromPoint() {
        return Triquetrum.Points.PISIFORM;
    }

    @Override
    public Pisiform to() {
        return Pisiform.INSTANCE;
    }

    @Override
    public Point<Pisiform> toPoint() {
        return Pisiform.Points.TRIQUETRUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
