package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.IndexFingerDistalPhalanx;
import com.example.fxsuite.life.body.bone.hand.IndexFingerMiddlePhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The index finger's distal phalanx on its middle: bending as far as 80 degrees.
 */
public record IndexFingerDistalInterphalangeal() implements Simple<IndexFingerMiddlePhalanx, IndexFingerDistalPhalanx> {

    public static final IndexFingerDistalInterphalangeal INSTANCE = new IndexFingerDistalInterphalangeal();

    @Override
    public String name() {
        return "distal interphalangeal joint of the index finger";
    }

    @Override
    public IndexFingerMiddlePhalanx from() {
        return IndexFingerMiddlePhalanx.INSTANCE;
    }

    @Override
    public Point<IndexFingerMiddlePhalanx> fromPoint() {
        return IndexFingerMiddlePhalanx.Points.HEAD;
    }

    @Override
    public IndexFingerDistalPhalanx to() {
        return IndexFingerDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<IndexFingerDistalPhalanx> toPoint() {
        return IndexFingerDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 80));
    }
}
