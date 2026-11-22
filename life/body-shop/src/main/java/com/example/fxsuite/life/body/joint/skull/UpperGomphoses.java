package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.Skull;
import com.example.fxsuite.life.body.bone.skull.UpperTeeth;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The upper teeth in their sockets in the maxillae: held fast.
 */
public record UpperGomphoses() implements Simple<Skull, UpperTeeth> {

    public static final UpperGomphoses INSTANCE = new UpperGomphoses();

    @Override
    public String name() {
        return "gomphoses of the upper teeth";
    }

    @Override
    public Skull from() {
        return Skull.INSTANCE;
    }

    @Override
    public Point<Skull> fromPoint() {
        return Skull.Points.TEETH;
    }

    @Override
    public UpperTeeth to() {
        return UpperTeeth.INSTANCE;
    }

    @Override
    public Point<UpperTeeth> toPoint() {
        return UpperTeeth.Points.MAXILLA;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
