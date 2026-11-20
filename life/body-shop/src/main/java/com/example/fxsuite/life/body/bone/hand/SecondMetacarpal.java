package com.example.fxsuite.life.body.bone.hand;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * The second metacarpal, in the hand's frame: from its base in the wrist to its head, the knuckle.
 */
public record SecondMetacarpal() implements Bone {

    public static final SecondMetacarpal INSTANCE = new SecondMetacarpal();

    @Override
    public String name() {
        return "second metacarpal";
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
        Fingers.INSTANCE.metacarpal(s, Digit.INDEX, 1 - 0.12 * female);
    }
}
