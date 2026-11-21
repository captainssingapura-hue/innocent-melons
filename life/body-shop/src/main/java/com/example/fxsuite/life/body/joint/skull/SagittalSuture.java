package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Parietal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Side;
import com.example.fxsuite.life.body.joint.Simple;

import java.util.List;

/**
 * The two parietals meeting down the middle of the top of the head: knit fast.
 */
public record SagittalSuture() implements Simple<Parietal, Parietal> {

    public static final SagittalSuture INSTANCE = new SagittalSuture();

    @Override
    public String name() {
        return "sagittal suture";
    }

    @Override
    public Parietal from() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> fromPoint() {
        return Parietal.Points.PARIETAL;
    }

    @Override
    public Parietal to() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> toPoint() {
        return Parietal.Points.PARIETAL;
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
