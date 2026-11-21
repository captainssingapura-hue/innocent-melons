package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.Axis;
import com.example.fxsuite.life.body.bone.spine.Atlas;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The atlas turning on the axis, round its dens: as far as the head turns on the neck, most of it here; a little
 * bending each other way.
 */
public record AtlantoAxial() implements Simple<Axis, Atlas> {

    public static final AtlantoAxial INSTANCE = new AtlantoAxial();

    @Override
    public String name() {
        return "atlanto-axial joint";
    }

    @Override
    public Axis from() {
        return Axis.INSTANCE;
    }

    @Override
    public Point<Axis> fromPoint() {
        return Axis.Points.ABOVE;
    }

    @Override
    public Atlas to() {
        return Atlas.INSTANCE;
    }

    @Override
    public Point<Atlas> toPoint() {
        return Atlas.Points.BELOW;
    }

    @Override
    public Motion motion() {
        return new Motion.Disc(new Range(-5, 5), new Range(-3, 3), new Range(-40, 40));
    }
}
