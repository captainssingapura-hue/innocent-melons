package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Temporal;
import com.example.fxsuite.life.body.bone.skull.Zygomatic;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The zygomatic's arch on the temporal's, before the ear: knit fast.
 */
public record TemporozygomaticSuture() implements Simple<Temporal, Zygomatic> {

    public static final TemporozygomaticSuture INSTANCE = new TemporozygomaticSuture();

    @Override
    public String name() {
        return "temporozygomatic suture";
    }

    @Override
    public Temporal from() {
        return Temporal.INSTANCE;
    }

    @Override
    public Point<Temporal> fromPoint() {
        return Temporal.Points.ZYGOMATIC;
    }

    @Override
    public Zygomatic to() {
        return Zygomatic.INSTANCE;
    }

    @Override
    public Point<Zygomatic> toPoint() {
        return Zygomatic.Points.TEMPORAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
