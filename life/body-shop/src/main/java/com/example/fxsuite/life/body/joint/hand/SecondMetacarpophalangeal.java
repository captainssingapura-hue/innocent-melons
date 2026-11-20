package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.IndexFingerProximalPhalanx;
import com.example.fxsuite.life.body.bone.hand.SecondMetacarpal;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The index finger's knuckle, on its metacarpal's head: bending toward the palm as far as 90 degrees and back 30,
 * spreading 20 either way.
 */
public record SecondMetacarpophalangeal() implements Simple<SecondMetacarpal, IndexFingerProximalPhalanx> {

    public static final SecondMetacarpophalangeal INSTANCE = new SecondMetacarpophalangeal();

    @Override
    public String name() {
        return "metacarpophalangeal joint of the index finger";
    }

    @Override
    public SecondMetacarpal from() {
        return SecondMetacarpal.INSTANCE;
    }

    @Override
    public Point<SecondMetacarpal> fromPoint() {
        return SecondMetacarpal.Points.HEAD;
    }

    @Override
    public IndexFingerProximalPhalanx to() {
        return IndexFingerProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<IndexFingerProximalPhalanx> toPoint() {
        return IndexFingerProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-30, 90), new Range(-20, 20));
    }
}
