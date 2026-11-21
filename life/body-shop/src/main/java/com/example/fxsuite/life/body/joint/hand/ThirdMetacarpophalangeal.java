package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.MiddleFingerProximalPhalanx;
import com.example.fxsuite.life.body.bone.hand.ThirdMetacarpal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The middle finger's knuckle, on its metacarpal's head: bending toward the palm as far as 90 degrees and back 30,
 * spreading 20 either way.
 */
public record ThirdMetacarpophalangeal() implements Simple<ThirdMetacarpal, MiddleFingerProximalPhalanx> {

    public static final ThirdMetacarpophalangeal INSTANCE = new ThirdMetacarpophalangeal();

    @Override
    public String name() {
        return "metacarpophalangeal joint of the middle finger";
    }

    @Override
    public ThirdMetacarpal from() {
        return ThirdMetacarpal.INSTANCE;
    }

    @Override
    public Point<ThirdMetacarpal> fromPoint() {
        return ThirdMetacarpal.Points.HEAD;
    }

    @Override
    public MiddleFingerProximalPhalanx to() {
        return MiddleFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<MiddleFingerProximalPhalanx> toPoint() {
        return MiddleFingerProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-30, 90), new Range(-20, 20));
    }
}
