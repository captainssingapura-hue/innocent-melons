package com.example.fxsuite.life.body.joint.foot;

import com.example.fxsuite.life.body.bone.foot.GreatToeDistalPhalanx;
import com.example.fxsuite.life.body.bone.foot.GreatToeProximalPhalanx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The great toe's two phalanges: curling down as far as 80 degrees.
 */
public record GreatToeInterphalangeal() implements Simple<GreatToeProximalPhalanx, GreatToeDistalPhalanx> {

    public static final GreatToeInterphalangeal INSTANCE = new GreatToeInterphalangeal();

    @Override
    public String name() {
        return "interphalangeal joint of the great toe";
    }

    @Override
    public GreatToeProximalPhalanx from() {
        return GreatToeProximalPhalanx.INSTANCE;
    }

    @Override
    public Point<GreatToeProximalPhalanx> fromPoint() {
        return GreatToeProximalPhalanx.Points.HEAD;
    }

    @Override
    public GreatToeDistalPhalanx to() {
        return GreatToeDistalPhalanx.INSTANCE;
    }

    @Override
    public Point<GreatToeDistalPhalanx> toPoint() {
        return GreatToeDistalPhalanx.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(0, 80));
    }
}
