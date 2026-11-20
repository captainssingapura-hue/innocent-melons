package com.example.fxsuite.life.body.joint.spine;

import com.example.fxsuite.life.body.bone.spine.Sacrum;
import com.example.fxsuite.life.body.bone.spine.Coccyx;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Range;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The coccyx on the sacrum's apex, bending a little forward and back.
 */
public record Sacrococcygeal() implements Simple<Sacrum, Coccyx> {

    public static final Sacrococcygeal INSTANCE = new Sacrococcygeal();

    @Override
    public String name() {
        return "sacrococcygeal joint";
    }

    @Override
    public Sacrum from() {
        return Sacrum.INSTANCE;
    }

    @Override
    public Point<Sacrum> fromPoint() {
        return Sacrum.Points.BELOW;
    }

    @Override
    public Coccyx to() {
        return Coccyx.INSTANCE;
    }

    @Override
    public Point<Coccyx> toPoint() {
        return Coccyx.Points.ABOVE;
    }

    @Override
    public Motion motion() {
        return new Motion.Hinge(new Range(-5, 5));
    }
}
