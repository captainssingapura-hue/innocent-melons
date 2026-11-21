package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.SecondMetatarsal;
import com.example.fxsuite.life.body.bone.foot.SecondToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The second toe on its metatarsal's head: curling down as far as 40 degrees and up 60, a little to either side.
 */
public record SecondMetatarsophalangeal() implements Simple<SecondMetatarsal, SecondToeProximalPhalanx> {

    public static final SecondMetatarsophalangeal INSTANCE = new SecondMetatarsophalangeal();

    @Override
    public String name() {
        return "second metatarsophalangeal joint";
    }

    @Override
    public SecondMetatarsal from() {
        return SecondMetatarsal.INSTANCE;
    }

    @Override
    public Point<SecondMetatarsal> fromPoint() {
        return SecondMetatarsal.Points.HEAD;
    }

    @Override
    public SecondToeProximalPhalanx to() {
        return SecondToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<SecondToeProximalPhalanx> toPoint() {
        return SecondToeProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-60, 40), new Range(-10, 10));
    }
}
