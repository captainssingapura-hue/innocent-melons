package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The middle phalanx of the middle finger, in the hand's frame, on from the joint before it, bent a little.
 */
public record MiddleFingerMiddlePhalanx() implements Bone {

    public static final MiddleFingerMiddlePhalanx INSTANCE = new MiddleFingerMiddlePhalanx();

    @Override
    public String name() {
        return "middle phalanx of the middle finger";
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
        Fingers.INSTANCE.phalanx(s, Digit.MIDDLE, 1, 1 - 0.12 * female);
    }
}
