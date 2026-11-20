package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The distal phalanx of the fourth toe, in the toes' frame, from the joint they turn at.
 */
public record FourthToeDistalPhalanx() implements Bone {

    public static final FourthToeDistalPhalanx INSTANCE = new FourthToeDistalPhalanx();

    @Override
    public String name() {
        return "distal phalanx of the fourth toe";
    }

    @Override
    public Region region() {
        return Region.FOOT;
    }

    @Override
    public Part part() {
        return Part.RIGHT_TOES;
    }

    @Override
    public double coarsest() {
        return 0.1;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Toes.INSTANCE.phalanx(s, Toe.FOURTH, 2);
    }
}
