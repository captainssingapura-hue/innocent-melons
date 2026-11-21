package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FourthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.RingFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The ring finger's knuckle, on its metacarpal's head: bending toward the palm as far as 90 degrees and back 30,
 * spreading 20 either way.
 */
public record FourthMetacarpophalangeal() implements Simple<FourthMetacarpal, RingFingerProximalPhalanx> {

    public static final FourthMetacarpophalangeal INSTANCE = new FourthMetacarpophalangeal();

    @Override
    public String name() {
        return "metacarpophalangeal joint of the ring finger";
    }

    @Override
    public FourthMetacarpal from() {
        return FourthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FourthMetacarpal> fromPoint() {
        return FourthMetacarpal.Points.HEAD;
    }

    @Override
    public RingFingerProximalPhalanx to() {
        return RingFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<RingFingerProximalPhalanx> toPoint() {
        return RingFingerProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-30, 90), new Range(-20, 20));
    }
}
