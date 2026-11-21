package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FifthMetatarsal;
import com.example.fxsuite.life.body.bone.foot.LittleToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The little toe on its metatarsal's head: curling down as far as 40 degrees and up 60, a little to either side.
 */
public record FifthMetatarsophalangeal() implements Simple<FifthMetatarsal, LittleToeProximalPhalanx> {

    public static final FifthMetatarsophalangeal INSTANCE = new FifthMetatarsophalangeal();

    @Override
    public String name() {
        return "fifth metatarsophalangeal joint";
    }

    @Override
    public FifthMetatarsal from() {
        return FifthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FifthMetatarsal> fromPoint() {
        return FifthMetatarsal.Points.HEAD;
    }

    @Override
    public LittleToeProximalPhalanx to() {
        return LittleToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<LittleToeProximalPhalanx> toPoint() {
        return LittleToeProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-60, 40), new Range(-10, 10));
    }
}
