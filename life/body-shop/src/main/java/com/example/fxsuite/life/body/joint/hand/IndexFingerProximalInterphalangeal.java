package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.IndexFingerMiddlePhalanx;
import com.example.fxsuite.life.body.bone.hand.IndexFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The index finger's middle phalanx on its proximal: bending as far as 100 degrees.
 */
public record IndexFingerProximalInterphalangeal()
        implements Simple<IndexFingerProximalPhalanx, IndexFingerMiddlePhalanx> {

    public static final IndexFingerProximalInterphalangeal INSTANCE = new IndexFingerProximalInterphalangeal();

    @Override
    public String name() {
        return "proximal interphalangeal joint of the index finger";
    }

    @Override
    public IndexFingerProximalPhalanx from() {
        return IndexFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<IndexFingerProximalPhalanx> fromPoint() {
        return IndexFingerProximalPhalanx.Points.HEAD;
    }

    @Override
    public IndexFingerMiddlePhalanx to() {
        return IndexFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<IndexFingerMiddlePhalanx> toPoint() {
        return IndexFingerMiddlePhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 100));
    }
}
