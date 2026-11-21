package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.ThumbProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The thumb on its metacarpal's head: bending as far as 60 degrees and back 10, a little to either side.
 */
public record FirstMetacarpophalangeal() implements Simple<FirstMetacarpal, ThumbProximalPhalanx> {

    public static final FirstMetacarpophalangeal INSTANCE = new FirstMetacarpophalangeal();

    @Override
    public String name() {
        return "metacarpophalangeal joint of the thumb";
    }

    @Override
    public FirstMetacarpal from() {
        return FirstMetacarpal.INSTANCE;
    }

    @Override
    public Point<FirstMetacarpal> fromPoint() {
        return FirstMetacarpal.Points.HEAD;
    }

    @Override
    public ThumbProximalPhalanx to() {
        return ThumbProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThumbProximalPhalanx> toPoint() {
        return ThumbProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-10, 60), new Range(-10, 10));
    }
}
