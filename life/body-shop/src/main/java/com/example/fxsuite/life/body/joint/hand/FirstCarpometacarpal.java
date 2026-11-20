package com.example.fxsuite.life.body.joint.hand;

import com.example.fxsuite.life.body.bone.hand.FirstMetacarpal;
import com.example.fxsuite.life.body.bone.hand.Trapezium;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The thumb's metacarpal on the trapezium, a saddle: across the palm as far as 50 degrees and back 15, to the side 45
 * one way and 10 the other.
 */
public record FirstCarpometacarpal() implements Simple<Trapezium, FirstMetacarpal> {

    public static final FirstCarpometacarpal INSTANCE = new FirstCarpometacarpal();

    @Override
    public String name() {
        return "carpometacarpal joint of the thumb";
    }

    @Override
    public Trapezium from() {
        return Trapezium.INSTANCE;
    }

    @Override
    public Point<Trapezium> fromPoint() {
        return Trapezium.Points.FIRST;
    }

    @Override
    public FirstMetacarpal to() {
        return FirstMetacarpal.INSTANCE;
    }

    @Override
    public Point<FirstMetacarpal> toPoint() {
        return FirstMetacarpal.Points.BASE;
    }

    @Override
    public Motion motion() {
        return new Motion.Condyloid(new Range(-15, 50), new Range(-10, 45));
    }
}
