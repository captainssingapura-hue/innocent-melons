package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.ThirdMetatarsal;
import com.example.fxsuite.life.body.bone.foot.ThirdToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The third toe on its metatarsal's head: curling down as far as 40 degrees and up 60, a little to either side.
 */
public record ThirdMetatarsophalangeal() implements Simple<ThirdMetatarsal, ThirdToeProximalPhalanx> {

    public static final ThirdMetatarsophalangeal INSTANCE = new ThirdMetatarsophalangeal();

    @Override
    public String name() {
        return "third metatarsophalangeal joint";
    }

    @Override
    public ThirdMetatarsal from() {
        return ThirdMetatarsal.INSTANCE;
    }

    @Override
    public Point<ThirdMetatarsal> fromPoint() {
        return ThirdMetatarsal.Points.HEAD;
    }

    @Override
    public ThirdToeProximalPhalanx to() {
        return ThirdToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<ThirdToeProximalPhalanx> toPoint() {
        return ThirdToeProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-60, 40), new Range(-10, 10));
    }
}
