package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.IntermediateCuneiform;
import com.example.fxsuite.life.body.bone.foot.MedialCuneiform;
import com.example.fxsuite.life.body.joint.Joint;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;

/**
 * The intermediate cuneiform beside the medial: held.
 */
public record MedialIntermediate() implements Joint<MedialCuneiform, IntermediateCuneiform> {

    public static final MedialIntermediate INSTANCE = new MedialIntermediate();

    @Override
    public String name() {
        return "medial to intermediate cuneiform";
    }

    @Override
    public MedialCuneiform from() {
        return MedialCuneiform.INSTANCE;
    }

    @Override
    public Point<MedialCuneiform> fromPoint() {
        return MedialCuneiform.Points.INTERMEDIATE;
    }

    @Override
    public IntermediateCuneiform to() {
        return IntermediateCuneiform.INSTANCE;
    }

    @Override
    public Point<IntermediateCuneiform> toPoint() {
        return IntermediateCuneiform.Points.MEDIAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
