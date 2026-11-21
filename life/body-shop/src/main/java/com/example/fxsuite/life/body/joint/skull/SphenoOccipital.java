package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Occipital;
import com.example.fxsuite.life.body.bone.skull.Sphenoid;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sphenoid's body on the occipital's base, grown into one: fast.
 */
public record SphenoOccipital() implements Simple<Occipital, Sphenoid> {

    public static final SphenoOccipital INSTANCE = new SphenoOccipital();

    @Override
    public String name() {
        return "spheno-occipital synchondrosis";
    }

    @Override
    public Occipital from() {
        return Occipital.INSTANCE;
    }

    @Override
    public Point<Occipital> fromPoint() {
        return Occipital.Points.SPHENOID;
    }

    @Override
    public Sphenoid to() {
        return Sphenoid.INSTANCE;
    }

    @Override
    public Point<Sphenoid> toPoint() {
        return Sphenoid.Points.OCCIPITAL;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
