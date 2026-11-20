package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The middle phalanx of the index finger, in the hand's frame, on from the joint before it, bent a little.
 */
public record IndexFingerMiddlePhalanx() implements Bone {

    public static final IndexFingerMiddlePhalanx INSTANCE = new IndexFingerMiddlePhalanx();

    @Override
    public String name() {
        return "middle phalanx of the index finger";
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
        Fingers.INSTANCE.phalanx(s, Digit.INDEX, 1, 1 - 0.12 * female);
    }
}
