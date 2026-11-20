package com.example.fxsuite.life.body.joint.thorax;

import com.example.fxsuite.life.body.bone.thorax.CostalCartilages;
import com.example.fxsuite.life.body.bone.thorax.Sternum;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * One side's cartilages on the sternum, the first seven reaching it: held, here.
 */
public record Sternocostal() implements Simple<Sternum, CostalCartilages> {

    public static final Sternocostal INSTANCE = new Sternocostal();

    @Override
    public String name() {
        return "sternocostal joints";
    }

    @Override
    public Sternum from() {
        return Sternum.INSTANCE;
    }

    @Override
    public Point<Sternum> fromPoint() {
        return Sternum.Points.CARTILAGES;
    }

    @Override
    public CostalCartilages to() {
        return CostalCartilages.INSTANCE;
    }

    @Override
    public Point<CostalCartilages> toPoint() {
        return CostalCartilages.Points.STERNUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
