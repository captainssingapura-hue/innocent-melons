package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.Lunate;
import com.example.fxsuite.life.body.bone.hand.Triquetrum;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The triquetrum beside the lunate: held.
 */
public record Lunotriquetral() implements Joint<Lunate, Triquetrum> {

    public static final Lunotriquetral INSTANCE = new Lunotriquetral();

    @Override
    public String name() {
        return "lunotriquetral joint";
    }

    @Override
    public Lunate from() {
        return Lunate.INSTANCE;
    }

    @Override
    public Point<Lunate> fromPoint() {
        return Lunate.Points.TRIQUETRUM;
    }

    @Override
    public Triquetrum to() {
        return Triquetrum.INSTANCE;
    }

    @Override
    public Point<Triquetrum> toPoint() {
        return Triquetrum.Points.LUNATE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
