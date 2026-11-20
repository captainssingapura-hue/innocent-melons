package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The proximal phalanx of the little finger, in the hand's frame, on from the joint before it, bent a little.
 */
public record LittleFingerProximalPhalanx() implements Bone {

    public static final LittleFingerProximalPhalanx INSTANCE = new LittleFingerProximalPhalanx();

    @Override
    public String name() {
        return "proximal phalanx of the little finger";
    }

    @Override
    public Region region() {
        return Region.HAND;
    }

    @Override
    public Part part() {
        return Part.RIGHT_HAND;
    }

    @Override
    public double coarsest() {
        return 0.08;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Fingers.INSTANCE.phalanx(s, Digit.LITTLE, 0, 1 - 0.12 * female);
    }
}
