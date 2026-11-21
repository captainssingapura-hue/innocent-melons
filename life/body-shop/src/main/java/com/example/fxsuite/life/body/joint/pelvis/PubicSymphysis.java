package com.example.fxsuite.life.body.joint.pelvis;

import com.example.fxsuite.life.body.bone.hip.HipBone;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Side;
import com.example.fxsuite.life.body.joint.Simple;

import java.util.List;

/**
 * The two hip bones meeting in front, on the disc of cartilage between them: held fast.
 */
public record PubicSymphysis() implements Simple<HipBone, HipBone> {

    public static final PubicSymphysis INSTANCE = new PubicSymphysis();

    @Override
    public String name() {
        return "pubic symphysis";
    }

    @Override
    public HipBone from() {
        return HipBone.INSTANCE;
    }

    @Override
    public Point<HipBone> fromPoint() {
        return HipBone.Points.SYMPHYSIS;
    }

    @Override
    public HipBone to() {
        return HipBone.INSTANCE;
    }

    @Override
    public Point<HipBone> toPoint() {
        return HipBone.Points.SYMPHYSIS;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }

    @Override
    public List<Ends> sides() {
        return List.of(new Ends(Side.RIGHT, Side.LEFT));
    }
}
