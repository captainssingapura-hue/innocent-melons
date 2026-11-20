package com.example.fxsuite.life.body.joint.skull;

import com.example.fxsuite.life.body.bone.skull.LowerTeeth;
import com.example.fxsuite.life.body.bone.skull.Mandible;
import com.example.fxsuite.life.body.joint.Motion;
import com.example.fxsuite.life.body.joint.Point;
import com.example.fxsuite.life.body.joint.Simple;

/**
 * The lower teeth in their sockets in the mandible: held fast.
 */
public record LowerGomphoses() implements Simple<Mandible, LowerTeeth> {

    public static final LowerGomphoses INSTANCE = new LowerGomphoses();

    @Override
    public String name() {
        return "gomphoses of the lower teeth";
    }

    @Override
    public Mandible from() {
        return Mandible.INSTANCE;
    }

    @Override
    public Point<Mandible> fromPoint() {
        return Mandible.Points.TEETH;
    }

    @Override
    public LowerTeeth to() {
        return LowerTeeth.INSTANCE;
    }

    @Override
    public Point<LowerTeeth> toPoint() {
        return LowerTeeth.Points.MANDIBLE;
    }

    @Override
    public Motion motion() {
        return new Motion.Fixed();
    }
}
