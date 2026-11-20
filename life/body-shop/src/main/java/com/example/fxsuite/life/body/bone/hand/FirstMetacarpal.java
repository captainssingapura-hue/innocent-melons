package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The first metacarpal, in the hand's frame: from its base in the wrist to its head, the knuckle.
 */
public record FirstMetacarpal() implements Bone {

    public static final FirstMetacarpal INSTANCE = new FirstMetacarpal();

    @Override
    public String name() {
        return "first metacarpal";
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
        return 0.1;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Fingers.INSTANCE.metacarpal(s, Digit.THUMB, 1 - 0.12 * female);
    }
}
