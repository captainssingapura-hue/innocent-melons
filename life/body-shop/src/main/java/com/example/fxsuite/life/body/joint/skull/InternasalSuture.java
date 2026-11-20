package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Nasal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Side;
import com.example.fxsuite.life.body.joint.Simple;

import java.util.List;

/**
 * The two nasal bones meeting down the bridge of the nose: knit fast.
 */
public record InternasalSuture() implements Simple<Nasal, Nasal> {

    public static final InternasalSuture INSTANCE = new InternasalSuture();

    @Override
    public String name() {
        return "internasal suture";
    }

    @Override
    public Nasal from() {
        return Nasal.INSTANCE;
    }

    @Override
    public Point<Nasal> fromPoint() {
        return Nasal.Points.NASAL;
    }

    @Override
    public Nasal to() {
        return Nasal.INSTANCE;
    }

    @Override
    public Point<Nasal> toPoint() {
        return Nasal.Points.NASAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }

    @Override
    public List<Ends> sides() {
        return List.of(new Ends(Side.RIGHT, Side.LEFT));
    }
}
