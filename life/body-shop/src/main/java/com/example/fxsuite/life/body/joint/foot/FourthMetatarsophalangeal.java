package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FourthMetatarsal;
import com.example.fxsuite.life.body.bone.foot.FourthToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The fourth toe on its metatarsal's head: curling down as far as 40 degrees and up 60, a little to either side.
 */
public record FourthMetatarsophalangeal() implements Simple<FourthMetatarsal, FourthToeProximalPhalanx> {

    public static final FourthMetatarsophalangeal INSTANCE = new FourthMetatarsophalangeal();

    @Override
    public String name() {
        return "fourth metatarsophalangeal joint";
    }

    @Override
    public FourthMetatarsal from() {
        return FourthMetatarsal.INSTANCE;
    }

    @Override
    public Point<FourthMetatarsal> fromPoint() {
        return FourthMetatarsal.Points.HEAD;
    }

    @Override
    public FourthToeProximalPhalanx to() {
        return FourthToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<FourthToeProximalPhalanx> toPoint() {
        return FourthToeProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-60, 40), new Range(-10, 10));
    }
}
