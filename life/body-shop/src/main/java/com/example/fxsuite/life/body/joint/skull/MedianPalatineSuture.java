package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Palatine;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Side;
import com.example.fxsuite.life.body.joint.Simple;

import java.util.List;

/**
 * The two palatines meeting down the middle of the hard palate: knit fast.
 */
public record MedianPalatineSuture() implements Simple<Palatine, Palatine> {

    public static final MedianPalatineSuture INSTANCE = new MedianPalatineSuture();

    @Override
    public String name() {
        return "median palatine suture";
    }

    @Override
    public Palatine from() {
        return Palatine.INSTANCE;
    }

    @Override
    public Point<Palatine> fromPoint() {
        return Palatine.Points.PALATINE;
    }

    @Override
    public Palatine to() {
        return Palatine.INSTANCE;
    }

    @Override
    public Point<Palatine> toPoint() {
        return Palatine.Points.PALATINE;
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
