package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FifthMetacarpal;
import com.example.fxsuite.life.body.bone.hand.LittleFingerProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little finger's knuckle, on its metacarpal's head: bending toward the palm as far as 90 degrees and back 30,
 * spreading 20 either way.
 */
public record FifthMetacarpophalangeal() implements Simple<FifthMetacarpal, LittleFingerProximalPhalanx> {

    public static final FifthMetacarpophalangeal INSTANCE = new FifthMetacarpophalangeal();

    @Override
    public String name() {
        return "metacarpophalangeal joint of the little finger";
    }

    @Override
    public FifthMetacarpal from() {
        return FifthMetacarpal.INSTANCE;
    }

    @Override
    public Point<FifthMetacarpal> fromPoint() {
        return FifthMetacarpal.Points.HEAD;
    }

    @Override
    public LittleFingerProximalPhalanx to() {
        return LittleFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleFingerProximalPhalanx> toPoint() {
        return LittleFingerProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-30, 90), new Range(-20, 20));
    }
}
