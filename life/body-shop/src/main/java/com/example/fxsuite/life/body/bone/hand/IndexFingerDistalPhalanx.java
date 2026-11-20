package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The distal phalanx of the index finger, in the hand's frame, on from the joint before it, bent a little.
 */
public record IndexFingerDistalPhalanx() implements Bone {

    public static final IndexFingerDistalPhalanx INSTANCE = new IndexFingerDistalPhalanx();

    @Override
    public String name() {
        return "distal phalanx of the index finger";
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
        Fingers.INSTANCE.phalanx(s, Digit.INDEX, 2, 1 - 0.12 * female);
    }
}
