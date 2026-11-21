package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.skull.Parietal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The occipital bone on the parietals, across the back of the head: knit fast.
 */
public record LambdoidSuture() implements Simple<Occipital, Parietal> {

    public static final LambdoidSuture INSTANCE = new LambdoidSuture();

    @Override
    public String name() {
        return "lambdoid suture";
    }

    @Override
    public Occipital from() {
        return Occipital.INSTANCE;
    }

    @Override
    public Point<Occipital> fromPoint() {
        return Occipital.Points.PARIETAL;
    }

    @Override
    public Parietal to() {
        return Parietal.INSTANCE;
    }

    @Override
    public Point<Parietal> toPoint() {
        return Parietal.Points.OCCIPITAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
