package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The proximal phalanx of the little toe, in the toes' frame, from the joint they turn at.
 */
public record LittleToeProximalPhalanx() implements Bone {

    public static final LittleToeProximalPhalanx INSTANCE = new LittleToeProximalPhalanx();

    @Override
    public String name() {
        return "proximal phalanx of the little toe";
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
        Toes.INSTANCE.phalanx(s, Toe.LITTLE, 0);
    }
}
