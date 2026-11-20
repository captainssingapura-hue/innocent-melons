package com.example.fxsuite.life.body.joint.pelvis;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The sacrum's wing on the ilium, one a side: held fast by its ligaments, nodding a little.
 */
public record Sacroiliac() implements Simple<Sacrum, HipBone> {

    public static final Sacroiliac INSTANCE = new Sacroiliac();

    @Override
    public String name() {
        return "sacroiliac joint";
    }

    @Override
    public Sacrum from() {
        return Sacrum.INSTANCE;
    }

    @Override
    public Point<Sacrum> fromPoint() {
        return Sacrum.Points.ILIUM;
    }

    @Override
    public HipBone to() {
        return HipBone.INSTANCE;
    }

    @Override
    public Point<HipBone> toPoint() {
        return HipBone.Points.SACRUM;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-2, 2));
    }
}
