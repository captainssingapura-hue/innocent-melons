package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.FirstMetatarsal;
import com.example.fxsuite.life.body.bone.foot.GreatToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The great toe on its metatarsal's head: curling down as far as 40 degrees and up 70, a little to either side.
 */
public record FirstMetatarsophalangeal() implements Simple<FirstMetatarsal, GreatToeProximalPhalanx> {

    public static final FirstMetatarsophalangeal INSTANCE = new FirstMetatarsophalangeal();

    @Override
    public String name() {
        return "first metatarsophalangeal joint";
    }

    @Override
    public FirstMetatarsal from() {
        return FirstMetatarsal.INSTANCE;
    }

    @Override
    public Point<FirstMetatarsal> fromPoint() {
        return FirstMetatarsal.Points.HEAD;
    }

    @Override
    public GreatToeProximalPhalanx to() {
        return GreatToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<GreatToeProximalPhalanx> toPoint() {
        return GreatToeProximalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-70, 40), new Range(-10, 10));
    }
}
